package ru.practicum.booking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;
import ru.practicum.booking.model.Status;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    @NotNull(message = "Дата начала бронирования не может быть null")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования не может быть null")
    private LocalDateTime end;

    @NotNull(message = "Id бронируемой вещи не может быть null")
    @PositiveOrZero(message = "Id не может отрицательным числом")
    private Long itemId;

    @PositiveOrZero(message = "Id арендатора не может быть отрицательным числом")
    private Long bookerId;

    private Status status;
}
