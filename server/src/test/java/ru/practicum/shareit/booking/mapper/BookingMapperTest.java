package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class RequestDTOMapperTest {
    private final BookingDTOMapper bookingDTOMapper;


    @Test
    void fromCreateDTO() {
        LocalDateTime start = LocalDateTime.of(2024, 10, 1, 12, 12);
        LocalDateTime end = LocalDateTime.of(2024, 12, 1, 12, 12);
        User user = new User(10, "ss", "sss");
        User requester = new User(11, "ss1", "sss1");
        ItemRequest itemRequest = new ItemRequest(2, "sss", requester, LocalDateTime.now());
        Item item = new Item(12, "ss", "sss", true, user, itemRequest);
        Booking booking = new Booking(10, start, end, item, requester, BookingStatus.APPROVED);

        BookingCreateDto createDTO = new BookingCreateDto(item.getId(), start, end);
        Booking result = BookingDTOMapper.fromCreateDTO(user, item, createDTO);
        assertEquals(user, result.getBooker());
    }

    @Test
    void toDTO() {
        User user = new User(10, "ss", "sss");
        LocalDateTime start = LocalDateTime.of(2024, 10, 1, 12, 12);
        LocalDateTime end = LocalDateTime.of(2024, 12, 1, 12, 12);
        User requester = new User(11, "ss1", "sss1");
        ItemRequest itemRequest = new ItemRequest(2, "sss", requester, LocalDateTime.now());
        Item item = new Item(12, "ss", "sss", true, user, itemRequest);
        Booking booking = new Booking(10, start, end, item, requester, BookingStatus.APPROVED);

        BookingCreateDto createDTO = new BookingCreateDto(item.getId(), start, end);
        Booking result = BookingDTOMapper.fromCreateDTO(user, item, createDTO);
        BookingDto bookingDto = bookingDTOMapper.toDTO(result);
        assertEquals(user.getName(),result.getBooker().getName());
    }
}
