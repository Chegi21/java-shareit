package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
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
    public User getUserById(Long id) {
        log.info("Запрос на получения пользователя с id = {}", id);

        User user = userDao.getUserById(id).orElseThrow(() -> {
            log.warn("Пользователь с id = {} не найден", id);
            return new NotFoundException("Пользователь не найден");
        });

        log.info("Пользователь с id = {} успешно найден", id);
        return user;
    }

    @Override
    public User create(User user) {
        log.info("Запрос на создание пользователя");

        if (userDao.existEmail(user)) {
            log.warn("Некорректный или уже существующий email {}", user.getEmail());
            throw new ValidationException("Некорректный или уже существующий email");
        }

        User createdUser = userDao.create(user);

        log.info("Пользователь с id = {} успешно создан", createdUser.getId());
        return createdUser;
    }

    @Override
    public User update(User user, Long userId) {
        log.info("Запрос на обновление данных пользователя с id = {}", userId);

        if (userDao.existEmail(user)) {
            log.warn("Некорректный или уже существующий email {}", user.getEmail());
            throw new ValidationException("Некорректный или уже существующий email");
        }

        User oldUser = userDao.getUserById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id = {} не найден", userId);
            return new NotFoundException("Пользователь не найден");
        });

        Optional.ofNullable(user.getName()).ifPresent(oldUser::setName);
        Optional.ofNullable(user.getEmail()).ifPresent(oldUser::setEmail);

        User updateUser = userDao.update(oldUser);

        log.info("Пользователь с id = {} успешно обновлен", updateUser.getId());
        return updateUser;
    }

    @Override
    public User delete(Long userId) {
        log.info("Запрос на удаление пользователя с id = {}", userId);

        User user = userDao.getUserById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id = {} не найден", userId);
            return new NotFoundException("Пользователь не найден");
        });

        User deleteUser = userDao.delete(user.getId());

        log.info("Пользователь с id = {} успешно удален", deleteUser.getId());
        return deleteUser;
    }
}
