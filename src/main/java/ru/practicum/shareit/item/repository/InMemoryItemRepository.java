package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InMemoryItemRepository implements ItemRepository {

    private final HashMap<Long, Item> itemData = new HashMap<>();

    @Override
    public Item create(Item item) {
        item.setId(getNextId());
        itemData.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item oldItem, Item newItem) {
        if (newItem.getName() == null || newItem.getName().isEmpty()) {
            newItem.setName(oldItem.getName());
        }
        if (newItem.getDescription() == null || newItem.getDescription().isEmpty()) {
            newItem.setDescription(oldItem.getDescription());
        }
        if (newItem.getAvailable() == null || newItem.getAvailable().isEmpty()) {
            newItem.setAvailable(oldItem.getAvailable());
        }
        if (newItem.getOwnerId() == null) {
            newItem.setOwnerId(oldItem.getOwnerId());
        }
        itemData.replace(newItem.getId(), newItem);
        return newItem;
    }

    @Override
    public Optional<Item> get(Long itemId) {
        return Optional.ofNullable(itemData.get(itemId));
    }

    @Override
    public Collection<Item> getAllByOwner(Long ownerId) {
        return itemData.values().stream()
                .filter(item -> {
                    return item.getOwnerId().equals(ownerId);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> getByQuery(String query) {
        if (query == null || query.isEmpty()) {
            return new ArrayList<>();
        }
        return itemData.values().stream()
                .filter(item -> {
                    return item.getAvailable().equals("true")
                            && item.getName().toLowerCase().contains(query.toLowerCase())
                            || item.getDescription().toLowerCase().contains(query.toLowerCase());
                })
                .collect(Collectors.toList());
    }

    private long getNextId() {
        long currentMaxId = itemData.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
