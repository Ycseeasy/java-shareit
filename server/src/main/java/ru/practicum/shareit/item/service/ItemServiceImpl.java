package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.item.dto.comment.CommentCreateDTO;
import ru.practicum.shareit.item.dto.comment.CommentDTO;
import ru.practicum.shareit.item.dto.item.ItemCreateDTO;
import ru.practicum.shareit.item.dto.item.ItemDTO;
import ru.practicum.shareit.item.dto.item.ItemDTOWithBookings;
import ru.practicum.shareit.item.dto.item.ItemUpdateDTO;
import ru.practicum.shareit.item.mapper.CommentDTOMapper;
import ru.practicum.shareit.item.mapper.ItemDTOMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ItemDTO createItem(long userId, ItemCreateDTO itemCreateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException(String.format("User with id=%d does not exists", userId)));
        Long requestId = itemCreateDTO.getRequestId();
        ItemRequest request = null;
        if (requestId != null) {
            request = requestRepository.findById(requestId)
                    .orElseThrow(() -> new IdNotFoundException(
                            String.format("Item request with id=%d does not exists", requestId)));
        }
        Item itemToCreate = ItemDTOMapper.fromCreateDTO(user, itemCreateDTO, request);
        Item createdItem = itemRepository.save(itemToCreate);
        log.info("Item {} was created", createdItem);
        Collection<Comment> comments = commentRepository.findByItemId(createdItem.getId());
        return ItemDTOMapper.toDTO(createdItem, comments);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ItemDTO updateItem(long userId, long itemId, ItemUpdateDTO itemUpdateDTO) {
        Item oldItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new IdNotFoundException(String.format("Item with id=%d does not exists", itemId)));
        validateIfUserOwnsItem(userId, itemId);

        Item itemToUpdate = fillInFieldsToUpdate(oldItem, itemUpdateDTO);
        Item updatedItem = itemRepository.save(itemToUpdate);
        log.info("{} was updated", updatedItem);
        Collection<Comment> comments = commentRepository.findByItemId(updatedItem.getId());
        return ItemDTOMapper.toDTO(itemToUpdate, comments);
    }

    private Item fillInFieldsToUpdate(Item item, ItemUpdateDTO itemUpdateDTO) {
        String name = itemUpdateDTO.getName();
        if (nonNull(name)) {
            item.setName(name);
        }
        String description = itemUpdateDTO.getDescription();
        if (nonNull(description)) {
            item.setDescription(description);
        }
        Boolean available = itemUpdateDTO.getAvailable();
        if (nonNull(available)) {
            item.setAvailable(available);
        }
        return item;
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public ItemDTOWithBookings getItem(long userId, long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        Booking lastBooking = bookingRepository.getLastBookingForItemOwnedByUser(userId, itemId).orElse(null);
        Booking nextBooking = bookingRepository.getNextBookingForItemOwnedByUser(userId, itemId).orElse(null);
        Collection<Comment> comments = commentRepository.findByItemId(itemId);
        return item.map(i -> ItemDTOMapper.toDTOWithBookings(userId, i, comments, lastBooking, nextBooking)).orElseThrow(
                () -> new IdNotFoundException("Item with id=" + itemId + " not found"));
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public Collection<ItemDTOWithBookings> getAllItems(long userId) {
        validateIfUserNotExists(userId);
        Collection<Item> items = itemRepository.findByOwnerId(userId);
        Collection<Long> itemIds = items
                .stream()
                .map(Item::getId)
                .toList();
        Map<Long, Booking> lastBookingMap =  getLastBookingMap(userId, itemIds);
        Map<Long, Booking> nextBookingMap =  getNextBookingMap(userId, itemIds);
        Map<Long, List<Comment>> mapComments = getCommentsMapByItemIds(items);
        return ItemDTOMapper.toDTOWithBookings(userId, items, mapComments, lastBookingMap, nextBookingMap);
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public Collection<ItemDTO> searchItems(String text) {
        if (isNull(text) || text.isBlank()) {
            return Collections.emptyList();
        }
        Collection<Item> items = itemRepository.searchItems(text);
        return ItemDTOMapper.toDTO(items, getCommentsMapByItemIds(items));
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CommentDTO addComment(long userId, long itemId, CommentCreateDTO commentDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IdNotFoundException(String.format("Item with id=%d does not exists", itemId)));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException(String.format("User with id=%d does not exists", userId)));
        validateIfUserBookedItem(userId, itemId);
        Comment comment = CommentDTOMapper.fromCreateDTO(user, item, commentDto);
        Comment newComment = commentRepository.save(comment);
        log.info("{} was added", newComment);
        return CommentDTOMapper.toDTO(newComment);
    }

    private Map<Long, List<Comment>> getCommentsMapByItemIds(Collection<Item> items) {
        Collection<Long> itemIds = items.stream()
                .map(Item::getId)
                .toList();
        Collection<Comment> allComments = commentRepository.findByItemIdIn(itemIds);
        Map<Long, List<Comment>> map = new HashMap<>();
        for (Comment comment : allComments) {
            Long itemId = comment.getItem().getId();
            if (!map.containsKey(itemId)) {
                map.put(itemId, new ArrayList<>());
            }
            map.get(itemId).add(comment);
        }
        return map;
    }

    private Map<Long, Booking> getLastBookingMap(long userId, Collection<Long> itemIds) {
        Collection<Booking> allBookings = bookingRepository.getAllLastBookings(userId, itemIds);
        return allBookings.stream()
                .collect(Collectors.toMap(b -> b.getItem().getId(), Function.identity()));
    }

    private Map<Long, Booking> getNextBookingMap(long userId, Collection<Long> itemIds) {
        Collection<Booking> allBookings = bookingRepository.getAllNextBooking(userId, itemIds);
        return allBookings.stream()
                .collect(Collectors.toMap(b -> b.getItem().getId(), Function.identity()));
    }

    public void validateIfUserNotExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IdNotFoundException(String.format("User with id=%d does not exists", userId));
        }
    }

    public void validateIfUserBookedItem(long userId, long itemId) {
        if (!bookingRepository.isUserBookedItem(userId, itemId)) {
            throw new InternalServerException(String.format("User id=%d did not book item id=%d", userId, itemId));
        }
    }

    public void validateIfUserOwnsItem(long userId, long itemId) {
        if (!itemRepository.existsByIdAndOwner(itemId, userId)) {
            throw new IdNotFoundException(String.format("User id=%d does not own item id=%d", userId, itemId));
        }
    }
}
