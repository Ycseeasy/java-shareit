package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDtoInput;
import ru.practicum.shareit.item.dto.CommentDtoOutput;
import ru.practicum.shareit.item.dto.ItemDtoInput;
import ru.practicum.shareit.item.dto.ItemDtoOutput;
import ru.practicum.shareit.item.dto.ItemDtoOutputBooking;

import java.util.Collection;

public interface ItemService {
    ItemDtoOutput createItem(ItemDtoInput itemDtoInput, Long userId);

    ItemDtoOutput updateItem(Long itemId, ItemDtoInput itemDtoInput, Long userId);

    ItemDtoOutputBooking getItem(Long itemId, Long userId);

    Collection<ItemDtoOutputBooking> getAllOwnerItems(Long userId);

    Collection<ItemDtoOutput> searchByQuery(String text);

    CommentDtoOutput addComment(long userId, long itemId, CommentDtoInput comment);
}
