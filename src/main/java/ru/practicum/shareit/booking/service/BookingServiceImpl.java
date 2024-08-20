package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.model.BookingStatus.REJECTING;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingDtoMapper bookingDtoMapper;

    @Override
    public BookingDtoOutput createBooking(long userId, BookingDtoInput newBooking) {
        validate(newBooking);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден."));
        long itemId = newBooking.getItemId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Позиция с ID " + itemId + " не найдена."));
        if (!item.isAvailable()) {
            throw new InternalServerException("Позиция с ID " + itemId + " не доступна для бронирования.");
        }
        Booking booking = bookingDtoMapper.fromDTO(user, item, newBooking);
        booking.setStatus(WAITING);
        Booking createdBooking = bookingRepository.save(booking);
        return bookingDtoMapper.toDTO(createdBooking);
    }

    @Override
    public BookingDtoOutput answerBookingRequest(long userId, long bookingId, boolean isApproved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь с ID " + bookingId + " не найдена."));
        if (booking.getItem().getOwner().getId() != userId) {
            throw new InternalServerException("Пользователь с ID " + userId + " не имеет брони с ID "
                    + bookingId + ".");
        }
        booking.setStatus(isApproved ? APPROVED : REJECTING);
        Booking savedBooking = bookingRepository.save(booking);
        return bookingDtoMapper.toDTO(savedBooking);
    }

    @Override
    public BookingDtoOutput getBookingStatus(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь с ID " + bookingId + "не найдена"));
        if ((booking.getBooker().getId() != userId) && (booking.getItem().getOwner().getId() != userId)) {
            throw new NotFoundException(String.format("Пользователь с ID " + userId + " не создавал бронь с ID "
                    + bookingId + "и не является владельцем позиции."));
        }
        return bookingDtoMapper.toDTO(booking);
    }

    @Override
    public List<BookingDtoOutput> getAllBookingsOfUser(long userId, BookingState state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден в системе.");
        }
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.getAllBookingsOfUser(userId);
            case PAST -> bookingRepository.getPastBookingsOfUser(userId);
            case CURRENT -> bookingRepository.getCurrentBookingsOfUser(userId);
            case FUTURE -> bookingRepository.getFutureBookingsOfUser(userId);
            case REJECTED -> bookingRepository.getRejectedBookingsOfUser(userId);
            case WAITING -> bookingRepository.getWaitingBookingsOfUser(userId);
        };
        return bookings.stream()
                .map(bookingDtoMapper::toDTO)
                .toList();
    }

    @Override
    public List<BookingDtoOutput> getAllBookingsForUserItems(long userId, BookingState state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден в системе.");
        }
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.getAllBookingsForUserItems(userId);
            case PAST -> bookingRepository.getPastBookingsForUserItems(userId);
            case CURRENT -> bookingRepository.getCurrentBookingsForUserItems(userId);
            case FUTURE -> bookingRepository.getFutureBookingsForUserItems(userId);
            case REJECTED -> bookingRepository.getRejectedBookingsForUserItems(userId);
            case WAITING -> bookingRepository.getWaitingBookingsForUserItems(userId);
        };
        return bookings.stream()
                .map(bookingDtoMapper::toDTO)
                .toList();
    }

    private void validate(BookingDtoInput booking) {
        if (booking.getItemId() <= 0) {
            throw new ValidationException("ID позиции должен положительным числом");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата начала бронирования не может быть в прошлом");
        }
        if (booking.getEnd().isEqual(LocalDateTime.now()) || booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата окончания бронирования не может быть в прошлом или в настоящем");
        }
    }
}
