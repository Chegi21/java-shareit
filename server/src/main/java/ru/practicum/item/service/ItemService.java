package ru.practicum.item.service;

import ru.practicum.item.dto.CommentRequestDto;
import ru.practicum.item.dto.CommentResponseDto;
import ru.practicum.item.dto.ItemFullDto;
import ru.practicum.item.dto.ItemShortDto;

import java.util.Collection;

public interface ItemService {
    Collection<ItemFullDto> getItemsByOwner(Long ownerId);

    Collection<ItemFullDto> getItemsBySearchQuery(String text);

    ItemFullDto getItemById(Long itemId, Long ownerId);

    ItemShortDto create(ItemShortDto item, Long ownerId);

    ItemShortDto update(ItemShortDto item, Long itemId, Long ownerId);

    void delete(Long itemId, Long ownerId);

    CommentResponseDto create(CommentRequestDto comment, Long itemId, Long userId);
}
