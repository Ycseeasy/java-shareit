package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
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
    public UserDto createUser(UserDto userDto) {
        User user = UserDtoMapper.fromDTO(userDto);
        validate(user);
        User createdUser = userRepository.create(user);
        return UserDtoMapper.toDTO(createdUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User newUser = UserDtoMapper.fromDTO(userDto);
        Optional<User> result = userRepository.get(userId);
        if (result.isPresent()) {
            User oldUser = result.get();
            newUser.setId(userId);
            User updateDUser = userRepository.update(oldUser, newUser);
            validate(updateDUser);
            return UserDtoMapper.toDTO(updateDUser);
        } else {
            throw new NotFoundException("Пользователь с ID - " + userId + " не найден.");
        }
    }

    @Override
    public void deleteUser(Long userId) {
        Optional<User> deletedUser = userRepository.delete(userId);
        if (deletedUser.isEmpty()) {
            throw new NotFoundException("Пользователь с ID - " + userId + " не найден.");
        }
    }

    @Override
    public UserDto getUser(Long userId) {
        Optional<User> result = userRepository.get(userId);
        if (result.isPresent()) {
            User searchResult = result.get();
            return UserDtoMapper.toDTO(searchResult);
        } else {
            throw new NotFoundException("Пользователь с ID - " + userId + " не найден.");
        }
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userRepository.getAll()
                .stream()
                .map(UserDtoMapper::toDTO)
                .toList();
    }

    private void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            throw new ValidationException("Поле Name не может быть пустым");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Поле Email не может быть пустым");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException(user.getEmail() + " - некорректный формат почты пользователя");
        }
    }
}
