package ru.practicum.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.booking.model.Status;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingRequestDto {
    private LocalDateTime start;

    private LocalDateTime end;

    private Long itemId;

    private Long bookerId;

    private Status status;
}
