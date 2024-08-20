package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class CommentDtoMapper {
    public static CommentDtoOutput toDTO(Comment comment) {
        return CommentDtoOutput.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static Comment fromNewDTO(User user, Item item, LocalDateTime creationTime, CommentDtoInput dto) {
        return Comment.builder()
                .text(dto.getText())
                .item(item)
                .author(user)
                .created(creationTime)
                .build();
    }
}
