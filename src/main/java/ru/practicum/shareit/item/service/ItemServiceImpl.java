package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public Collection<ItemDto> getItemsByOwner(Long ownerId) {
        log.info("Запрос на список вещей пользователя с id = {}", ownerId);

        Optional<User> user = userDao.getUserById(ownerId);
        if (user.isEmpty()) {
            log.warn("Пользователь с id = {} не найден", ownerId);
            throw new NotFoundException("Пользователь не найден");
        }

        Collection<Item> items = itemDao.getItemsByOwner(ownerId);

        log.info("Найден список в количестве {} штук", items.size());
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toSet());
    }

    @Override
    public Collection<ItemDto> getItemsBySearchQuery(String text) {
        log.info("Запрос на поиск вещи с текстом = {}", text);

        Collection<Item> items = new ArrayList<>();
        if (!text.isBlank()) {
            items = itemDao.getItemsBySearchQuery(text.toLowerCase());
        }

        log.info("Найден список в количестве {} штук", items.size());
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toSet());
    }

    @Override
    public ItemDto getItemById(Long id) {
        log.info("Запрос на получение вещи с ID={}", id);

        Optional<Item> item = itemDao.getItemById(id);
        if (item.isEmpty()) {
            log.warn("Вещь с id = {} не найдена", id);
            throw new NotFoundException("Вещь не найдена");
        }

        log.info("Вещь с id = {} успешно найдена", id);
        return ItemMapper.toItemDto(item.get());
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        log.info("Запрос на добавление вещи владельцем с Id = {}", ownerId);

        Optional<User> user = userDao.getUserById(ownerId);
        if (user.isEmpty()) {
            log.warn("Пользователь с id = {} не найден", ownerId);
            throw new NotFoundException("Пользователь не найден");
        }

        Item createItem = itemDao.create(ItemMapper.toItem(itemDto, ownerId));

        log.info("Вещь с id = {} владельца с id = {} успешно добавлена", createItem.getId(), ownerId);
        return ItemMapper.toItemDto(createItem);
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long ownerId) {
        log.info("Запрос на обновление вещи с id = {}", itemId);

        Optional<User> user = userDao.getUserById(ownerId);
        if (user.isEmpty()) {
            log.warn("Пользователь с id = {} не найден", ownerId);
            throw new NotFoundException("Пользователь не найден");
        }

        Optional<Item> oldItem = itemDao.getItemById(itemId);
        if (oldItem.isEmpty()) {
            log.warn("Вещь с id = {} не найдена", itemId);
            throw new NotFoundException("Вещь не найдена");
        }

        if (!oldItem.get().getOwnerId().equals(user.get().getId())) {
            log.warn("Вещь с id = {} не принадлежит пользователю с id = {}", itemId, ownerId);
            throw new ValidationException("Вещь не принадлежит пользователю");
        }

        Optional.ofNullable(itemDto.getName()).ifPresent(name -> oldItem.get().setName(name));
        Optional.ofNullable(itemDto.getDescription()).ifPresent(description -> oldItem.get().setDescription(description));
        Optional.ofNullable(itemDto.getAvailable()).ifPresent(available -> oldItem.get().setAvailable(available));

        Item updateItem = itemDao.update(oldItem.get());

        log.info("Вещь с id = {} успешно обновлена", updateItem.getId());
        return ItemMapper.toItemDto(updateItem);
    }

    @Override
    public ItemDto delete(Long itemId, Long ownerId) {
        log.info("Запрос на удаление вещи с id = {}", itemId);

        Optional<User> user = userDao.getUserById(ownerId);
        if (user.isEmpty()) {
            log.warn("Пользователь с id = {} не найден", ownerId);
            throw new NotFoundException("Пользователь не найден");
        }

        Optional<Item> oldItem = itemDao.getItemById(itemId);
        if (oldItem.isEmpty()) {
            log.warn("Вещь с id = {} не найдена", itemId);
            throw new NotFoundException("Вещь не найдена");
        }

        if (!oldItem.get().getOwnerId().equals(user.get().getId())) {
            log.warn("Вещь с id = {} не принадлежит пользователю с id = {}", itemId, ownerId);
            throw new ValidationException("Вещь не принадлежит пользователю");
        }

        Item deleteItem = itemDao.delete(itemId);

        log.info("Вещь с id = {} успешно удалена", deleteItem.getId());
        return ItemMapper.toItemDto(deleteItem);
    }
}
