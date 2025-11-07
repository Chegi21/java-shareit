package ru.practicum.item.dto;

import lombok.*;
import ru.practicum.booking.dto.BookingShortDto;

import java.util.Collection;

@Data
@Builder
public class ItemFullDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long ownerId;

    private Long requestId;

    private BookingShortDto lastBooking;

    private BookingShortDto nextBooking;

    private Collection<CommentResponseDto> comments;
}
