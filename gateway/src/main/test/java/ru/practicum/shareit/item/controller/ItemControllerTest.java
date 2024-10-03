package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.comment.CommentCreateDTO;
import ru.practicum.shareit.item.dto.item.ItemCreateDTO;
import ru.practicum.shareit.item.dto.item.ItemUpdateDTO;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @MockBean
    private ItemClient itemClient;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    final String userIdHeader = "X-Sharer-User-Id";
    final long userId = 112L;
    final long itemId = 23123L;
    final ItemCreateDTO item = new ItemCreateDTO("nam", "descripti", true, 1L);

    @Test
    void createItem() throws Exception {
        ResponseEntity<Object> response = new ResponseEntity<>(item, HttpStatus.CREATED);
        when(itemClient.createItem(userId, item))
                .thenReturn(response);

        mockMvc.perform(post("/items")
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(item.getRequestId()), Long.class));
        verify(itemClient, times(1)).createItem(userId, item);
        verifyNoMoreInteractions(itemClient);
    }

    @Test
    void updateItem() throws Exception {
        ItemUpdateDTO updateItem = new ItemUpdateDTO("upd", "upd", true);
        ResponseEntity<Object> response = new ResponseEntity<>(updateItem, HttpStatus.OK);
        when(itemClient.updateItem(userId, itemId, updateItem))
                .thenReturn(response);

        mockMvc.perform(patch("/items/" + itemId)
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(updateItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(updateItem.getName())))
                .andExpect(jsonPath("$.description", is(updateItem.getDescription())))
                .andExpect(jsonPath("$.available", is(updateItem.getAvailable()), Boolean.class));
        verify(itemClient, times(1)).updateItem(userId, itemId, updateItem);
        verifyNoMoreInteractions(itemClient);
    }

    @Test
    void getItem() throws Exception {
        ResponseEntity<Object> response = new ResponseEntity<>(item, HttpStatus.OK);
        when(itemClient.getItem(userId, itemId))
                .thenReturn(response);

        mockMvc.perform(get("/items/" + itemId)
                        .header(userIdHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(item.getRequestId()), Long.class));
        verify(itemClient, times(1)).getItem(userId, itemId);
        verifyNoMoreInteractions(itemClient);
    }

    @Test
    void getAllItems() throws Exception {
        List<ItemCreateDTO> itemList = new ArrayList<>();
        ItemCreateDTO item2 = new ItemCreateDTO("name2", "description2", true, 2L);
        itemList.add(item2);
        itemList.add(item);
        ResponseEntity<Object> response = new ResponseEntity<>(itemList, HttpStatus.OK);
        when(itemClient.getAllItems(userId))
                .thenReturn(response);

        mockMvc.perform(get("/items")
                        .header(userIdHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(item2.getName())))
                .andExpect(jsonPath("$[0].description", is(item2.getDescription())))
                .andExpect(jsonPath("$[0].available", is(item2.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].requestId", is(item2.getRequestId()), Long.class));
        verify(itemClient, times(1)).getAllItems(userId);
        verifyNoMoreInteractions(itemClient);
    }

    @Test
    void searchItems() throws Exception {
        List<ItemCreateDTO> itemList = new ArrayList<>();
        ItemCreateDTO item2 = new ItemCreateDTO("name2", "description2", true, 2L);
        itemList.add(item2);
        itemList.add(item);
        ResponseEntity<Object> response = new ResponseEntity<>(itemList, HttpStatus.OK);
        when(itemClient.searchItems("sssss")).thenReturn(response);

        mockMvc.perform(get("/items/search?text=sssss")
                        .header(userIdHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(item2.getName())))
                .andExpect(jsonPath("$[0].description", is(item2.getDescription())))
                .andExpect(jsonPath("$[0].available", is(item2.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].requestId", is(item2.getRequestId()), Long.class));
        verify(itemClient, times(1)).searchItems("sssss");
        verifyNoMoreInteractions(itemClient);
    }

    @Test
    void addComment() throws Exception {
        CommentCreateDTO comment = new CommentCreateDTO("saxsdxa");
        ResponseEntity<Object> response = new ResponseEntity<>(comment, HttpStatus.CREATED);
        when(itemClient.addComment(userId, itemId, comment)).thenReturn(response);

        mockMvc.perform(post("/items/" + itemId + "/comment")
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text", is(comment.getText())));
        verify(itemClient, times(1)).addComment(userId, itemId, comment);
        verifyNoMoreInteractions(itemClient);
    }
}