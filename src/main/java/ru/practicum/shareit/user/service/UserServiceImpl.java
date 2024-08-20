package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Optional;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userRepository.hasEmail(userDto.getEmail())) {
            throw new AlreadyExistsException("Почта " + userDto.getEmail() + " уже зарегестрирована в системе");
        }
        User user = UserDtoMapper.fromDTO(userDto);
        validate(user);
        User createdUser = userRepository.save(user);
        return UserDtoMapper.toDTO(createdUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        if (userRepository.hasEmail(userId, userDto.getEmail())) {
            throw new AlreadyExistsException("Почта " + userDto.getEmail()
                    + " зарегистрированна у другого пользователя");
        }
        User oldUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + userId + "Не найден в системе"));
        User newUser = UserDtoMapper.fromDTO(userDto);
        newUser.setId(userId);
        User updatedUser = updateFields(oldUser, newUser);
        validate(updatedUser);
        return UserDtoMapper.toDTO(userRepository.save(updatedUser));

    }

    private User updateFields(User oldUser, User newUser) {
        String email = newUser.getEmail();
        if (nonNull(email)) {
            oldUser.setEmail(email);
        }
        String name = newUser.getName();
        if (nonNull(name)) {
            oldUser.setName(name);
        }
        return oldUser;
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден в системе");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public UserDto getUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(UserDtoMapper::toDTO).orElseThrow(
                () -> new NotFoundException("Пользователь с ID " + userId + " не найден."));
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        Collection<User> users = userRepository.findAll();
        return users.stream()
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
