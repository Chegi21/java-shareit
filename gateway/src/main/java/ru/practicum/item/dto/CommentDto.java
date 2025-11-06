package ru.practicum.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
public class CommentDto {
    @NotBlank(message = "Содержание комментария не должно быть пустым")
    private String text;

    private String authorName;

    private LocalDateTime created;
}