package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidatorItemTest {

    @Mock
    private UserRepository mockUserRepository;

    @InjectMocks
    BookingServiceImpl bookingService;

    @Test
    @DisplayName("user exists")
    void validateIfUserNotExistsTest() {
        long userId = 10L;
        when(mockUserRepository.existsById(userId)).thenReturn(true);
        assertDoesNotThrow(() -> bookingService.validateIfUserNotExists(userId));
        verifyNoMoreInteractions(mockUserRepository);
    }

    @Test
    @DisplayName("user not exists")
    void validateIfUserExistsTest() {
        long userId = 10L;
        when(mockUserRepository.existsById(userId)).thenReturn(false);
        assertThrows(IdNotFoundException.class, () -> bookingService.validateIfUserNotExists(userId));
        verifyNoMoreInteractions(mockUserRepository);
    }
}