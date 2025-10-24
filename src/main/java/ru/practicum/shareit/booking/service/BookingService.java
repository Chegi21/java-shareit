package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;

public interface BookingService {
    Collection<BookingOutDto> findAllBookingsUser(String state, Long userId);

    Collection<BookingOutDto> findAllBookingsOwner(String state, Long ownerId);

    BookingOutDto findById(Long bookingId, Long userId);

    BookingOutDto create(Booking booking);

    BookingOutDto update(Long bookingId, Long userId, boolean approved);
}
