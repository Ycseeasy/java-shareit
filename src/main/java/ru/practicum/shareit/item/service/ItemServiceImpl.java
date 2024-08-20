package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDtoInput;
import ru.practicum.shareit.item.dto.CommentDtoMapper;
import ru.practicum.shareit.item.dto.CommentDtoOutput;
import ru.practicum.shareit.item.dto.ItemDtoInput;
import ru.practicum.shareit.item.dto.ItemDtoOutput;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemDtoOutputBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ItemDtoMapper itemDTOMapper;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDtoOutput createItem(ItemDtoInput itemDtoInput, Long userId) {
        validate(itemDtoInput);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден."));
        Item itemToCreate = itemDTOMapper.fromDTO(user, itemDtoInput);
        Item createdItem = itemRepository.save(itemToCreate);
        return itemDTOMapper.toDTO(createdItem);
    }

    @Override
    public ItemDtoOutput updateItem(Long itemId, ItemDtoInput itemDtoInput, Long userId) {
        Item oldItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Позиция с ID " + itemId + " не найдена."));
        if (!itemRepository.existsByIdAndOwner(itemId, userId)) {
            throw new NotFoundException("Пользователь с ID " + userId + " не владеет позицией с ID " + itemId + ".");
        }
        Item itemToUpdate = updateFields(oldItem, itemDtoInput);
        Item updatetItem = itemRepository.save(itemToUpdate);
        return itemDTOMapper.toDTO(updatetItem);
    }

    private Item updateFields(Item oldItem, ItemDtoInput itemDtoInput) {
        String name = itemDtoInput.getName();
        if (nonNull(name)) {
            oldItem.setName(name);
        }
        String description = itemDtoInput.getDescription();
        if (nonNull(description)) {
            oldItem.setDescription(description);
        }
        Boolean available = itemDtoInput.getAvailable();
        if (nonNull(available)) {
            oldItem.setAvailable(available);
        }
        return oldItem;
    }

    @Override
    public ItemDtoOutputBooking getItem(Long userId, Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        return item.map(i -> itemDTOMapper.toDtoBookings(userId, i)).orElseThrow(
                () -> new NotFoundException("Позиция с ID " + userId + " не найдена."));
    }

    @Override
    public Collection<ItemDtoOutputBooking> getAllOwnerItems(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден.");
        }
        Collection<Item> items = itemRepository.getAllItems(userId);
        return items.stream()
                .map(item -> itemDTOMapper.toDtoBookings(userId, item))
                .toList();
    }

    @Override
    public Collection<ItemDtoOutput> searchByQuery(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        Collection<Item> items = itemRepository.searchItems(text);
        return items.stream()
                .map(itemDTOMapper::toDTO)
                .toList();
    }

    @Override
    public CommentDtoOutput addComment(long userId, long itemId, CommentDtoInput commentDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Позиция с ID " + itemId + " не найдена."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден."));
        if (!bookingRepository.isUserBookedItem(userId, itemId)) {
            throw new InternalServerException("Пользователь с ID " + userId
                    + " не бронировал товар с ID " + item + ".");
        }
        Comment comment = CommentDtoMapper.fromNewDTO(user, item, LocalDateTime.now(), commentDto);
        Comment newComment = commentRepository.save(comment);
        return CommentDtoMapper.toDTO(newComment);
    }

    private void validate(ItemDtoInput item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("Поле Name не может быть пустым");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Поле Description не может быть пустым");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("Поле Available не может быть пустым");
        }
    }
}
