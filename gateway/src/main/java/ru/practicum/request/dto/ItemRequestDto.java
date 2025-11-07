package ru.practicum.request.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;
import ru.practicum.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder
public class ItemRequestDto {
    @NotBlank
    private String description;

    @PositiveOrZero(message = "Id пользователя запроса не может быть отрицательным числом")
    private Long requesterId;

    @FutureOrPresent(message = "Дата запроса не может быть в прошлом")
    private LocalDateTime created;

    private Collection<ItemDto> items;
}
