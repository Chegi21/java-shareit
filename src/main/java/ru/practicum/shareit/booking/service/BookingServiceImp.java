package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IllegalAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingServiceImp implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public BookingServiceImp(UserRepository userRepository, ItemRepository itemRepository, BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<BookingOutDto> findAllBookingsUser(String state, Long userId) {
        if (!userRepository.existsById(userId)) {
            log.warn("Пользователя с id = {} не существует", userId);
            throw new NotFoundException("Пользователя не существует");
        }

        BookingState bookingState = BookingState.from(state);
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        Collection<Booking> bookings = switch (bookingState) {
            case CURRENT -> bookingRepository.findCurrentByBookerId(userId, now);
            case PAST -> bookingRepository.findPastByBookerId(userId, now);
            case FUTURE -> bookingRepository.findFutureByBookerId(userId, now);
            case WAITING -> bookingRepository.findByBookerIdAndWaitingStatus(userId);
            case REJECTED -> bookingRepository.findByBookerIdAndRejectStatus(userId);
            default -> bookingRepository.findAllByBookerId(userId);
        };

        Collection<BookingOutDto> bookingOutDtoCollection = bookings.stream()
                .map(booking -> {
                    User booker = userRepository.findById(booking.getBookerId())
                            .orElseThrow(() -> {
                                log.warn("Пользователь с id = {} не существует", booking.getBookerId());
                                return new NotFoundException("Пользователь не существует");
                            });

                    Item item = itemRepository.findById(booking.getItemId())
                            .orElseThrow(() -> {
                                log.warn("Вещи для брони с id = {} не существует", booking.getItemId());
                                return new NotFoundException("Вещи для брони не существует");
                            });

                    return BookingMapper.toBookingOutDto(booking, booker, item);
                })
                .collect(Collectors.toList());

        log.info("Найдено {} бронирований для пользователя id = {} со статусом {}", bookings.size(), userId, state);
        return bookingOutDtoCollection;
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<BookingOutDto> findAllBookingsOwner(String state, Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            log.warn("Пользователя с id = {} не существует", ownerId);
            throw new NotFoundException("Пользователя не существует");
        }

        BookingState bookingState = BookingState.from(state);
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        Collection<Booking> bookings = switch (bookingState) {
            case CURRENT -> bookingRepository.findCurrentByOwnerId(ownerId, now);
            case PAST -> bookingRepository.findPastByOwnerId(ownerId, now);
            case FUTURE -> bookingRepository.findFutureByOwnerId(ownerId, now);
            case WAITING -> bookingRepository.findWaitingByOwnerId(ownerId);
            case REJECTED -> bookingRepository.findRejectedByOwnerId(ownerId);
            default -> bookingRepository.findAllByOwnerId(ownerId);
        };

        Collection<BookingOutDto> bookingOutDtoCollection = bookings.stream()
                .map(booking -> {
                    User booker = userRepository.findById(booking.getBookerId())
                            .orElseThrow(() -> {
                                log.warn("Пользователь с id = {} не существует", booking.getBookerId());
                                return new NotFoundException("Пользователь не существует");
                            });

                    Item item = itemRepository.findById(booking.getItemId())
                            .orElseThrow(() -> {
                                log.warn("Вещи для брони с id = {} не существует", booking.getItemId());
                                return new NotFoundException("Вещи для брони не существует");
                            });

                    return BookingMapper.toBookingOutDto(booking, booker, item);
                })
                .collect(Collectors.toList());

        log.info("Найдено {} бронирований для пользователя id = {} со статусом {}", bookings.size(), ownerId, state);
        return bookingOutDtoCollection;
    }

    @Transactional(readOnly = true)
    @Override
    public BookingOutDto findById(Long bookingId, Long userId) {
        log.info("Запрос на получение брони с id = {} от пользователя с id = {}", bookingId, userId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.warn("Бронь с id = {} не найдена", bookingId);
            return new NotFoundException("Бронь не найден");
        });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь с id = {} не существует", userId);
                    return new NotFoundException("Пользователь не существует");
                });

        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> {
                    log.warn("Вещи для брони с id = {} не существует", booking.getItemId());
                    return new NotFoundException("Вещи для брони не существует");
                });

        if (!booking.getBookerId().equals(user.getId()) || item.getOwnerId().equals(user.getId())) {
            log.warn("Пользователь с id = {} не имеет доступа к бронированию c id = {}", userId, bookingId);
            throw new IllegalAccessException("Пользователь не имеет доступа к бронированию");
        }

        log.info("Бронь с id = {} успешно найдена", bookingId);
        return BookingOutDto.builder()
                .id(booking.getId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .booker(user)
                .item(item)
                .status(booking.getStatus())
                .build();
    }

    @Transactional
    @Override
    public BookingOutDto create(Booking booking) {
        log.info("Запрос на создание брони для вещи с id = {} пользователем с id = {}", booking.getItemId(), booking.getBookerId());

        User booker = userRepository.findById(booking.getBookerId())
                .orElseThrow(() -> {
                    log.warn("Пользователя с id = {} не существует", booking.getBookerId());
                    return new NotFoundException("Пользователя нет в системе");
                });

        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> {
                    log.warn("Вещи для бронирования с id = {} не существует", booking.getItemId());
                    return new NotFoundException("Вещи нет в системе");
                });

        if (!item.getAvailable()) {
            log.warn("Вещь с id = {} не доступна для бронирования", item.getId());
            throw new IllegalAccessException("Вещь не доступна для бронирования");
        }

        if (item.getOwnerId().equals(booker.getId())) {
            log.warn("Владелец с id = {} не может бронировать свою вещь с id = {}", booker.getId(), item.getId());
            throw new IllegalAccessException("Владелец не может бронировать свою вещь");
        }

        if (booking.getStartDate().isAfter(booking.getEndDate()) || booking.getStartDate().isEqual(booking.getEndDate())) {
            log.warn("Некорректное время бронирования");
            throw new ValidationException("Некорректное время бронирования");
        }

        Booking createBooking = bookingRepository.save(booking);

        log.info("Бронь для вещи с id = {} пользователем с id = {} успешно создана", createBooking.getItemId(), createBooking.getBookerId());
        return BookingOutDto.builder()
                .id(createBooking.getId())
                .start(createBooking.getStartDate())
                .end(createBooking.getEndDate())
                .item(item)
                .booker(booker)
                .status(createBooking.getStatus())
                .build();
    }

    @Transactional
    @Override
    public BookingOutDto update(Long bookingId, Long userId, boolean approved) {
        log.info("Запрос на обновление брони для вещи с id = {} пользователем с id = {}", bookingId, userId);

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Владельца вещи с id = {} не существует", bookingId);
                    return new IllegalAccessException("Владельца вещи не существует");
                });

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.warn("Брони с id = {} не существует", bookingId);
            return new NotFoundException("Брони не существует");
        });

        Item item = itemRepository.findById(booking.getItemId())
                .orElseThrow(() -> {
                    log.warn("Вещи для обновления брони с id = {} не существует", bookingId);
                    return new NotFoundException("Вещи не существует");
                });

        User booker = userRepository.findById(booking.getBookerId()).orElseThrow(() -> {
            log.warn("Автора брони с id = {} не существует", bookingId);
            return new NotFoundException("Автора брони не существует");
        });

        if (!item.getOwnerId().equals(owner.getId())) {
            log.warn("Пользователь id = {} не является владельцем вещи id = {}", userId, booking.getItemId());
            throw new IllegalAccessException("Пользователь не является владельцем вещи");
        }

        if (booking.getStatus() == Status.APPROVED && approved) {
            throw new ValidationException("Бронирование уже одобрено");
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        Booking updated = bookingRepository.save(booking);

        log.info("Статус брони вещи с id = {} изменён на {}", booking.getItemId(), booking.getStatus());
        return BookingOutDto.builder()
                .id(updated.getId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .booker(booker)
                .item(item)
                .status(updated.getStatus())
                .build();
    }
}
