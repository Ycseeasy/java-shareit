package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidatorItemTest {

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private ItemRepository mockItemRepository;

    @Mock
    private BookingRepository mockBookingRepository;

    @InjectMocks
    ItemServiceImpl itemService;

    @Test
    @DisplayName("user exists")
    void validateIfUserNotExistsTest() {
        long userId = 10L;
        when(mockUserRepository.existsById(userId)).thenReturn(true);
        assertDoesNotThrow(() -> itemService.validateIfUserNotExists(userId));
        verifyNoMoreInteractions(mockUserRepository);
    }

    @Test
    @DisplayName("user not exists")
    void validateIfUserExistsTest() {
        long userId = 10L;
        when(mockUserRepository.existsById(userId)).thenReturn(false);
        assertThrows(IdNotFoundException.class, () -> itemService.validateIfUserNotExists(userId));
        verifyNoMoreInteractions(mockUserRepository);
    }

    @Test
    @DisplayName("user booked item")
    void validateIfUserBookedItemTest() {
        long userId = 121L;
        long itemId = 312L;
        when(mockBookingRepository.isUserBookedItem(userId, itemId)).thenReturn(true);
        assertDoesNotThrow(() -> itemService.validateIfUserBookedItem(userId, itemId));
        verifyNoMoreInteractions(mockBookingRepository);
    }

    @Test
    @DisplayName("user did not book item")
    void validateIfUserDidNotBookItemTest() {
        long userId = 121L;
        long itemId = 312L;
        when(mockBookingRepository.isUserBookedItem(userId, itemId)).thenReturn(false);
        assertThrows(InternalServerException.class, () -> itemService.validateIfUserBookedItem(userId, itemId));
        verifyNoMoreInteractions(mockBookingRepository);
    }

    @Test
    @DisplayName("user owns item")
    void validateIfUserOwnsItemTest() {
        long userId = 121L;
        long itemId = 312L;
        when(mockItemRepository.existsByIdAndOwner(itemId, userId)).thenReturn(true);
        assertDoesNotThrow(() -> itemService.validateIfUserOwnsItem(userId, itemId));
        verifyNoMoreInteractions(mockBookingRepository);
    }

    @Test
    @DisplayName("user does not own item")
    void validateIfUserDoesNotOwnItemTest() {
        long userId = 121L;
        long itemId = 312L;
        when(mockItemRepository.existsByIdAndOwner(itemId, userId)).thenReturn(false);
        assertThrows(IdNotFoundException.class, () -> itemService.validateIfUserOwnsItem(userId, itemId));
        verifyNoMoreInteractions(mockBookingRepository);
    }
}