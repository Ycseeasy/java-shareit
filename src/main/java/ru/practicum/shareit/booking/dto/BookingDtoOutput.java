package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoOutput;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDtoOutput {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDtoOutput item;
    private UserDto booker;
    private BookingStatus status;
}
