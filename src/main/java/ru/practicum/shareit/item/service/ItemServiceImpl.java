package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InvalidUserException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemDTOMapper;
    private final BookingRepository bookingRepository;
    private final CommentMapperOut commentMapperOut;
    private final CommentMapperIn commentMapperIn;

    @Override
    public ItemDtoOutput createItem(ItemDtoInput itemDtoInput, Long userId) {
        validate(itemDtoInput);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден."));
        Item itemToCreate = itemDTOMapper.fromDTO(itemDtoInput, user);
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
        Collection<CommentDtoOutput> commentsDto = commentRepository
                .findByItemId(updatetItem.getId())
                .stream()
                .map(commentMapperOut::toDTO)
                .toList();
        return itemDTOMapper.toDTO(updatetItem, commentsDto);
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
    public ItemDtoOutputBooking getItem(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Позиция с ID " + itemId + " не найдена."));
        Optional<Booking> lastBooking = bookingRepository.getLastBookingForItemOwnedByUser(userId, item.getId());
        Optional<Booking> nextBooking = bookingRepository.getNextBookingForItemOwnedByUser(userId, item.getId());
        Collection<CommentDtoOutput> commentsDto = commentRepository
                .findByItemId(item.getId())
                .stream()
                .map(commentMapperOut::toDTO)
                .toList();
        return itemDTOMapper.toDTOBookings(item, commentsDto,
                lastBooking.orElse(null), nextBooking.orElse(null));
    }

    @Override
    public Collection<ItemDtoOutputBooking> getAllOwnerItems(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден.");
        }
        Collection<ItemDtoOutputBooking> result = new ArrayList<>();

        Collection<Item> items = itemRepository.findByOwnerId(userId);
        Collection<Long> itemsId = items
                .stream()
                .map(Item::getId)
                .toList();

        Map<Item, List<Booking>> bookingsMap = bookingRepository.findByItemIdInAndStatusNotOrderByStartAsc(itemsId,
                        BookingStatus.REJECTED)
                .stream()
                .collect(Collectors.groupingBy(Booking::getItem));

        Map<Item, List<Comment>> commentMap = commentRepository.findByItemIdIn(itemsId)
                .stream()
                .collect(Collectors.groupingBy(Comment::getItem));

        for (Item i : items) {
            List<CommentDtoOutput> commentsI = List.of();
            if (!commentMap.isEmpty()) {
                commentsI = commentMap.get(i)
                        .stream()
                        .map(commentMapperOut::toDTO)
                        .toList();
            }
            if (!bookingsMap.isEmpty()) {
                List<Booking> bookingsI = bookingsMap.get(i);
                Optional<Booking> nextBooking = bookingsI
                        .stream()
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .findFirst();

                Optional<Booking> lastBooking = bookingsI
                        .stream()
                        .sorted(Comparator.comparing(Booking::getEnd))
                        .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                        .findFirst();

                result.add(itemDTOMapper.toDTOBookings(i, commentsI,
                        lastBooking.orElse(null), nextBooking.orElse(null)));
            }
            result.add(itemDTOMapper.toDTOBookings(i, commentsI, null, null));
        }
        return result;
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
            throw new InvalidUserException("Пользователь с ID " + userId
                    + " не бронировал товар с ID " + item + ".");
        }
        Comment comment = commentMapperIn.fromDTO(user, item, LocalDateTime.now(), commentDto);
        Comment newComment = commentRepository.save(comment);
        return commentMapperOut.toDTO(newComment);
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
