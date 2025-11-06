package ru.practicum.booking.mapper;

import ru.practicum.booking.dto.BookingRequestDto;
import ru.practicum.booking.dto.BookingResponseDto;
import ru.practicum.booking.dto.BookingShortDto;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.Status;
import ru.practicum.item.mapper.ItemMapper;
import ru.practicum.item.model.Item;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

public class BookingMapper {
    public static Booking toBooking(BookingRequestDto booking, Item item, User booker) {
        Status status = booking.getStatus() != null ? booking.getStatus() : Status.WAITING;
        return new Booking(booking.getStart(), booking.getEnd(), item, booker, status);
    }

    public static BookingResponseDto toBookingResponseDto(Booking booking, Item item, User user) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .item(ItemMapper.toItemShortDto(item))
                .booker(UserMapper.toUserDto(user))
                .status(booking.getStatus())
                .build();
    }

    public static BookingShortDto toBookingShortDto(Booking booking) {
        return BookingShortDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .build();
    }

}
