package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto getUserById(Long userId);

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, Long userId);

    UserDto delete(Long userId);
}
