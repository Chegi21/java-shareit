package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemDao {
    Optional<Item> getItemById(Long itemId);

    Collection<Item> getItemsByOwner(Long ownerId);

    Collection<Item> getItemsBySearchQuery(String text);

    Item create(Item item);

    Item update(Item item);

    Item delete(Long itemId);

}
