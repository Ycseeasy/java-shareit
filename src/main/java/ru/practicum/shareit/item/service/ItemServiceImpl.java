package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {


    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public Item createItem(Item item) {
        validate(item);
        return itemRepository.create(item);
    }

    @Override
    public Item updateItem(Long itemId, Item item) {
        Item oldItem = getItem(itemId);
        Item updatedItem = itemRepository.update(oldItem, item);
        validate(updatedItem);
        return updatedItem;
    }

    @Override
    public Item getItem(Long itemId) {
        Optional<Item> searchedItem = itemRepository.get(itemId);
        if (searchedItem.isPresent()) {
            return searchedItem.get();
        } else {
            throw new NotFoundException("Инструмент с ID - " + itemId + " не найден.");
        }
    }

    @Override
    public Collection<Item> getAllOwnerItems(Long userId) {
        return itemRepository.getAllByOwner(userId);
    }

    @Override
    public Collection<Item> searchByQuery(String text) {
        return itemRepository.getByQuery(text);
    }

    private void validate(Item item) {
        Optional<User> owner = userRepository.get(item.getOwnerId());
        if (owner.isEmpty()) {
            throw new NotFoundException("Владелец инструмента с ID - " + item.getOwnerId() + " не найден");
        }
        if (item.getOwnerId() == null) {
            throw new ValidationException("Поле OwnerId не может быть пустым");
        }
        if (item.getName() == null || item.getName().isEmpty()) {
            throw new ValidationException("Поле Name не может быть пустым");
        }
        if (item.getDescription() == null || item.getDescription().isEmpty()) {
            throw new ValidationException("Поле Description не может быть пустым");
        }
        if (item.getAvailable() == null || item.getAvailable().isEmpty()) {
            throw new ValidationException("Поле Available не может быть пустым");
        }
    }
}
