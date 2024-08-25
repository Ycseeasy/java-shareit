package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InvalidUserException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDtoOutput;
import ru.practicum.shareit.item.dto.CommentMapperOut;
import ru.practicum.shareit.item.dto.ItemDtoOutput;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.model.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapperIn bookingMapperIn;
    private final BookingMapperOut bookingMapperOut;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final CommentRepository commentRepository;
    private final CommentMapperOut commentMapperOut;

    @Override
    public BookingDtoOutput createBooking(long userId, BookingDtoInput newBooking) {
        validate(newBooking);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден."));
        long itemId = newBooking.getItemId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Позиция с ID " + itemId + " не найдена."));
        if (!item.isAvailable()) {
            throw new ValidationException("Позиция с ID " + itemId + " не доступна для бронирования.");
        }
        Booking booking = bookingMapperIn.formDTO(newBooking, item, user);
        booking.setStatus(WAITING);
        Booking createdBooking = bookingRepository.save(booking);
        return createDTO(user, item, createdBooking);
    }

    @Override
    public BookingDtoOutput answerBookingRequest(long userId, long bookingId, boolean isApproved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь с ID " + bookingId + " не найдена."));
        if (booking.getItem().getOwner().getId() != userId) {
            throw new InvalidUserException("Пользователь с ID " + userId + " не имеет брони с ID "
                    + bookingId + ".");
        }
        booking.setStatus(isApproved ? APPROVED : REJECTED);
        Booking savedBooking = bookingRepository.save(booking);
        return createDTO(savedBooking.getBooker(), savedBooking.getItem(), savedBooking);
    }

    @Override
    public BookingDtoOutput getBookingStatus(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь с ID " + bookingId + "не найдена"));
        if ((booking.getBooker().getId() != userId) && (booking.getItem().getOwner().getId() != userId)) {
            throw new NotFoundException(String.format("Пользователь с ID " + userId + " не создавал бронь с ID "
                    + bookingId + "и не является владельцем позиции."));
        }
        return createDTO(booking.getBooker(), booking.getItem(), booking);
    }

    @Override
    public List<BookingDtoOutput> getAllBookingsOfUser(long userId, BookingState state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден в системе.");
        }
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByBookerIdOrderByStartAsc(userId);
            case PAST -> bookingRepository.findByBookerIdAndEndBeforeOrderByStartAsc(userId, LocalDateTime.now());
            case CURRENT -> bookingRepository.findByBookerIdAndStartAfterAndEndBeforeOrderByStartAsc(userId,
                    LocalDateTime.now(), LocalDateTime.now());
            case FUTURE -> bookingRepository.findByBookerIdAndStartAfterOrderByStartAsc(userId, LocalDateTime.now());
            case REJECTED -> bookingRepository.findByBookerIdAndStatusOrderByStartAsc(userId, REJECTED);
            case WAITING -> bookingRepository.findByBookerIdAndStatusOrderByStartAsc(userId, WAITING);
        };
        return bookings.stream()
                .map(booking -> createDTO(booking.getBooker(), booking.getItem(), booking))
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
                .map(booking -> createDTO(booking.getBooker(), booking.getItem(), booking))
                .toList();
    }

    private void validate(BookingDtoInput booking) {
        if (booking.getStart() == null) {
            throw new ValidationException("Дата начала бронирования обязательна.");
        }
        if (booking.getEnd() == null) {
            throw new ValidationException("Дата окончания бронирования обязательна");
        }
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

    private BookingDtoOutput createDTO(User user, Item item, Booking booking) {
        UserDto userDto = userMapper.toDTO(user);
        Collection<CommentDtoOutput> commentsDto = commentRepository
                .findByItemId(item.getId())
                .stream()
                .map(commentMapperOut::toDTO)
                .toList();
        ItemDtoOutput itemDtoOutput = itemMapper.toDTO(item, commentsDto);
        return bookingMapperOut.toDTO(booking, itemDtoOutput, userDto);
    }
}
