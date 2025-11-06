package ru.practicum.user.mapper;

import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

public class UserMapper {
    public static User toUser(UserDto user) {
        return new User(user.getName(), user.getEmail());
    }

    public static User toUser(UserDto user, Long userId) {
        return new User(userId, user.getName(), user.getEmail());
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
