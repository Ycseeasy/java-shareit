package ru.practicum.shareit.booking.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


public class BookingClientTest {

    private final BookingCreateDto bookingCreateDto = new BookingCreateDto(
            1L,
            LocalDateTime.of(2024, 12, 1, 12, 12, 12),
            LocalDateTime.of(2025, 12, 1, 12, 12, 12)
    );
    long userId = 1L;
    RestTemplate rest;
    BookingClient client;

    @BeforeEach
    void setUp() {
        rest = Mockito.mock(RestTemplate.class);
        client = new BookingClient(rest);
    }

    @Test
    void createBooking() {
        ResponseEntity<Object> response = new ResponseEntity<>(bookingCreateDto, HttpStatus.CREATED);
        when(rest.exchange(
                anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Object>>any()))
                .thenReturn(response);
        assertEquals(response, client.createBooking(userId, bookingCreateDto));
    }

    @Test
    void answerBookingRequest() {
        ResponseEntity<Object> response = new ResponseEntity<>(bookingCreateDto, HttpStatus.CREATED);
        when(rest.exchange(
                anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Object>>any()))
                .thenReturn(response);
        assertEquals(response, client.answerBookingRequest(userId, 1, true));
    }

    @Test
    void getBookingStatus() {
        ResponseEntity<Object> response = new ResponseEntity<>(bookingCreateDto, HttpStatus.CREATED);
        when(rest.exchange(
                anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Object>>any()))
                .thenReturn(response);
        assertEquals(response, client.getBookingStatus(userId, 1));
    }

    @Test
    void getAllBookingsOfUser() {
        ResponseEntity<Object> response = new ResponseEntity<>(bookingCreateDto, HttpStatus.CREATED);
        when(rest.exchange(
                anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Object>>any()))
                .thenReturn(response);
        assertEquals(response, client.getAllBookingsOfUser(userId, BookingState.ALL));
    }

    @Test
    void getAllBookingsForUserItems() {
        ResponseEntity<Object> response = new ResponseEntity<>(bookingCreateDto, HttpStatus.CREATED);
        when(rest.exchange(
                anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Object>>any()))
                .thenReturn(response);
        assertEquals(response, client.getAllBookingsOfUser(userId, BookingState.ALL));
    }

    @Test
    public void createBookingErrorTest() {
        Long userId = null;
        assertThrows(Throwable.class, () -> client.createBooking(userId,
                null));
    }

    @Test
    public void answerBookingRequestErrorTest() {
        Long userId = null;
        Boolean isApproved = null;
        Long bookingId = null;
        assertThrows(Throwable.class, () -> client.answerBookingRequest(userId,
                bookingId, isApproved));
    }

    @Test
    public void getBookingStatusErrorTest() {
        Long userId = null;
        Long bookingId = null;
        assertThrows(Throwable.class, () -> client.getBookingStatus(userId,
                bookingId));
    }

    @Test
    public void getAllBookingsOfUserErrorTest() {
        Long userId = null;
        assertThrows(Throwable.class, () -> client.getAllBookingsOfUser(userId, null));
    }

    @Test
    public void getAllBookingsForUserItemsErrorTest() {
        Long userId = null;
        assertThrows(Throwable.class, () -> client.getAllBookingsForUserItems(userId, null));
    }
}
