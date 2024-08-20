package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemDtoOutput;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;

@Component
@RequiredArgsConstructor
public class BookingDtoMapper {

    private final ItemDtoMapper itemDtoMapper;

    public Booking fromDTO(User user, Item item, BookingDtoInput booking) {
        return Booking.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(item)
                .booker(user)
                .build();
    }

    public BookingDtoOutput toDTO(Booking booking) {
        ItemDtoOutput itemDTO = itemDtoMapper.toDTO(booking.getItem());
        UserDto userDTO = UserDtoMapper.toDTO(booking.getBooker());
        return BookingDtoOutput.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(itemDTO)
                .booker(userDTO)
                .status(booking.getStatus())
                .build();
    }

}
