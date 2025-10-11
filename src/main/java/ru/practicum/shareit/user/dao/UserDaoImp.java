package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class UserDaoImp implements UserDao {
    private final Map<Long, User> userMap = new HashMap<>();

    @Override
    public Collection<User> getAllUsers() {
        return userMap.values();
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return Optional.ofNullable(userMap.get(userId));
    }

    @Override
    public User create(User user) {
        long id = getNextId();
        user.setId(id);
        userMap.put(id, user);
        return user;
    }

    @Override
    public User update(User user) {
        return userMap.put(user.getId(), user);
    }

    @Override
    public User delete(Long userId) {
        return userMap.remove(userId);
    }

    private long getNextId() {
        long currentId = userMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentId;
    }

    public boolean userNotExist(Long userId) {
        return userMap.values().stream()
                .noneMatch(user -> user.getId().equals(userId));
    }

    public boolean existEmail(User user) {
        return userMap.values().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()));
    }
}
