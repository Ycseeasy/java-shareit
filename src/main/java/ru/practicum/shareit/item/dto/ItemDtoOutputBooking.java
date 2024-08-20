package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
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