package ru.practicum.shareit.item.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
public class CommentResponseDto {
    @PositiveOrZero(message = "Id комментария не может быть отрицательным числом")
    private Long id;

    @NotBlank(message = "Содержание сообщения не должно быть пустым")
    private String text;

    @NotBlank(message = "Имя автора комментария не должно быть пустым")
    private String authorName;

    @NotNull(message = "Дата создания комментария не должна быть null")
    private LocalDateTime created;
}