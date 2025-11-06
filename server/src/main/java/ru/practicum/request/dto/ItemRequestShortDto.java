package ru.practicum.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestShortDto {
    private Long id;

    private String description;

    private User requester;

    private LocalDateTime created;
}
