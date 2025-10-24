package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

public interface UserService {
    User findById(Long userId);

    User create(User user);

    User update(User user, Long userId);

    void delete(Long userId);
}
