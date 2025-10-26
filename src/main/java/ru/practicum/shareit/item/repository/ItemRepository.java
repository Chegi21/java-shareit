package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i WHERE i.owner.id = :ownerId")
    Collection<Item> findAllByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT i FROM Item i " +
            "WHERE (LOWER(name) LIKE %:text% OR LOWER(description) LIKE %:text%) " +
            "AND available = TRUE")
    Collection<Item> findItemsBySearchQuery(String text);

}
