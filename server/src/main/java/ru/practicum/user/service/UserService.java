package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

public interface UserService {
    UserDto findById(Long userId);

    UserDto create(User user);

    UserDto update(User user);

    void delete(Long userId);
}
