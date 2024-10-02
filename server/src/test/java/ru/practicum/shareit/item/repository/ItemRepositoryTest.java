package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRequestRepository requestRepository;

    User user;
    ItemRequest request;
    Item item;

    @BeforeEach
    void addItem() {
        user = userRepository.save(User.builder()
                .email("email")
                .name("name")
                .build());

        request = requestRepository.save(ItemRequest.builder()
                .created(LocalDateTime.now())
                .description("sss")
                .requestor(user)
                .build());

        item = itemRepository.save(Item.builder()
                .name("name")
                .description("qwerty")
                .owner(user)
                .request(request)
                .available(true)
                .build());
    }

    @Test
    void findByOwnerId() {
        Collection<Item> result = itemRepository.findByOwnerId(user.getId());
        List<Item> itemList = result.stream().toList();
        Item item = itemList.getFirst();
        assertEquals(item.getOwner().getId(), user.getId());
    }

    @Test
    void searchItemsTest() {
        Collection<Item> result = itemRepository.searchItems("qwerty");
        List<Item> itemList = result.stream().toList();
        Item itemResult = itemList.getFirst();
        assertEquals(itemResult.getDescription(), "qwerty");

        Collection<Item> result2 = itemRepository.searchItems("name");
        List<Item> itemList2 = result.stream().toList();
        Item itemResult2 = itemList.getFirst();
        assertEquals(itemResult2.getName(), "name");
    }

    @Test
    void existsByIdAndOwnerTest() {
        boolean isExist = itemRepository.existsByIdAndOwner(item.getId(), user.getId());
        assertTrue(isExist);
    }

    @Test
    void findByRequestId() {
        List<Item> result = itemRepository.findByRequestId(request.getId());
        Item itemResult = result.getFirst();
        assertEquals(item, itemResult);
    }

    @Test
    void findByRequestIdIn() {
        List<Long> requestIds = Arrays.asList(0L, 1L, 2L, 3L, 4L);
        List<Item> result = itemRepository.findByRequestIdIn(requestIds);
        Item itemResult = result.getFirst();
        assertEquals(item, itemResult);
    }

    @AfterEach
    void clear() {
        requestRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

}
