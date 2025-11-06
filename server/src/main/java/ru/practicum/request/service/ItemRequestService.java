package ru.practicum.request.service;

import ru.practicum.request.dto.ItemRequestFullDto;
import ru.practicum.request.dto.ItemRequestShortDto;

import java.time.LocalDateTime;
import java.util.Collection;

public interface ItemRequestService {
    ItemRequestShortDto create(ItemRequestShortDto itemRequestShortDto, Long requesterId, LocalDateTime created);

    ItemRequestFullDto getItemRequestById(Long itemRequestId, Long userId);

    Collection<ItemRequestFullDto> getOwnItemRequests(Long requesterId);

    Collection<ItemRequestFullDto> getAllItemRequests(Long userId, Integer from, Integer size);
}
