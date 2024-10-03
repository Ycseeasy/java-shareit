package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingDTOMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.item.ItemCreateDTO;
import ru.practicum.shareit.item.dto.item.ItemDTOForRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class ItemMapperTest {

    @Test
    void fromCreateDTO() {
        User user = new User(10, "ss", "sss");
        User requester = new User(11, "ss1", "sss1");
        ItemRequest itemRequest = new ItemRequest(2, "sss", requester, LocalDateTime.now());
        ItemCreateDTO dto = new ItemCreateDTO("ss", "sss", true, 2L);
        Item result = ItemDTOMapper.fromCreateDTO(user, dto, itemRequest);
        assertEquals(user, result.getOwner());
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
        BookingDto bookingDto = BookingDTOMapper.toDTO(result, null);
        assertEquals(user.getName(), result.getBooker().getName());
    }

    @Test
    void toDTOForRequest() {
        User user = new User(10, "ss", "sss");
        Item item = new Item(0, "ss", "sss", true, user, null);
        ItemDTOForRequest itemDTOForRequest = ItemDTOMapper.toDTOForRequest(item);
        assertEquals(item.getId(), itemDTOForRequest.getId());
        assertEquals(item.getName(), itemDTOForRequest.getName());
        assertEquals(item.getOwner().getId(), itemDTOForRequest.getOwnerId());
    }
}
