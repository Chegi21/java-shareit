package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemDaoImp implements ItemDao {
    private final Map<Long, Item> itemMap = new HashMap<>();

    @Override
    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(itemMap.get(itemId));
    }

    @Override
    public Collection<Item> getItemsByOwner(Long ownerId) {
        return itemMap.values().stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<Item> getItemsBySearchQuery(String text) {
        return itemMap.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text) ||
                        item.getDescription().toLowerCase().contains(text))
                .collect(Collectors.toList());
    }

    @Override
    public Item create(Item item) {
        long id = getNextId();
        item.setId(id);
        itemMap.put(id, item);
        return item;
    }

    @Override
    public Item update(Item item) {
        return itemMap.put(item.getId(), item);
    }

    @Override
    public Item delete(Long itemId) {
        return itemMap.remove(itemId);
    }

    private long getNextId() {
        long currentMaxId = itemMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L);
        return ++currentMaxId;
    }
}
