package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

public interface UserService {
    User getUserById(Long userId);

    User create(User user);

    User update(User user, Long userId);

    User delete(Long userId);
}
