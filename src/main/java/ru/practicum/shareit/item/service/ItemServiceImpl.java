package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
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
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        Item item = ItemDtoMapper.fromDTO(userId, itemDto);
        validate(item);
        Item createdItem = itemRepository.create(item);
        return ItemDtoMapper.toDTO(createdItem);
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {
        Item item = ItemDtoMapper.fromDTO(userId, itemDto);
        Optional<Item> searchedItem = itemRepository.get(itemId);
        if (searchedItem.isPresent()) {
            Item oldItem = searchedItem.get();
            Item updatedItem = itemRepository.update(oldItem, item);
            validate(updatedItem);
            return ItemDtoMapper.toDTO(updatedItem);
        } else {
            throw new NotFoundException("Инструмент с ID - " + itemId + " не найден.");
        }
    }

    @Override
    public ItemDto getItem(Long itemId) {
        Optional<Item> searchedItem = itemRepository.get(itemId);
        if (searchedItem.isPresent()) {
            Item searchResult = searchedItem.get();
            return ItemDtoMapper.toDTO(searchResult);
        } else {
            throw new NotFoundException("Инструмент с ID - " + itemId + " не найден.");
        }
    }

    @Override
    public Collection<ItemDto> getAllOwnerItems(Long userId) {
        return itemRepository.getAllByOwner(userId)
                .stream()
                .map(ItemDtoMapper::toDTO)
                .toList();
    }

    @Override
    public Collection<ItemDto> searchByQuery(String text) {
        return itemRepository.getByQuery(text)
                .stream()
                .map(ItemDtoMapper::toDTO)
                .toList();
    }

    private void validate(Item item) {
        Optional<User> owner = userRepository.get(item.getOwnerId());
        if (owner.isEmpty()) {
            throw new NotFoundException("Владелец инструмента с ID - " + item.getOwnerId() + " не найден");
        }
        if (item.getOwnerId() == null) {
            throw new ValidationException("Поле OwnerId не может быть пустым");
        }
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("Поле Name не может быть пустым");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Поле Description не может быть пустым");
        }
        if (item.getAvailable() == null || item.getAvailable().isBlank()) {
            throw new ValidationException("Поле Available не может быть пустым");
        }
    }
}
