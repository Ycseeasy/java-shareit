package ru.practicum.shareit.request.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.request.dto.ItemRequestCreateDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ItemRequestClientTest {

    private final ItemRequestCreateDTO request = new ItemRequestCreateDTO("sss");
    long userId = 1L;
    long requestId = 1L;
    RestTemplate rest;
    ItemRequestClient client;

    @BeforeEach
    void setUp() {
        rest = Mockito.mock(RestTemplate.class);
        client = new ItemRequestClient(rest);
    }

    @Test
    void createItemRequest() {
        ResponseEntity<Object> response = new ResponseEntity<>(request, HttpStatus.CREATED);
        when(rest.exchange(
                anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Object>>any()))
                .thenReturn(response);
        assertEquals(response, client.createItemRequest(userId, request));
    }

    @Test
    void getUserRequests() {
        ResponseEntity<Object> response = new ResponseEntity<>(request, HttpStatus.CREATED);
        when(rest.exchange(
                anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Object>>any()))
                .thenReturn(response);
        assertEquals(response, client.getUserRequests(userId));
    }

    @Test
    void getAllRequestsExceptUser() {
        ResponseEntity<Object> response = new ResponseEntity<>(request, HttpStatus.CREATED);
        when(rest.exchange(
                anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Object>>any()))
                .thenReturn(response);
        assertEquals(response, client.getAllRequestsExceptUser(userId));
    }

    @Test
    void getRequestById() {
        ResponseEntity<Object> response = new ResponseEntity<>(request, HttpStatus.CREATED);
        when(rest.exchange(
                anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Object>>any()))
                .thenReturn(response);
        assertEquals(response, client.getRequestById(userId, requestId));
    }

    @Test
    public void createItemRequestErrorTest() {
        Long userId = null;
        assertThrows(Throwable.class, () -> client.createItemRequest(userId,
                null));
    }

    @Test
    public void getUserRequestsErrorTest() {
        Long userId = null;
        assertThrows(Throwable.class, () -> client.getUserRequests(userId));
    }

    @Test
    public void getAllRequestsExceptUserErrorTest() {
        Long userId = null;
        assertThrows(Throwable.class, () -> client.getAllRequestsExceptUser(userId));
    }

    @Test
    public void getRequestByIdErrorTest() {
        Long userId = null;
        Long requestId = null;
        assertThrows(Throwable.class, () -> client.getRequestById(userId,
                requestId));
    }
}