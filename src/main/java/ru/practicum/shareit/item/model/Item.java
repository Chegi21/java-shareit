package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = {"id"})
public class Item {
    @PositiveOrZero(message = "Id вещи не может быть отрицательным числом")
    private Long id;

    @NotBlank(message = "Название вещи не может быть пустым")
    private String name;

    @NotBlank(message = "Описание вещи не может быть пустым")
    private String description;

    @NotNull
    private Boolean available;

    @PositiveOrZero(message = "Id владельца не может быть отрицательным числом")
    private Long ownerId;

    @PositiveOrZero(message = "Id запроса не может быть отрицательным числом")
    private Long requestId;
}
