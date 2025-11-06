package ru.practicum.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.dto.BookingShortDto;
import ru.practicum.booking.mapper.BookingMapper;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.repository.BookingRepository;
import ru.practicum.exception.NotAccessException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.item.dto.CommentRequestDto;
import ru.practicum.item.dto.CommentResponseDto;
import ru.practicum.item.dto.ItemFullDto;
import ru.practicum.item.dto.ItemShortDto;
import ru.practicum.item.mapper.CommentMapper;
import ru.practicum.item.mapper.ItemMapper;
import ru.practicum.item.model.Comment;
import ru.practicum.item.model.Item;
import ru.practicum.item.repository.CommentRepository;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.request.repository.ItemRequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           CommentRepository commentRepository,
                           BookingRepository bookingRepository,
                           ItemRequestRepository itemRequestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemFullDto> getItemsByOwner(Long ownerId) {
        log.info("Запрос на список вещей пользователя с id = {}", ownerId);

        if (!userRepository.existsById(ownerId)) {
            log.warn("Пользователь с id = {} не найден", ownerId);
            throw  new NotFoundException("Пользователь не найден");
        }

        Collection<Item> items = itemRepository.findAllByOwnerId(ownerId);

        log.info("Найден список в количестве {} штук", items.size());
        return items.stream()
                .map(item -> {
                    Booking lastBooking = getLastBooking(item.getId());
                    Booking nextBooking = getNextBooking(item.getId());
                    Collection<CommentResponseDto> comments = getComments(item.getId());

                    return ItemMapper.toItemDto(
                            item,
                            comments,
                            lastBooking != null ? BookingMapper.toBookingShortDto(lastBooking) : null,
                            nextBooking != null ? BookingMapper.toBookingShortDto(nextBooking) : null
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemFullDto> getItemsBySearchQuery(String text) {
        log.info("Запрос на поиск вещи с текстом = {}", text);

        Collection<Item> items = new ArrayList<>();
        if (!text.isBlank()) {
            items = itemRepository.findItemsBySearchQuery(text.toLowerCase());
        }

        log.info("Найден список вещей в количестве {} штук", items.size());
        return items.stream()
                .map(item -> {
                    Booking lastBooking = getLastBooking(item.getId());
                    Booking nextBooking = getNextBooking(item.getId());
                    Collection<CommentResponseDto> comments = getComments(item.getId());

                    return ItemMapper.toItemDto(
                            item,
                            comments,
                            lastBooking != null ? BookingMapper.toBookingShortDto(lastBooking) : null,
                            nextBooking != null ? BookingMapper.toBookingShortDto(nextBooking) : null
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ItemFullDto getItemById(Long itemId, Long ownerId) {
        log.info("Запрос на получение вещи с id = {}", itemId);

        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("Вещь с id = {} не найдена", itemId);
            return new NotFoundException("Вещь не найдена");
        });

        BookingShortDto lastBookingShortDto = null;
        BookingShortDto nextBookingShortDto = null;

        if (item.getOwner().getId().equals(ownerId)) {
            Booking lastBooking = getLastBooking(item.getId());
            Booking nextBooking = getNextBooking(item.getId());

            if (lastBooking != null) {
                lastBookingShortDto = BookingMapper.toBookingShortDto(lastBooking);
            }
            if (nextBooking != null) {
                nextBookingShortDto = BookingMapper.toBookingShortDto(nextBooking);
            }
        }

        log.info("Вещь с id = {} успешно найдена", itemId);
        return ItemMapper.toItemDto(
                item,
                getComments(item.getId()),
                lastBookingShortDto,
                nextBookingShortDto
        );
    }

    @Transactional
    @Override
    public ItemShortDto create(ItemShortDto item, Long ownerId) {
        log.info("Запрос на добавление вещи владельцем с Id = {}", ownerId);

        User owner = userRepository.findById(ownerId).orElseThrow(() -> {
            log.warn("Пользователь с id = {} не найден", ownerId);
            return new NotFoundException("Пользователь не найден");
        });

        ItemRequest request = null;
        if (item.getRequestId() != null) {
            request = itemRequestRepository.findById(item.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Request not found"));
        }

        Item createItem = itemRepository.save(ItemMapper.toItem(item, owner, request));

        log.info("Вещь с id = {} пользователем с id = {} успешно добавлена", createItem.getId(), createItem.getOwner().getId());
        return ItemMapper.toItemShortDto(createItem);
    }

    @Transactional
    @Override
    public ItemShortDto update(ItemShortDto item, Long itemId, Long ownerId) {
        log.info("Запрос на обновление вещи с id = {}", item.getId());

        if (!userRepository.existsById(ownerId)) {
            log.warn("Пользователь с id = {} не найден", ownerId);
            throw  new NotFoundException("Пользователь не найден");
        }

        Item oldItem = itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("Вещь с id = {} не найдена", item.getId());
            return new NotFoundException("Вещь не найдена");
        });

        if (!oldItem.getOwner().getId().equals(ownerId)) {
            log.warn("Вещь с id = {} не принадлежит пользователю с id = {}", oldItem.getId(), oldItem.getOwner().getId());
            throw new ValidationException("Вещь не принадлежит пользователю");
        }

        Optional.ofNullable(item.getName()).ifPresent(oldItem::setName);
        Optional.ofNullable(item.getDescription()).ifPresent(oldItem::setDescription);
        Optional.ofNullable(item.getAvailable()).ifPresent(oldItem::setAvailable);

        Item updateItem = itemRepository.save(oldItem);

        log.info("Вещь с id = {} успешно обновлена", updateItem.getId());
        return ItemMapper.toItemShortDto(updateItem);
    }

    @Transactional
    @Override
    public void delete(Long itemId, Long ownerId) {
        log.info("Запрос на удаление вещи с id = {}", itemId);

        User user = userRepository.findById(ownerId).orElseThrow(() -> {
            log.warn("Пользователь с id = {} не найден", ownerId);
            return new NotFoundException("Пользователь не найден");
        });

        Item oldItem = itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("Вещь с id = {} не найдена", itemId);
            return new NotFoundException("Вещь не найдена");
        });

        if (!oldItem.getOwner().getId().equals(user.getId())) {
            log.warn("Вещь с id = {} не принадлежит пользователю с id = {}", itemId, ownerId);
            throw new ValidationException("Вещь не принадлежит пользователю");
        }

        itemRepository.deleteById(itemId);
        log.info("Вещь с id = {} успешно удалена", itemId);
    }

    @Transactional
    @Override
    public CommentResponseDto create(CommentRequestDto comment, Long itemId, Long userId) {
        log.info("Получен запрос на создание комментария для вещи с id = {}, пользователем с id = {}", itemId, userId);

        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("Вещь с id = {} не найдена", itemId);
            return new NotFoundException("Вещь не найдена");
        });

        User booker = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id = {} не найден", userId);
            return new NotFoundException("Пользователь не найден");
        });

        if (item.getOwner().getId().equals(userId)) {
            log.warn("Пользователь с id = {} является владельцем вещи с id = {} и не может оставить комментарий", userId, itemId);
            throw new NotAccessException("Владелец не может оставлять комментарии к своей вещи");
        }

        if (!bookingRepository.hasUserCompletedBooking(item.getId(), booker.getId())) {
            log.warn("У вещи с id = {} бронь еще не завершена или пользователь не бронировал эту вещь", item.getId());
            throw new NotAccessException("У вещь бронь ещё не завершена или пользователь не бронировал эту вещь");
        }

        Comment createdComment = commentRepository.save(CommentMapper.toComment(comment, item, booker));

        log.info("Комментарий с id = {} успешно создан пользователем id = {}", createdComment.getId(), booker.getId());
        return CommentMapper.toCommentResponseDto(createdComment);
    }

    private Collection<CommentResponseDto> getComments(Long itemId) {
        log.info("Получен запрос на все комментарии вещи с id = {}", itemId);

        if (!itemRepository.existsById(itemId)) {
            log.warn("Вещь с id = {} не найдена", itemId);
            throw new NotFoundException("Вещь не найдена");
        }

        Collection<Comment> comments = commentRepository.findAllByItemId(itemId);

        Collection<CommentResponseDto> responseDtoCollection = comments.stream()
                .map(CommentMapper::toCommentResponseDto).collect(Collectors.toList());

        log.info("Найдено комментариев в количестве {}", responseDtoCollection.size());
        return responseDtoCollection;
    }

    private Booking getLastBooking(Long itemId) {
        return bookingRepository.findLastBooking(itemId).orElse(null);
    }

    private Booking getNextBooking(Long itemId) {
        return bookingRepository.findNextBooking(itemId).orElse(null);
    }
}
