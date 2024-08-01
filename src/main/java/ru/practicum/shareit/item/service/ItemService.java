package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(ItemDto itemdto, Long userId);

    ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId);

    ItemDto getItem(Long itemId);

    Collection<ItemDto> getAllOwnerItems(Long userId);

    Collection<ItemDto> searchByQuery(String text);
}
