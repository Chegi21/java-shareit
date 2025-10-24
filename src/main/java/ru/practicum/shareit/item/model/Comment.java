package ru.practicum.shareit.item.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;
}

