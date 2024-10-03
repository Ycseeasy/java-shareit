package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.ObjectAlreadyExistsException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidatorUserTest {

    @Mock
    private UserRepository mockUserRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    @DisplayName("user exists")
    void validateIfUserNotExistsTest() {
        long userId = 10L;
        when(mockUserRepository.existsById(userId)).thenReturn(true);
        assertDoesNotThrow(() -> userService.validateIfUserNotExists(userId));
        verifyNoMoreInteractions(mockUserRepository);
    }

    @Test
    @DisplayName("user not exists")
    void validateIfUserExistsTest() {
        long userId = 10L;
        when(mockUserRepository.existsById(userId)).thenReturn(false);
        assertThrows(IdNotFoundException.class, () -> userService.validateIfUserNotExists(userId));
        verifyNoMoreInteractions(mockUserRepository);
    }

    @Test
    @DisplayName("email exists")
    void validateIfEmailIsUniqueTest() {
        String email = "anna@mail.ru";
        when(mockUserRepository.hasEmail(email)).thenReturn(true);
        assertThrows(ObjectAlreadyExistsException.class, () -> userService.validateIfEmailIsUnique(email));
        verifyNoMoreInteractions(mockUserRepository);
    }

    @Test
    @DisplayName("email not exists")
    void testValidateIfEmailIsUniqueTest() {
        String email = "anna@mail.ru";
        when(mockUserRepository.hasEmail(email)).thenReturn(false);
        assertDoesNotThrow(() -> userService.validateIfEmailIsUnique(email));
        verifyNoMoreInteractions(mockUserRepository);
    }

    @Test
    @DisplayName("duplicate emails")
    void validateIfEmailIsDuplicateTest() {
        long userId = 10L;
        String email = "anna@mail.ru";
        when(mockUserRepository.hasEmail(userId, email)).thenReturn(true);
        assertThrows(ObjectAlreadyExistsException.class, () -> userService.validateIfEmailIsUnique(email, userId));
        verifyNoMoreInteractions(mockUserRepository);
    }

    @Test
    @DisplayName("not duplicate emails")
    void validateIfEmailIsNotDuplicateTest() {
        long userId = 10L;
        String email = "anna@mail.ru";
        when(mockUserRepository.hasEmail(userId, email)).thenReturn(false);
        assertDoesNotThrow(() -> userService.validateIfEmailIsUnique(email, userId));
        verifyNoMoreInteractions(mockUserRepository);
    }
}