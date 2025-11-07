package ru.practicum.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("FROM Item i WHERE i.owner.id = :ownerId")
    Collection<Item> findAllByOwnerId(@Param("ownerId") Long ownerId);

    @Query("FROM Item i " +
            "WHERE (LOWER(name) LIKE %:text% OR LOWER(description) LIKE %:text%) " +
            "AND available = TRUE")
    Collection<Item> findItemsBySearchQuery(String text);

    @Query("FROM Item i WHERE i.request.id = :requestId")
    Collection<Item> findAllByRequestId(@Param("requestId") Long requestId);

    @Query("FROM Item i WHERE i.request.id IN :requestIds")
    Collection<Item> findAllByRequestIds(@Param("requestIds") Collection<Long> requestIds);
}
