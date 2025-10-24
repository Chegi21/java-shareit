package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

public class CommentMapper {
    public static CommentResponseDto toCommentResponseDto(Comment comment, User author) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(author.getName())
                .created(comment.getCreated())
                .build();
    }
}
