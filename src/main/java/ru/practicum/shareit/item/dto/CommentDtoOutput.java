package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDtoOutput {
    long id;
    String text;
    String authorName;
    LocalDateTime created;
}
