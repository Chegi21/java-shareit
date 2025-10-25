package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto findById(Long userId);

    UserDto create(UserDto user);

    UserDto update(UserDto user, Long userId);

    void delete(Long userId);
}
