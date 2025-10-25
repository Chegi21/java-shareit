package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;

public interface BookingService {
    Collection<BookingResponseDto> findAllBookingsUser(String state, Long userId);

    Collection<BookingResponseDto> findAllBookingsOwner(String state, Long ownerId);

    BookingResponseDto findById(Long bookingId, Long userId);

    BookingResponseDto create(Booking booking);

    BookingResponseDto update(Long bookingId, Long userId, boolean approved);
}
