package ru.practicum.shareit.item.service;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.dto.item.ItemCreateDTO;
import ru.practicum.shareit.item.dto.item.ItemDTO;
import ru.practicum.shareit.item.dto.item.ItemDTOWithBookings;
import ru.practicum.shareit.item.dto.item.ItemUpdateDTO;
import ru.practicum.shareit.request.dto.ItemRequestCreateDTO;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.UserCreateDTO;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ItemServiceImplTest {

    private final UserServiceImpl userService;
    private final ItemRequestServiceImpl itemRequestService;
    private final ItemServiceImpl itemService;

    @Test
    void createItem() {
        long userId = userService.createUser(new UserCreateDTO("ss", "ss@ss")).getId();
        long itemRequestId = itemRequestService.createItemRequest(userId,
                new ItemRequestCreateDTO("ssss")).getId();
        ItemDTO item = itemService.createItem(userId,
                new ItemCreateDTO("sss", "ss", true, itemRequestId));
        assertEquals("sss", item.getName());
        assertEquals("ss", item.getDescription());
    }

    @Test
    void updateItem() {
        long userId = userService.createUser(new UserCreateDTO("ss", "ss@ss")).getId();
        long itemRequestId = itemRequestService.createItemRequest(userId,
                new ItemRequestCreateDTO("ssss")).getId();
        ItemDTO item = itemService.createItem(userId,
                new ItemCreateDTO("sss", "ss", true, itemRequestId));
        ItemDTO updatedItem = itemService.updateItem(userId, item.getId(),
                new ItemUpdateDTO("qwer", "rewq", true));

        assertEquals("qwer", updatedItem.getName());
        assertEquals("rewq", updatedItem.getDescription());
    }

    @Test
    void getItem() {
        long userId = userService.createUser(new UserCreateDTO("ss", "ss@ss")).getId();
        long itemRequestId = itemRequestService.createItemRequest(userId,
                new ItemRequestCreateDTO("ssss")).getId();
        long itemId = itemService.createItem(userId,
                new ItemCreateDTO("sss", "ss", true, itemRequestId)).getId();

        ItemDTOWithBookings search = itemService.getItem(userId, itemId);

        assertEquals("sss", search.getName());
        assertEquals("ss", search.getDescription());
    }

    @Test
    void getIncorrectItem() {
        assertThrows(IdNotFoundException.class, () -> itemService.getItem(1L, 2L));
    }

    @Test
    void getAllItems() {
        long userId = userService.createUser(new UserCreateDTO("ss", "ss@ss")).getId();
        long itemRequestId1 = itemRequestService.createItemRequest(userId,
                new ItemRequestCreateDTO("1")).getId();
        long itemRequestId2 = itemRequestService.createItemRequest(userId,
                new ItemRequestCreateDTO("2")).getId();
        long itemRequestId3 = itemRequestService.createItemRequest(userId,
                new ItemRequestCreateDTO("3")).getId();
        itemService.createItem(userId, new ItemCreateDTO("1", "1", true, itemRequestId1));
        itemService.createItem(userId, new ItemCreateDTO("2", "2", true, itemRequestId2));
        itemService.createItem(userId, new ItemCreateDTO("3", "3", true, itemRequestId2));

        Collection<ItemDTOWithBookings> items = itemService.getAllItems(userId);

        assertEquals(items.size(), 3);
    }
}
