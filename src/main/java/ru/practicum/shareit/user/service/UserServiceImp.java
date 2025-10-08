package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.Optional;

@Slf4j
@Service
public class UserServiceImp implements UserService {
    private final UserDao userDao;

    @Autowired
    public UserServiceImp(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("Запрос на получения пользователя с id = {}", id);

        Optional<User> user = userDao.getUserById(id);
        if (user.isEmpty()) {
            log.warn("Пользователь с id = {} не найден", id);
            throw new NotFoundException("Пользователь не найден");
        }

        log.info("Пользователь с id = {} успешно найден", id);
        return UserMapper.toUserDto(user.get());
    }

    @Override
    public UserDto create(UserDto userDto) {
        log.info("Запрос на создание пользователя");

        if (userDao.existEmail(userDto)) {
            log.warn("Некорректный или уже существующий email {}", userDto.getEmail());
            throw new ValidationException("Некорректный или уже существующий email");
        }

        User createdUser = userDao.create(UserMapper.toUser(userDto));

        log.info("Пользователь с id = {} успешно создан", createdUser.getId());
        return UserMapper.toUserDto(createdUser);
    }

    @Override
    public UserDto update(UserDto userDto, Long userId) {
        log.info("Запрос на обновление данных пользователя с id = {}", userId);

        if (userDao.existEmail(userDto)) {
            log.warn("Некорректный или уже существующий email {}", userDto.getEmail());
            throw new ValidationException("Некорректный или уже существующий email");
        }

        Optional<User> oldUser = userDao.getUserById(userId);
        if (oldUser.isEmpty()) {
            log.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        }

        Optional.ofNullable(userDto.getName()).ifPresent(name -> oldUser.get().setName(name));
        Optional.ofNullable(userDto.getEmail()).ifPresent(email -> oldUser.get().setEmail(email));

        User updateUser = userDao.update(oldUser.get());

        log.info("Пользователь с id = {} успешно обновлен", updateUser.getId());
        return UserMapper.toUserDto(updateUser);
    }

    @Override
    public UserDto delete(Long userId) {
        log.info("Запрос на удаление пользователя с id = {}", userId);

        Optional<User> user = userDao.getUserById(userId);
        if (user.isEmpty()) {
            log.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        }

        User deleteUser = userDao.delete(user.get().getId());

        log.info("Пользователь с id = {} успешно удален", deleteUser.getId());
        return UserMapper.toUserDto(deleteUser);
    }
}
