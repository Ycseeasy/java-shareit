package ru.practicum.shareit.user.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.user.dto.UserCreateDTO;
import ru.practicum.shareit.user.dto.UserUpdateDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


class UserClientTest {

    private final UserCreateDTO user = new UserCreateDTO("name", "email@email");
    private final UserUpdateDTO userUpd = new UserUpdateDTO("name", "email@email");
    long userId = 1L;
    RestTemplate rest;
    UserClient client;

    @BeforeEach
    void setUp() {
        rest = Mockito.mock(RestTemplate.class);
        client = new UserClient(rest);
    }

    @Test
    void createUser() {
        ResponseEntity<Object> response = new ResponseEntity<>(user, HttpStatus.CREATED);
        when(rest.exchange(
                anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Object>>any()))
                .thenReturn(response);
        assertEquals(response, client.createUser(user));
    }

    @Test
    void updateUser() {
        ResponseEntity<Object> response = new ResponseEntity<>(userUpd, HttpStatus.CREATED);
        when(rest.exchange(
                anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Object>>any()))
                .thenReturn(response);
        assertEquals(response, client.updateUser(userId, userUpd));
    }

    @Test
    void getUser() {
        ResponseEntity<Object> response = new ResponseEntity<>(user, HttpStatus.CREATED);
        when(rest.exchange(
                anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Object>>any()))
                .thenReturn(response);
        assertEquals(response, client.getUser(userId));
    }

    @Test
    void getAllUsers() {
        ResponseEntity<Object> response = new ResponseEntity<>(user, HttpStatus.CREATED);
        when(rest.exchange(
                anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Object>>any()))
                .thenReturn(response);
        assertEquals(response, client.getAllUsers());
    }

    @Test
    public void createUserErrorTest() {
        assertThrows(Throwable.class, () -> client.createUser(null));
    }

    @Test
    public void updateUserErrorTest() {
        Long userId = null;
        assertThrows(Throwable.class, () -> client.updateUser(userId, null));
    }

    @Test
    public void getUserErrorTest() {
        Long userId = null;
        assertThrows(Throwable.class, () -> client.getUser(userId));
    }
}