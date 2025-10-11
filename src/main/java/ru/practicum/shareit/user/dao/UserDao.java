package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserDao {
    Optional<User> getUserById(Long id);

    Collection<User> getAllUsers();

    User create(User user);

    User update(User user);

    User delete(Long id);

    boolean userNotExist(Long userId);

    boolean existEmail(User user);
}
