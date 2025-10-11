package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final UserDao userDao;

    @Autowired
    public ItemServiceImpl(ItemDao itemDao, UserDao userDao) {
        this.itemDao = itemDao;
        this.userDao = userDao;
    }

    @Override
    public Collection<Item> getItemsByOwner(Long ownerId) {
        log.info("Запрос на список вещей пользователя с id = {}", ownerId);

        if (userDao.userNotExist(ownerId)) {
            log.warn("Пользователь с id = {} не найден", ownerId);
            throw  new NotFoundException("Пользователь не найден");
        }

        Collection<Item> items = itemDao.getItemsByOwner(ownerId);

        log.info("Найден список в количестве {} штук", items.size());
        return items;
    }

    @Override
    public Collection<Item> getItemsBySearchQuery(String text) {
        log.info("Запрос на поиск вещи с текстом = {}", text);

        Collection<Item> items = new ArrayList<>();
        if (!text.isBlank()) {
            items = itemDao.getItemsBySearchQuery(text.toLowerCase());
        }

        log.info("Найден список в количестве {} штук", items.size());
        return items;
    }

    @Override
    public Item getItemById(Long id) {
        log.info("Запрос на получение вещи с ID={}", id);

        Item item = itemDao.getItemById(id).orElseThrow(() -> {
            log.warn("Вещь с id = {} не найдена", id);
            return new NotFoundException("Вещь не найдена");
        });

        log.info("Вещь с id = {} успешно найдена", id);
        return item;
    }

    @Override
    public Item create(Item item) {
        log.info("Запрос на добавление вещи владельцем с Id = {}", item.getOwnerId());

        if (userDao.userNotExist(item.getOwnerId())) {
            log.warn("Пользователь с id = {} не найден", item.getOwnerId());
            throw  new NotFoundException("Пользователь не найден");
        }

        Item createItem = itemDao.create(item);

        log.info("Вещь с id = {} владельца с id = {} успешно добавлена", createItem.getId(), createItem.getOwnerId());
        return createItem;
    }

    @Override
    public Item update(Item item) {
        log.info("Запрос на обновление вещи с id = {}", item.getId());

        User user = userDao.getUserById(item.getOwnerId()).orElseThrow(() -> {
            log.warn("Пользователь с id = {} не найден", item.getOwnerId());
            return new NotFoundException("Пользователь не найден");
        });

        Item oldItem = itemDao.getItemById(item.getId()).orElseThrow(() -> {
            log.warn("Вещь с id = {} не найдена", item.getId());
            return new NotFoundException("Вещь не найдена");
        });

        if (!oldItem.getOwnerId().equals(user.getId())) {
            log.warn("Вещь с id = {} не принадлежит пользователю с id = {}", item.getId(), item.getOwnerId());
            throw new ValidationException("Вещь не принадлежит пользователю");
        }

        Optional.ofNullable(item.getName()).ifPresent(oldItem::setName);
        Optional.ofNullable(item.getDescription()).ifPresent(oldItem::setDescription);
        Optional.ofNullable(item.getAvailable()).ifPresent(oldItem::setAvailable);

        Item updateItem = itemDao.update(oldItem);

        log.info("Вещь с id = {} успешно обновлена", updateItem.getId());
        return updateItem;
    }

    @Override
    public Item delete(Long itemId, Long ownerId) {
        log.info("Запрос на удаление вещи с id = {}", itemId);

        User user = userDao.getUserById(ownerId).orElseThrow(() -> {
            log.warn("Пользователь с id = {} не найден", ownerId);
            return new NotFoundException("Пользователь не найден");
        });

        Item oldItem = itemDao.getItemById(itemId).orElseThrow(() -> {
            log.warn("Вещь с id = {} не найдена", itemId);
            return new NotFoundException("Вещь не найдена");
        });

        if (!oldItem.getOwnerId().equals(user.getId())) {
            log.warn("Вещь с id = {} не принадлежит пользователю с id = {}", itemId, ownerId);
            throw new ValidationException("Вещь не принадлежит пользователю");
        }

        Item deleteItem = itemDao.delete(itemId);

        log.info("Вещь с id = {} успешно удалена", deleteItem.getId());
        return deleteItem;
    }
}
