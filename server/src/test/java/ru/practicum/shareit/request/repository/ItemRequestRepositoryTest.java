package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository repository;

    @Test
    void findByIdTest() {
    }

    @Test
    void findByRequestorIdOrderByCreatedAsc() {
    }

    @Test
    void findByRequestorIdNotOrderByCreatedAsc() {
    }
}
