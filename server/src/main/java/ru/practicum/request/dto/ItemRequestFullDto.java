package ru.practicum.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.item.dto.ItemShortDto;
import ru.practicum.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder
public class ItemRequestFullDto {
    private Long id;

    private String description;

    private UserDto requester;

    private LocalDateTime created;

    private Collection<ItemShortDto> items;
}
