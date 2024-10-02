package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRequestRepository requestRepository;

    User user;
    ItemRequest request;
    Item item;
    Booking booking;
    User booker;

    @BeforeEach
    void addBooking() {
        LocalDateTime start = LocalDateTime.of(2025, 12, 12, 12, 12, 12);
        LocalDateTime end = LocalDateTime.of(2026, 12, 12, 12, 12, 12);

        user = userRepository.save(User.builder()
                .email("email")
                .name("name")
                .build());

        booker = userRepository.save(User.builder()
                .email("email333")
                .name("nameBooker")
                .build());

        request = requestRepository.save(ItemRequest.builder()
                .created(LocalDateTime.now())
                .description("sss")
                .requestor(booker)
                .build());

        item = itemRepository.save(Item.builder()
                .name("name")
                .description("qwerty")
                .owner(user)
                .request(request)
                .available(true)
                .build());

        booking = bookingRepository.save(Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build());
    }

    @Test
    void findByBookerIdOrderByStartAscTest() {
        List<Booking> result = bookingRepository.findByBookerIdOrderByStartAsc(booker.getId());
        Booking bookingResult = result.getFirst();
        assertEquals(bookingResult, booking);
    }

    @Test
    void findByBookerIdAndEndBeforeOrderByStartAscTest() {
        List<Booking> result = bookingRepository.findByBookerIdAndEndBeforeOrderByStartAsc(booker.getId(),
                LocalDateTime.of(2030, 12, 12, 12, 12, 12));
        Booking bookingResult = result.getFirst();
        assertEquals(bookingResult, booking);
    }

    @Test
    void findByBookerIdAndStartAfterAndEndBeforeOrderByStartAscTest() {
        List<Booking> result = bookingRepository.findByBookerIdAndStartAfterAndEndBeforeOrderByStartAsc(booker.getId(),
                LocalDateTime.of(2019, 12, 12, 12, 12, 12),
                LocalDateTime.of(2030, 12, 12, 12, 12, 12));
        Booking bookingResult = result.getFirst();
        assertEquals(bookingResult, booking);
    }

    @Test
    void findByBookerIdAndStatusOrderByStartAscTest() {
        List<Booking> result = bookingRepository
                .findByBookerIdAndStatusOrderByStartAsc(booker.getId(), BookingStatus.WAITING);
        Booking bookingResult = result.getFirst();
        assertEquals(bookingResult, booking);
    }

    @AfterEach
    void clear() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        requestRepository.deleteAll();
        userRepository.deleteAll();
    }

}
