package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    @PositiveOrZero(message = "Id брони не может быть отрицательным числом")
    private Long id;

    @NotNull(message = "Дата начала бронирования не может быть null")
    @FutureOrPresent(message = "Дата начала бронирования не может быть в прошлом")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования не может быть null")
    @FutureOrPresent(message = "Дата окончания бронирования не может быть в прошлом")
    private LocalDateTime end;

    @NotNull(message = "Id бронируемой вещи не может быть null")
    @PositiveOrZero(message = "Id не может отрицательным числом")
    private Long itemId;

    @NotNull(message = "Id арендатора не может быть null")
    @PositiveOrZero(message = "Id арендатора не может быть отрицательным числом")
    private Long bookerId;

    @NotNull(message = "Статус брони не может быть null")
    private String status;
}
