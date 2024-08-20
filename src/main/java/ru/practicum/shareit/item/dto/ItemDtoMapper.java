package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.user.model.User;

@Component
@RequiredArgsConstructor
public class ItemDtoMapper {
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    public ItemDtoOutput toDTO(Item item) {
        var commentsDTO = commentRepository
                .getCommentsByItem(item.getId())
                .stream()
                .map(CommentDtoMapper::toDTO)
                .toList();
        return ItemDtoOutput.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .comments(commentsDTO)
                .build();
    }

    public ItemDtoOutputBooking toDtoBookings(long userId, Item item) {
        var commentsDTO = commentRepository
                .getCommentsByItem(item.getId())
                .stream()
                .map(CommentDtoMapper::toDTO)
                .toList();
        var lastBooking = bookingRepository.getLastBookingForItemOwnedByUser(userId, item.getId());
        var nextBooking = bookingRepository.getNextBookingForItemOwnedByUser(userId, item.getId());
        return ItemDtoOutputBooking.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .comments(commentsDTO)
                .lastBooking(lastBooking.map(Booking::getEnd).orElse(null))
                .nextBooking(nextBooking.map(Booking::getStart).orElse(null))
                .build();
    }

    public Item fromDTO(User user, ItemDtoInput itemNewDTO) {
        return Item.builder()
                .name(itemNewDTO.getName())
                .description(itemNewDTO.getDescription())
                .available(itemNewDTO.getAvailable())
                .owner(user)
                .build();
    }
}
