package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingDTOMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
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
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public BookingDto createBooking(long userId, BookingCreateDto newBooking) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException(String.format("User with id=%d does not exists", userId)));
        long itemId = newBooking.getItemId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IdNotFoundException(String.format("Item with id=%d does not exists", itemId)));
        if (!item.isAvailable()) {
            throw new InternalServerException(String.format("Item with id=%d is not available", itemId));
        }
        Booking booking = BookingDTOMapper.fromCreateDTO(user, item, newBooking);
        booking.setStatus(WAITING);
        Booking createdBooking = bookingRepository.save(booking);
        log.info("{} was created", createdBooking);
        Collection<Comment> itemComments = commentRepository.findByItemId(item.getId());
        return BookingDTOMapper.toDTO(createdBooking, itemComments);
    }

    @Transactional
    @Override
    public BookingDto answerBookingRequest(long userId, long bookingId, boolean isApproved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IdNotFoundException(
                        String.format("Booking with id=%d does not exists", bookingId)));
        if (booking.getItem().getOwner().getId() != userId) {
            throw new InternalServerException(
                    String.format("User with id=%d does not have booking with id=%d", userId, bookingId));
        }
        booking.setStatus(isApproved ? APPROVED : REJECTED);
        Booking savedBooking = bookingRepository.save(booking);
        log.info("User saved answer {} for {} ", isApproved, savedBooking);
        Collection<Comment> itemComments = commentRepository.findByItemId(savedBooking.getItem().getId());
        return BookingDTOMapper.toDTO(savedBooking, itemComments);
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public BookingDto getBookingStatus(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IdNotFoundException(
                        String.format("Booking with id=%d does not exists", bookingId)));
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new IdNotFoundException(
                    String.format("User with id=%d is not owner or booker for booking with id=%d", userId, bookingId));
        }
        Collection<Comment> itemComments = commentRepository.findByItemId(booking.getItem().getId());
        return BookingDTOMapper.toDTO(booking, itemComments);
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public List<BookingDto> getAllBookingsOfUser(long userId, BookingState state) {
        validateIfUserNotExists(userId);
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
                .map((Booking booking) -> {
                    Collection<Comment> itemComments = commentRepository.findByItemId(booking.getItem().getId());
                    return BookingDTOMapper.toDTO(booking, itemComments);
                })
                .toList();

    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public List<BookingDto> getAllBookingsForUserItems(long userId, BookingState state) {
        validateIfUserNotExists(userId);
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.getAllBookingsForUserItems(userId);
            case PAST -> bookingRepository.getPastBookingsForUserItems(userId);
            case CURRENT -> bookingRepository.getCurrentBookingsForUserItems(userId);
            case FUTURE -> bookingRepository.getFutureBookingsForUserItems(userId);
            case REJECTED -> bookingRepository.getRejectedBookingsForUserItems(userId);
            case WAITING -> bookingRepository.getWaitingBookingsForUserItems(userId);
        };
        return bookings.stream()
                .map((Booking booking) -> {
                    Collection<Comment> itemComments = commentRepository.findByItemId(booking.getItem().getId());
                    return BookingDTOMapper.toDTO(booking, itemComments);
                })
                .toList();
    }

    public void validateIfUserNotExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IdNotFoundException(String.format("User with id=%d does not exists", userId));
        }
    }
}
