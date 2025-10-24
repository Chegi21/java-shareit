package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Collection<Item> findAllByOwnerId(Long ownerId);

    @Query(value = "SELECT * FROM items " +
            "WHERE (LOWER(name) LIKE %:text% OR LOWER(description) LIKE %:text%) " +
            "AND available = TRUE",
            nativeQuery = true)
    Collection<Item> findItemsBySearchQuery(String text);

}
