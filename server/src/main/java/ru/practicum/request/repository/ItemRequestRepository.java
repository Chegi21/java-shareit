package ru.practicum.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.request.model.ItemRequest;

import java.util.Collection;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("FROM ItemRequest ir WHERE ir.requester.id = :requesterId ORDER BY ir.created DESC")
    Collection<ItemRequest> findAllByRequesterId(@Param("requesterId") Long requesterId);

    @Query("FROM ItemRequest ir WHERE ir.requester.id <> :userId ORDER BY ir.created DESC")
    Page<ItemRequest> findAllByRequesterIdNot(@Param("userId") Long userId, Pageable pageable);

}
