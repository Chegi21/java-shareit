package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;

import java.util.Collection;

public interface ItemService {
    Collection<ItemDto> getItemsByOwner(Long ownerId);

    Collection<ItemDto> getItemsBySearchQuery(String text);

    ItemDto getItemById(Long itemId);

    ItemShortDto create(ItemShortDto item, Long ownerId);

    ItemShortDto update(ItemShortDto item, Long itemId, Long ownerId);

    void delete(Long itemId, Long ownerId);

    CommentResponseDto create(CommentRequestDto comment, Long itemId, Long authorId);
}
