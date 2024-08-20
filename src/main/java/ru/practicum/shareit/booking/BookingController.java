package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDtoOutput createBooking(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                          @RequestBody BookingDtoInput booking) {
        return bookingService.createBooking(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOutput answerBookingRequest(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                           @PathVariable("bookingId") @Positive long bookingId,
                                           @RequestParam(name = "approved") boolean isApproved) {
        return bookingService.answerBookingRequest(userId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOutput getBookingStatus(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                       @PathVariable("bookingId") @Positive long bookingId) {
        return bookingService.getBookingStatus(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoOutput> getAllBookingsByUser(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                                 @RequestParam(name = "state",
                                                         required = false,
                                                         defaultValue = "ALL") BookingState state) {
        return bookingService.getAllBookingsOfUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoOutput> getAllBookingsForUserItems(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                                       @RequestParam(name = "state",
                                                               required = false,
                                                               defaultValue = "ALL") @NotNull BookingState state) {
        return bookingService.getAllBookingsForUserItems(userId, state);
    }

}
