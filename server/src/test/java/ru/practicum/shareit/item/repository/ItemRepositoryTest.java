package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    void searchItemsTest() {
    }

    @Test
    void existsByIdAndOwnerTest() {
    }

    @Test
    void findByRequestId() {
    }

    @Test
    void findByRequestIdIn() {
    }
}
