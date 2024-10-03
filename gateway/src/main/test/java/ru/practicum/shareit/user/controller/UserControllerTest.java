package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserCreateDTO;
import ru.practicum.shareit.user.dto.UserUpdateDTO;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @MockBean
    private UserClient mockedUserClient;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private final UserCreateDTO user = new UserCreateDTO("Anna", "anna@mail.ru");
    final long userId = 2;

    @Test
    void createUserTest() throws Exception {
        ResponseEntity<Object> response = new ResponseEntity<>(user, HttpStatus.CREATED);
        when(mockedUserClient.createUser(user))
                .thenReturn(response);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.name", is(user.getName())));
        verify(mockedUserClient, times(1)).createUser(user);
        verifyNoMoreInteractions(mockedUserClient);
    }

    @Test
    void updateCorrectUserTest() throws Exception {
        UserUpdateDTO updateDTO = new UserUpdateDTO("Anna", "anna@mail.ru");
        ResponseEntity<Object> response = new ResponseEntity<>(updateDTO, HttpStatus.OK);
        when(mockedUserClient.updateUser(userId, updateDTO))
                .thenReturn(response);

        mockMvc.perform(patch("/users/" + userId)
                        .content(mapper.writeValueAsString(updateDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(updateDTO.getEmail())))
                .andExpect(jsonPath("$.name", is(updateDTO.getName())));
        verify(mockedUserClient, times(1)).updateUser(userId, updateDTO);
        verifyNoMoreInteractions(mockedUserClient);
    }

    @Test
    void deleteUserTest() throws Exception {
        mockMvc.perform(delete("/users/" + userId))
                .andExpect(status().isNoContent());
        verify(mockedUserClient, times(1)).deleteUser(userId);
        verifyNoMoreInteractions(mockedUserClient);
    }

    @Test
    void getCorrectUser() throws Exception {
        ResponseEntity<Object> response = new ResponseEntity<>(user, HttpStatus.OK);
        when(mockedUserClient.getUser(userId))
                .thenReturn(response);

        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.name", is(user.getName())));
        verify(mockedUserClient, times(1)).getUser(userId);
        verifyNoMoreInteractions(mockedUserClient);
    }

    @Test
    void getAllUsers() throws Exception {
        List<UserCreateDTO> userList = new ArrayList<>();
        UserCreateDTO user2 = new UserCreateDTO("Anna2", "anna2@mail.ru");
        userList.add(user2);
        userList.add(user);
        ResponseEntity<Object> response = new ResponseEntity<>(userList, HttpStatus.OK);
        when(mockedUserClient.getAllUsers())
                .thenReturn(response);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email", is(user2.getEmail())))
                .andExpect(jsonPath("$[0].name", is(user2.getName())));
        verify(mockedUserClient, times(1)).getAllUsers();
        verifyNoMoreInteractions(mockedUserClient);
    }
}