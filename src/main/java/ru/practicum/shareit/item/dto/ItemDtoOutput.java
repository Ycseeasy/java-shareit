package ru.practicum.shareit.item.dto;

import lombok.*;

import java.util.Collection;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDtoOutput {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private Collection<CommentDtoOutput> comments;
}