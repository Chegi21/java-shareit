package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingResponseDto {
    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private ItemShortDto item;

    private UserDto booker;

    private Status status;
}
