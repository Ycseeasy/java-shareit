package ru.practicum.shareit.user.repositiry;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void addUsers() {
        userRepository.save(User.builder()
                .email("email")
                .name("name")
                .build());
    }

    @Test
    void hasEmailTest() {
        boolean hasEmail = userRepository.hasEmail("email");
        assertTrue(hasEmail);
        boolean hasEmail2 = userRepository.hasEmail(0, "email");
        assertTrue(hasEmail2);
    }

    @AfterEach
    public void deleteUsers() {
        userRepository.deleteAll();
    }
}
