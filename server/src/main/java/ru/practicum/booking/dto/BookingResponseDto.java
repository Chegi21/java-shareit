package ru.practicum.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.booking.model.Status;
import ru.practicum.item.dto.ItemShortDto;
import ru.practicum.user.dto.UserDto;

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
