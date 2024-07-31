package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public User createUser(@RequestBody @Valid User user) {
        log.info("""
                Создание пользователя
                Имя {}
                Почта {}""", user.getName(), user.getEmail());
        return userService.createUser(user);
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@PathVariable @NonNull Long userId, @RequestBody @Valid User user) {
        log.info("""
                Обновление данных пользователя
                ID {}
                Имя {}
                Почта {}""", userId, user.getName(), user.getEmail());
        return userService.updateUser(userId, user);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable @NonNull Long userId) {
        log.info("""
                Удаление данных пользователя
                ID {}
                """, userId);
        userService.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User getUser(@PathVariable @NonNull Long userId) {
        log.info("""
                Поиск данных пользователя
                ID {}
                """, userId);
        return userService.getUser(userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> getAllUsers() {
        log.info("Отображения списка всех пользователей");
        return userService.getAllUsers();
    }
}