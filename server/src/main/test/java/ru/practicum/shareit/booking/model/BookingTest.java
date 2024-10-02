package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingTest {

    @Test
    void getStatus() {
        User user = new User(1L, "ss", "sss");
        ItemRequest request = new ItemRequest(1L,"sss",user, LocalDateTime.now());
        Item item = new Item(1L,"ss","sss",true, user, request);
        Booking booking = new Booking(
                1L,
                LocalDateTime.of(2222, 12,12,12,12,12),
                LocalDateTime.of(2223, 12,12,12,12,12),
                item,
                user,
                BookingStatus.APPROVED);

        assertEquals(BookingStatus.APPROVED, booking.getStatus());
    }
}
