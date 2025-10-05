package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    Collection<ItemDto> getItemsByOwner(Long ownerId);

    Collection<ItemDto> getItemsBySearchQuery(String text);

    ItemDto getItemById(Long itemId);

    ItemDto create(ItemDto itemDto, Long ownerId);

    ItemDto update(ItemDto itemDto, Long itemId, Long ownerId);

    ItemDto delete(Long itemId, Long ownerId);
}
