package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
@Builder
public class ItemDto {
    @PositiveOrZero(message = "Id не может отрицательным числом")
    private Long id;

    @NotBlank(message = "Название вещи не может быть пустым")
    private String name;

    @NotBlank(message = "Описание вещи не может быть пустым")
    private String description;

    @NotNull
    private Boolean isAvailable;

    @PositiveOrZero(message = "Id не может отрицательным числом")
    private Long ownerId;

    @PositiveOrZero(message = "Id не может отрицательным числом")
    private Long requestId;
}
