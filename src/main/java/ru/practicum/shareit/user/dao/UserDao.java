package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserDao {
    User getUserById(Long id);

    Collection<User> getAllUsers();

    User create(User user);

    User update(User user);

    User delete(Long id);

    boolean existEmail(UserDto userDto);
}
