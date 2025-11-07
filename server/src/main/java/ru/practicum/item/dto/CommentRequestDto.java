package ru.practicum.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentRequestDto {
    private String text;

    private String authorName;

    private LocalDateTime created;
}