package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    public Item createItem(Item item);

    public Item updateItem(Long itemId, Item item);

    public Item getItem(Long itemId);

    Collection<Item> getAllOwnerItems(Long userId);

    Collection<Item> searchByQuery(String text);
}
