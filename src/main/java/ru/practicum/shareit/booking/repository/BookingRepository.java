package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartAsc(long userId);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartAsc(long userId, LocalDateTime z);

    List<Booking> findByBookerIdAndStartAfterAndEndBeforeOrderByStartAsc(long userId, LocalDateTime y,
                                                                         LocalDateTime z);

    List<Booking> findByBookerIdAndStartAfterOrderByStartAsc(long userId, LocalDateTime z);

    List<Booking> findByBookerIdAndStatusOrderByStartAsc(long userId, BookingStatus status);


    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN FETCH b.item AS i " +
            "WHERE i.owner.id=:userId " +
            "ORDER BY b.start ASC")
    List<Booking> getAllBookingsForUserItems(long userId);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN FETCH b.item AS i " +
            "WHERE i.owner.id=:userId " +
            "AND b.end<CURRENT_TIMESTAMP " +
            "ORDER BY b.start ASC")
    List<Booking> getPastBookingsForUserItems(long userId);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN FETCH b.item AS i " +
            "WHERE i.owner.id=:userId " +
            "AND b.start>CURRENT_TIMESTAMP " +
            "AND b.end<CURRENT_TIMESTAMP " +
            "ORDER BY b.start ASC")
    List<Booking> getCurrentBookingsForUserItems(long userId);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN FETCH b.item AS i " +
            "WHERE i.owner.id=:userId " +
            "AND b.start>CURRENT_TIMESTAMP " +
            "ORDER BY b.start ASC")
    List<Booking> getFutureBookingsForUserItems(long userId);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN FETCH b.item AS i " +
            "WHERE i.owner.id=:userId " +
            "AND b.status='REJECTED' " +
            "ORDER BY b.start ASC")
    List<Booking> getRejectedBookingsForUserItems(long userId);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN FETCH b.item AS i " +
            "WHERE i.owner.id=:userId " +
            "AND b.status='WAITING' " +
            "ORDER BY b.start ASC")
    List<Booking> getWaitingBookingsForUserItems(long userId);

    @Query("SELECT CASE WHEN COUNT(b)> 0 THEN TRUE ELSE FALSE END " +
            "FROM Booking AS b " +
            "WHERE b.booker.id=:userId " +
            "AND b.item.id=:itemId " +
            "AND b.end<CURRENT_TIMESTAMP")
    boolean isUserBookedItem(long userId, long itemId);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN FETCH b.item AS i " +
            "WHERE i.owner.id=:userId " +
            "AND i.id = :itemId " +
            "AND b.end < CURRENT_TIMESTAMP " +
            "ORDER BY b.end DESC " +
            "LIMIT 1")
    Optional<Booking> getLastBookingForItemOwnedByUser(long userId, long itemId);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "JOIN FETCH b.item AS i " +
            "WHERE i.owner.id=:userId " +
            "AND i.id = :itemId " +
            "AND b.start > CURRENT_TIMESTAMP " +
            "AND b.status != 'REJECTED' " +
            "ORDER BY b.start ASC " +
            "LIMIT 1")
    Optional<Booking> getNextBookingForItemOwnedByUser(long userId, long itemId);

    List<Booking> findByItemIdInAndStatusNotOrderByStartAsc(Collection<Long> listId, BookingStatus status);
}
