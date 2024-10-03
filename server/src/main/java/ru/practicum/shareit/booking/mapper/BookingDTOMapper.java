package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.item.ItemDTO;
import ru.practicum.shareit.item.mapper.ItemDTOMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.mapper.UserDTOMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class BookingDTOMapper {
    private final ItemDTOMapper itemDTOMapper;

    public static Booking fromCreateDTO(User user, Item item, BookingCreateDto booking) {
        return Booking.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(item)
                .booker(user)
                .build();
    }

    public static BookingDto toDTO(Booking booking, Collection<Comment> comments) {
        ItemDTO itemDTO = ItemDTOMapper.toDTO(booking.getItem(), comments);
        UserDTO userDTO = UserDTOMapper.toDTO(booking.getBooker());
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(itemDTO)
                .booker(userDTO)
                .status(booking.getStatus())
                .build();
    }

}
