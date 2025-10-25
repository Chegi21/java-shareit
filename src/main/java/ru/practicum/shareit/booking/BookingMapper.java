package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static Booking toBooking(BookingRequestDto booking, Long bookerId) {
        Status status = booking.getStatus() != null ? booking.getStatus() : Status.WAITING;
        return Booking.builder()
                .startDate(booking.getStart())
                .endDate(booking.getEnd())
                .itemId(booking.getItemId())
                .bookerId(bookerId)
                .status(status)
                .build();
    }


    public static BookingOutDto toBookingOutDto(Booking booking, User user, Item item) {
        return BookingOutDto.builder()
                .id(booking.getId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .item(item)
                .booker(user)
                .status(booking.getStatus())
                .build();
    }

    public static BookingResponseDto toBookingShortDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBookerId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .build();
    }

}
