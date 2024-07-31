package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        validate(user);
        return userRepository.create(user);
    }

    @Override
    public User updateUser(Long userId, User mewUser) {
        User oldUser = getUser(userId);
        mewUser.setId(userId);
        User updateDUser = userRepository.update(oldUser, mewUser);
        validate(updateDUser);
        return updateDUser;
    }

    @Override
    public void deleteUser(Long userId) {
        Optional<User> deletedUser = userRepository.delete(userId);
        if (deletedUser.isEmpty()) {
            throw new NotFoundException("Пользователь с ID - " + userId + " не найден.");
        }
    }

    @Override
    public User getUser(Long userId) {
        Optional<User> result = userRepository.get(userId);
        if (result.isPresent()) {
            return result.get();
        } else {
            throw new NotFoundException("Пользователь с ID - " + userId + " не найден.");
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        return userRepository.getAll();
    }

    private void validate(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new ValidationException("Поле Name не может быть пустым");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new ValidationException("Поле Email не может быть пустым");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException(user.getEmail() + " - некорректный формат почты пользователя");
        }
    }
}
