package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Collection<Item> getItemsByOwner(Long ownerId);

    Collection<Item> getItemsBySearchQuery(String text);

    Item getItemById(Long itemId);

    Item create(Item item);

    Item update(Item item);

    Item delete(Long itemId, Long ownerId);
}
