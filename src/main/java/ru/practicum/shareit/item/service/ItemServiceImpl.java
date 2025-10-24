package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           CommentRepository commentRepository,
                           BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemDto> getItemsByOwner(Long ownerId) {
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
    public Collection<ItemDto> getItemsBySearchQuery(String text) {
        log.info("Запрос на поиск вещи с текстом = {}", text);

        Collection<Item> items = new ArrayList<>();
        if (!text.isBlank()) {
            items = itemRepository.findItemsBySearchQuery(text.toLowerCase());
        }

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
    public ItemDto getItemById(Long id) {
        log.info("Запрос на получение вещи с ID={}", id);

        Item item = itemRepository.findById(id).orElseThrow(() -> {
            log.warn("Вещь с id = {} не найдена", id);
            return new NotFoundException("Вещь не найдена");
        });

        log.info("Вещь с id = {} успешно найдена", id);
        return ItemMapper.toItemDto(
                item,
                getComments(item.getId()),
                getLastBooking(item.getId()) != null ? BookingMapper.toBookingShortDto(getLastBooking(item.getId())) : null,
                getNextBooking(item.getId()) != null ? BookingMapper.toBookingShortDto(getNextBooking(item.getId())) : null
        );
    }

    @Transactional
    @Override
    public ItemShortDto create(ItemShortDto item, Long ownerId) {
        log.info("Запрос на добавление вещи владельцем с Id = {}", item.getOwnerId());

        if (!userRepository.existsById(ownerId)) {
            log.warn("Пользователь с id = {} не найден", ownerId);
            throw  new NotFoundException("Пользователь не найден");
        }

        Item createItem = itemRepository.save(ItemMapper.toItem(item, ownerId));

        log.info("Вещь с id = {} владельца с id = {} успешно добавлена", createItem.getId(), createItem.getOwnerId());
        return ItemMapper.toItemShortDto(createItem);
    }

    @Transactional
    @Override
    public ItemShortDto update(ItemShortDto item, Long itemId, Long ownerId) {
        log.info("Запрос на обновление вещи с id = {}", item.getId());

        User user = userRepository.findById(ownerId).orElseThrow(() -> {
            log.warn("Пользователь с id = {} не найден", item.getOwnerId());
            return new NotFoundException("Пользователь не найден");
        });

        Item oldItem = itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("Вещь с id = {} не найдена", item.getId());
            return new NotFoundException("Вещь не найдена");
        });

        if (!oldItem.getOwnerId().equals(user.getId())) {
            log.warn("Вещь с id = {} не принадлежит пользователю с id = {}", item.getId(), item.getOwnerId());
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

        if (!oldItem.getOwnerId().equals(user.getId())) {
            log.warn("Вещь с id = {} не принадлежит пользователю с id = {}", itemId, ownerId);
            throw new ValidationException("Вещь не принадлежит пользователю");
        }

        itemRepository.deleteById(itemId);
        log.info("Вещь с id = {} успешно удалена", itemId);
    }

    @Transactional
    @Override
    public CommentResponseDto create(CommentRequestDto commentRequestDto, Long itemId, Long authorId) {
        log.info("Получен запрос на создание комментария для вещи с id = {}", itemId);

        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.warn("Вещь с id = {} не найдена", itemId);
            return new NotFoundException("Вещь не найдена");
        });

        User author = userRepository.findById(authorId).orElseThrow(() -> {
            log.warn("Пользователь с id = {} не найден", authorId);
            return new NotFoundException("Пользователь не найден");
        });

        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        if (!bookingRepository.hasUserCompletedBooking(item.getId(), author.getId(), now)) {
            throw new IllegalStateException("Пользователь не арендовал эту вещь или бронирование ещё не завершено");
        }

        Comment comment = Comment.builder()
                .text(commentRequestDto.getText())
                .itemId(item.getId())
                .authorId(author.getId())
                .created(now.toLocalDateTime())
                .build();

        CommentResponseDto createComment = CommentMapper.toCommentResponseDto(commentRepository.save(comment), author);

        log.info("Комментарий с id = {} создан успешно", createComment.getId());
        return createComment;
    }

    private Collection<CommentResponseDto> getComments(Long itemId) {
        log.info("Получен запрос на все комментарии вещи с id = {}", itemId);

        if (!itemRepository.existsById(itemId)) {
            log.warn("Вещь с id = {} не найдена", itemId);
            throw new NotFoundException("Вещь не найдена");
        }

        Collection<Comment> comments = commentRepository.findAllByItemId(itemId);

        return comments.stream()
                .map(comment -> {
                    User author = userRepository.findById(comment.getAuthorId()).orElseThrow(() -> {
                        log.warn("Пользователь с id = {} не найден", comment.getAuthorId());
                        return new NotFoundException("Пользователь не найден");
                    });
                    return CommentMapper.toCommentResponseDto(comment, author);
                }).collect(Collectors.toList());
    }

    private Booking getLastBooking(Long itemId) {
        return bookingRepository.findLastBooking(itemId, LocalDateTime.now())
                .orElse(null);
    }

    private Booking getNextBooking(Long itemId) {
        return bookingRepository.findNextBooking(itemId, LocalDateTime.now())
                .orElse(null);
    }


}
