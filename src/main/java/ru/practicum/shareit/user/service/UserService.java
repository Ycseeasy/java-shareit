package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    public User createUser(User user);

    public User updateUser(Long userId, User user);

    public void deleteUser(Long userId);

    public User getUser(Long userId);

    public Collection<User> getAllUsers();
}
