package ru.practicum.shareit.item.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.item.dto.comment.CommentCreateDTO;
import ru.practicum.shareit.item.dto.item.ItemCreateDTO;
import ru.practicum.shareit.item.dto.item.ItemUpdateDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ItemClientTest {

    private final ItemCreateDTO item = new ItemCreateDTO("name",
            "description", true, 1L);
    private final ItemUpdateDTO itemUpd = new ItemUpdateDTO("name",
            "description", true);
    private final CommentCreateDTO comment = new CommentCreateDTO("sss");
    long userId = 1L;
    long itemId = 1L;
    RestTemplate rest;
    ItemClient client;

    @BeforeEach
    void setUp() {
        rest = Mockito.mock(RestTemplate.class);
        client = new ItemClient(rest);
    }

    @Test
    void createItem() {
        ResponseEntity<Object> response = new ResponseEntity<>(item, HttpStatus.CREATED);
        when(rest.exchange(
                anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Object>>any()))
                .thenReturn(response);
        assertEquals(response, client.createItem(userId, item));
    }

    @Test
    void updateItem() {
        ResponseEntity<Object> response = new ResponseEntity<>(itemUpd, HttpStatus.CREATED);
        when(rest.exchange(
                anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Object>>any()))
                .thenReturn(response);
        assertEquals(response, client.updateItem(userId, itemId, itemUpd));
    }

    @Test
    void getItem() {
        ResponseEntity<Object> response = new ResponseEntity<>(item, HttpStatus.CREATED);
        when(rest.exchange(
                anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Object>>any()))
                .thenReturn(response);
        assertEquals(response, client.getItem(userId, itemId));
    }

    @Test
    void getAllItems() {
        ResponseEntity<Object> response = new ResponseEntity<>(item, HttpStatus.CREATED);
        when(rest.exchange(
                anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Object>>any()))
                .thenReturn(response);
        assertEquals(response, client.getAllItems(userId));
    }

    @Test
    void searchItems() {
        ResponseEntity<Object> response = new ResponseEntity<>(item, HttpStatus.CREATED);
        when(rest.exchange(
                anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Object>>any()))
                .thenReturn(response);
        assertEquals(response, client.searchItems("sss"));
    }

    @Test
    void addComment() {
        ResponseEntity<Object> response = new ResponseEntity<>(comment, HttpStatus.CREATED);
        when(rest.exchange(
                anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Object>>any()))
                .thenReturn(response);
        assertEquals(response, client.addComment(userId, itemId, comment));
    }

    @Test
    public void createItemErrorTest() {
        Long userId = null;
        assertThrows(Throwable.class, () -> client.createItem(userId, null));
    }

    @Test
    public void updateItemErrorTest() {
        Long userId = null;
        Long itemId = null;
        assertThrows(Throwable.class, () -> client.updateItem(userId, itemId, null));
    }

    @Test
    public void getItemErrorTest() {
        Long userId = null;
        Long itemId = null;
        assertThrows(Throwable.class, () -> client.getItem(userId, itemId));
    }

    @Test
    public void getAllItemsErrorTest() {
        Long userId = null;
        assertThrows(Throwable.class, () -> client.getAllItems(userId));
    }

    @Test
    public void searchItemsErrorTest() {
        assertThrows(Throwable.class, () -> client.searchItems("null"));
    }

    @Test
    public void addCommentErrorTest() {
        Long userId = null;
        Long itemId = null;
        assertThrows(Throwable.class, () -> client.addComment(userId, itemId, null));
    }
}