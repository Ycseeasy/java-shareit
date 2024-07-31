package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {

public Item create(Item item);

public Item update(Item oldItem, Item newItem);

public Optional<Item> get(Long itemId);

public Collection<Item> getAllByOwner(Long ownerId);

public Collection<Item> getByQuery(String query);

}
