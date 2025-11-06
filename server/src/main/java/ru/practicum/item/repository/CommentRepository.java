package ru.practicum.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.item.model.Comment;

import java.util.Collection;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("FROM Comment c " +
            "WHERE c.item.id = :itemId " +
            "ORDER BY c.created DESC")
    Collection<Comment> findAllByItemId(@Param("itemId") Long itemId);
}
