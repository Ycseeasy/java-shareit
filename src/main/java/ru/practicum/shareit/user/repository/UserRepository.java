package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {

    public User create(User user);

    public User update(User oldUser, User newUser);

    public Optional<User> get(Long userId);

    public Optional<User> delete(long userId);

    public Collection<User> getAll();
}
