package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDtoOutputBooking {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private Collection<CommentDtoOutput> comments;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
}