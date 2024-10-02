package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ItemRequestRepository requestRepository;

    User user;
    ItemRequest request;
    Item item;
    Comment comment;

    @BeforeEach
    void addComment() {
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

        comment = commentRepository.save(Comment.builder()
                .text("qwerty")
                .created(LocalDateTime.now())
                .item(item)
                .author(user)
                .build());
    }

    @Test
    void findByItemId() {
        List<Comment> result = commentRepository.findByItemId(item.getId())
                .stream()
                .toList();
        Comment commentResult = result.getFirst();
        assertEquals(commentResult, comment);
    }

    @Test
    void findByItemIdIn() {
        List<Comment> result = commentRepository.findByItemIdIn(List.of(0L, 1L, 2L, 3L))
                .stream()
                .toList();
        Comment commentResult = result.getFirst();
        assertEquals(commentResult, comment);
    }

    @AfterEach
    void clear() {
        itemRepository.deleteAll();
        requestRepository.deleteAll();
        userRepository.deleteAll();
        commentRepository.deleteAll();
    }
}
