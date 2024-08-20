package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingDtoOutput createBooking(long userId, BookingDtoInput booking);

    BookingDtoOutput answerBookingRequest(long userId, long bookingId, boolean isApproved);

    BookingDtoOutput getBookingStatus(long userId, long bookingId);

    List<BookingDtoOutput> getAllBookingsOfUser(long userId, BookingState state);

    List<BookingDtoOutput> getAllBookingsForUserItems(long userId, BookingState state);

}
