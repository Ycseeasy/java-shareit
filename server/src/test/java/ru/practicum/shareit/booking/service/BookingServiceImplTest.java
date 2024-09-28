package ru.practicum.shareit.booking.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.dto.item.ItemCreateDTO;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestCreateDTO;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.dto.ItemRequestDTOWithAnswers;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.UserCreateDTO;

import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    private final ItemRequestServiceImpl itemRequestService;
    private final UserServiceImpl userService;
    private final BookingServiceImpl bookingService;
    private final ItemServiceImpl itemService;

    private final EntityManager em;


    @Test
    void createBooking() {
        UserCreateDTO userCreateDTO = new UserCreateDTO("Anna", "anna@mail.ru");
        long userId = userService.createUser(userCreateDTO).getId();

        String description = "some description";
        ItemRequestCreateDTO itemRequestCreateDTO = new ItemRequestCreateDTO(description);
        ItemRequestDTO returnedRequestDTO = itemRequestService.createItemRequest(userId, itemRequestCreateDTO);

        ItemCreateDTO itemCreateDTO = new ItemCreateDTO("ss", "sss",
                true, returnedRequestDTO.getId());
        long itemId = itemService.createItem(userId,itemCreateDTO).getId();

        LocalDateTime start = LocalDateTime.of(2024, 10, 1, 12, 30);
        LocalDateTime end = LocalDateTime.of(2024, 11, 1, 12, 30);
        BookingCreateDto bookingCreateDto = new BookingCreateDto(itemId, start, end);

        bookingService.createBooking(userId, bookingCreateDto);

        TypedQuery<Booking> getQuery = em.createQuery("SELECT b FROM Booking AS b WHERE b.booker.id=:booker_id",
                Booking.class).setParameter("booker_id", userId);
        Booking savedBooking = getQuery.getSingleResult();

        assertEquals(userId, savedBooking.getBooker().getId());
        assertEquals(start, savedBooking.getStart());
        assertEquals(end, savedBooking.getEnd());
    }

    @Test
    void getIncorrectBookingStatus() {
        assertThrows(IdNotFoundException.class, () -> bookingService.getBookingStatus(1,1));
    }

    @Test
    void getBookingStatus() {
        UserCreateDTO userCreateDTO = new UserCreateDTO("Anna", "anna@mail.ru");
        long userId = userService.createUser(userCreateDTO).getId();

        String description = "some description";
        ItemRequestCreateDTO itemRequestCreateDTO = new ItemRequestCreateDTO(description);
        ItemRequestDTO returnedRequestDTO = itemRequestService.createItemRequest(userId, itemRequestCreateDTO);

        ItemCreateDTO itemCreateDTO = new ItemCreateDTO("ss", "sss",
                true, returnedRequestDTO.getId());
        long itemId = itemService.createItem(userId,itemCreateDTO).getId();

        LocalDateTime start = LocalDateTime.of(2024, 10, 1, 12, 30);
        LocalDateTime end = LocalDateTime.of(2024, 11, 1, 12, 30);
        BookingCreateDto bookingCreateDto = new BookingCreateDto(itemId, start, end);

        long bookingId = bookingService.createBooking(userId, bookingCreateDto).getId();

        BookingDto answer = bookingService.getBookingStatus(userId, bookingId);
        assertEquals(bookingId, answer.getId());
        assertEquals(userId, answer.getBooker().getId());
    }
}
