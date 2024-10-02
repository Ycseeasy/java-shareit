package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void builderTest() {
        User user = User.builder()
                .id(1L)
                .name("ss")
                .email("sss")
                .build();

        User user1 = new User(1L, "ss", "sss");
        assertEquals(user, user1);

        assertEquals(user.getId(), 1L);
        assertEquals(user.getName(), "ss");
        assertEquals(user.getEmail(), "sss");
    }
}