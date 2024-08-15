package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {

    Item create(Item item);

    Item update(Item oldItem, Item newItem);

    Optional<Item> get(Long itemId);

    Collection<Item> getAllByOwner(Long ownerId);

    Collection<Item> getByQuery(String query);

}
