package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {
    public static Booking toBooking(BookingDto booking) {
        return Booking.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItemId())
                .bookerId(booking.getBookerId())
                .status(booking.getStatus())
                .build();
    }

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItemId())
                .bookerId(booking.getBookerId())
                .status(booking.getStatus())
                .build();
    }
}
