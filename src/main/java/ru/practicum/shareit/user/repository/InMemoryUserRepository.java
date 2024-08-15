package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class InMemoryUserRepository implements UserRepository {

    private final HashMap<Long, User> userData = new HashMap<>();
    protected final Set<String> emails = new HashSet<>();
    private long counter = 1;

    @Override
    public User create(User user) {
        if (emails.contains(user.getEmail())) {
            throw new AlreadyExistsException("Пользователь с почтой " + user.getEmail()
                    + " уже зарегестрирован в системе");
        }
        user.setId(counter++);
        userData.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User update(User oldUser, User newUser) {
        if (emails.contains(newUser.getEmail())) {
            throw new AlreadyExistsException("Пользователь с почтой " + newUser.getEmail()
                    + " уже зарегестрирован в системе");
        }
        if (newUser.getName() == null || newUser.getName().isEmpty()) {
            newUser.setName(oldUser.getName());
        }
        if (newUser.getEmail() == null || newUser.getEmail().isEmpty()) {
            newUser.setEmail(oldUser.getEmail());
        }
        userData.replace(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public Optional<User> get(Long userId) {
        return Optional.ofNullable(userData.get(userId));
    }

    @Override
    public Optional<User> delete(long userId) {
        Optional<User> deletedUser = Optional.ofNullable(userData.remove(userId));
        deletedUser.ifPresent(user -> emails.remove(user.getEmail()));
        return deletedUser;
    }

    @Override
    public Collection<User> getAll() {
        return new ArrayList<>(userData.values());
    }
}
