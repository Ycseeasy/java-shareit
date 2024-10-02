package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDTO;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @MockBean
    private ItemRequestClient mockItemRequestClient;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private final String userIdHeader = "X-Sharer-User-Id";
    private final ItemRequestCreateDTO request = new ItemRequestCreateDTO("description");
    final long userId = 1L;
    final long requestId = 1000L;


    @Test
    void createRequest() throws Exception {
        ResponseEntity<Object> response = new ResponseEntity<>(request, HttpStatus.CREATED);

        when(mockItemRequestClient.createItemRequest(userId, request))
                .thenReturn(response);

        mockMvc.perform(post("/requests")
                        .header(userIdHeader, userId)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description", is(request.getDescription())));
        verify(mockItemRequestClient, times(1)).createItemRequest(userId, request);
        verifyNoMoreInteractions(mockItemRequestClient);
    }

    @Test
    void getUserRequests() throws Exception {
        List<ItemRequestCreateDTO> requestList = new ArrayList<>();
        ItemRequestCreateDTO request2 = new ItemRequestCreateDTO("description2");
        requestList.add(request2);
        requestList.add(request);

        ResponseEntity<Object> response = new ResponseEntity<>(requestList, HttpStatus.OK);
        when(mockItemRequestClient.getUserRequests(userId))
                .thenReturn(response);

        mockMvc.perform(get("/requests")
                        .header(userIdHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description", is(request2.getDescription())));
        verify(mockItemRequestClient, times(1)).getUserRequests(userId);
        verifyNoMoreInteractions(mockItemRequestClient);
    }

    @Test
    void getAllRequestsExceptUser() throws Exception {
        List<ItemRequestCreateDTO> requestList = new ArrayList<>();
        ItemRequestCreateDTO request2 = new ItemRequestCreateDTO("description2");
        requestList.add(request2);
        requestList.add(request);

        ResponseEntity<Object> response = new ResponseEntity<>(requestList, HttpStatus.OK);
        when(mockItemRequestClient.getAllRequestsExceptUser(userId)).thenReturn(response);

        mockMvc.perform(get("/requests/all")
                        .header(userIdHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description", is(request2.getDescription())));
        verify(mockItemRequestClient, times(1)).getAllRequestsExceptUser(userId);
        verifyNoMoreInteractions(mockItemRequestClient);
    }

    @Test
    void getRequestById() throws Exception {
        ResponseEntity<Object> response = new ResponseEntity<>(request, HttpStatus.OK);
        when(mockItemRequestClient.getRequestById(userId, requestId)).thenReturn(response);

        mockMvc.perform(get("/requests/" + requestId)
                        .header(userIdHeader, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(request.getDescription())));
        verify(mockItemRequestClient, times(1)).getRequestById(userId, requestId);
        verifyNoMoreInteractions(mockItemRequestClient);
    }

}