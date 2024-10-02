package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    BookingRepository repository;

    @Test
    void findByBookerIdOrderByStartAscTest() {

    }

    @Test
    void findByBookerIdAndEndBeforeOrderByStartAscTest() {

    }

    @Test
    void findByBookerIdAndStartAfterAndEndBeforeOrderByStartAscTest() {

    }

    @Test
    void findByBookerIdAndStartAfterOrderByStartAscTest() {

    }

    @Test
    void findByBookerIdAndStatusOrderByStartAscTest() {

    }

}
