package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingState;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingClient bookingClient;

    @Autowired
    private MockMvc mvc;

    private final BookingCreateDto bookingCreateDto = new BookingCreateDto(
            1L,
            LocalDateTime.of(2024, 12, 1, 12, 12, 12),
            LocalDateTime.of(2025, 12, 1, 12, 12, 12)
    );

    @Test
    void createBooking() throws Exception {
        ResponseEntity<Object> response = new ResponseEntity<>(bookingCreateDto, HttpStatus.CREATED);
        when(bookingClient.createBooking(anyLong(), any()))
                .thenReturn(response);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(bookingCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemId", is(bookingCreateDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingCreateDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingCreateDto.getEnd().toString())));
        verify(bookingClient, atMost(3)).createBooking(1L, bookingCreateDto);
    }

    @Test
    void answerBookingRequest() throws Exception {
        ResponseEntity<Object> response = new ResponseEntity<>(bookingCreateDto, HttpStatus.OK);
        boolean isApproved = true;
        long bookingId = 1;

        when(bookingClient.answerBookingRequest(1L, bookingId, isApproved))
                .thenReturn(response);

        mvc.perform(patch("/bookings/" + bookingId + "?approved=" + isApproved)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId", is(bookingCreateDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingCreateDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingCreateDto.getEnd().toString())));
        verify(bookingClient, atMost(3)).createBooking(1L, bookingCreateDto);
    }

    @Test
    void getBookingStatus() throws Exception {
        ResponseEntity<Object> response = new ResponseEntity<>(bookingCreateDto, HttpStatus.OK);
        long bookingId = 1;

        when(bookingClient.getBookingStatus(1L, bookingId))
                .thenReturn(response);

        mvc.perform(get("/bookings/" + bookingId)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId", is(bookingCreateDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingCreateDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingCreateDto.getEnd().toString())));
        verify(bookingClient, atMost(3)).createBooking(1L, bookingCreateDto);
    }

    @Test
    void getAllBookingsByUser() throws Exception {
        List<BookingCreateDto> bookingList = new ArrayList<>();
        BookingCreateDto bookingCreateDto2 = new BookingCreateDto(
                2L,
                LocalDateTime.of(2024, 12, 1, 12, 12, 12),
                LocalDateTime.of(2025, 12, 1, 12, 12, 12)
        );
        bookingList.add(bookingCreateDto2);
        bookingList.add(bookingCreateDto);

        ResponseEntity<Object> response = new ResponseEntity<>(bookingList, HttpStatus.OK);
        BookingState state = BookingState.ALL;


        when(bookingClient.getAllBookingsOfUser(1L, BookingState.ALL))
                .thenReturn(response);

        mvc.perform(get("/bookings?state=" + state.toString())
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].itemId", is(bookingCreateDto2.getItemId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingCreateDto2.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingCreateDto2.getEnd().toString())));
        verify(bookingClient, atMost(3)).createBooking(1L, bookingCreateDto);
    }

    @Test
    void getAllBookingsForUserItems() throws Exception {
        List<BookingCreateDto> bookingList = new ArrayList<>();
        BookingCreateDto bookingCreateDto2 = new BookingCreateDto(
                2L,
                LocalDateTime.of(2024, 12, 1, 12, 12, 12),
                LocalDateTime.of(2025, 12, 1, 12, 12, 12)
        );
        bookingList.add(bookingCreateDto2);
        bookingList.add(bookingCreateDto);

        ResponseEntity<Object> response = new ResponseEntity<>(bookingList, HttpStatus.OK);
        BookingState state = BookingState.ALL;


        when(bookingClient.getAllBookingsForUserItems(1L, BookingState.ALL))
                .thenReturn(response);

        mvc.perform(get("/bookings/owner?state=" + state.toString())
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].itemId", is(bookingCreateDto2.getItemId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingCreateDto2.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingCreateDto2.getEnd().toString())));
        verify(bookingClient, atMost(3)).createBooking(1L, bookingCreateDto);
    }
}
