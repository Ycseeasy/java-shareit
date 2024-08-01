package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {

    User create(User user);

    User update(User oldUser, User newUser);

    Optional<User> get(Long userId);

    Optional<User> delete(long userId);

    Collection<User> getAll();
}
