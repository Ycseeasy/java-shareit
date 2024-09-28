package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.comment.CommentDTO;
import ru.practicum.shareit.item.dto.item.ItemCreateDTO;
import ru.practicum.shareit.item.dto.item.ItemDTO;
import ru.practicum.shareit.item.dto.item.ItemDTOForRequest;
import ru.practicum.shareit.item.dto.item.ItemDTOWithBookings;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemDTOMapper {
    public static Item fromCreateDTO(User user, ItemCreateDTO itemCreateDTO, ItemRequest request) {
        return Item.builder()
                .name(itemCreateDTO.getName())
                .description(itemCreateDTO.getDescription())
                .available(itemCreateDTO.getAvailable())
                .owner(user)
                .request(request)
                .build();
    }

    private static ItemDTO toSemiFinishedDTO(Item item) {
        return ItemDTO.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .build();
    }

    public static ItemDTO toDTO(Item item, Collection<Comment> comments) {
        ItemDTO itemDTO = toSemiFinishedDTO(item);
        if (comments != null) {
            List<CommentDTO> commentsDTO = comments
                    .stream()
                    .map(CommentDTOMapper::toDTO)
                    .toList();
            itemDTO.setComments(commentsDTO);
        }
        return itemDTO;
    }

    public static Collection<ItemDTO> toDTO(Collection<Item> items, Map<Long, List<Comment>> commentMap) {
        Map<Long, List<CommentDTO>> commentMapDto = commentMapConvert(commentMap);
        return items.stream()
                .map(ItemDTOMapper::toSemiFinishedDTO)
                .peek(itemDTO -> itemDTO.setComments(commentMapDto.get(itemDTO.getId())))
                .toList();
    }

    private static ItemDTOWithBookings toSemiFinishedDTOWithBookings(Item item) {
        return ItemDTOWithBookings.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .build();
    }

    public static ItemDTOWithBookings toDTOWithBookings(long userId, Item item, Collection<Comment> comments,
                                                        Booking lastBooking, Booking nextBooking) {
        ItemDTOWithBookings itemDTO = toSemiFinishedDTOWithBookings(item);
        List<CommentDTO> commentsDTO = comments
                .stream()
                .map(CommentDTOMapper::toDTO)
                .toList();
        itemDTO.setComments(commentsDTO);
        if (lastBooking != null) {
            itemDTO.setLastBooking(lastBooking.getEnd());
        }
        if (nextBooking != null) {
            itemDTO.setNextBooking(nextBooking.getStart());
        }
        return itemDTO;
    }

    public static Collection<ItemDTOWithBookings> toDTOWithBookings(long userId, Collection<Item> items,
                                                                    Map<Long, List<Comment>> commentMap,
                                                                    Map<Long, Booking> lastBookingMap,
                                                                    Map<Long, Booking> nextBookingMap) {
        Map<Long, List<CommentDTO>> commentMapDto = commentMapConvert(commentMap);
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .toList();
        return items.stream()
                .map(ItemDTOMapper::toSemiFinishedDTOWithBookings)
                .peek(dto -> dto.setComments(commentMapDto.get(dto.getId())))
                .peek(dto -> {
                    Booking booking = lastBookingMap.get(dto.getId());
                    LocalDateTime date = (booking == null) ? null : booking.getEnd();
                    dto.setLastBooking(date);
                })
                .peek(dto -> {
                    Booking booking = nextBookingMap.get(dto.getId());
                    LocalDateTime date = (booking == null) ? null : booking.getStart();
                    dto.setNextBooking(date);
                })
                .toList();
    }


    public static ItemDTOForRequest toDTOForRequest(Item item) {
        return ItemDTOForRequest.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .build();
    }

    private static Map<Long, List<CommentDTO>> commentMapConvert(Map<Long, List<Comment>> commentMap) {
        Map<Long, List<CommentDTO>> commentMapDto = new HashMap<>();
        for (Map.Entry<Long, List<Comment>> entry : commentMap.entrySet()) {
            List<CommentDTO> commentDTO = entry.getValue()
                    .stream()
                    .map(CommentDTOMapper::toDTO)
                    .toList();
            commentMapDto.put(entry.getKey(), commentDTO);
        }
        return commentMapDto;
    }

}