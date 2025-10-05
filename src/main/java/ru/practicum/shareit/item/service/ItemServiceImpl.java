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
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final UserService userService;

    @Autowired
    public ItemServiceImpl(ItemDao itemDao, UserServiceImp userService) {
        this.itemDao = itemDao;
        this.userService = userService;
    }

    @Override
    public Collection<ItemDto> getItemsByOwner(Long ownerId) {
        log.info("Запрос на список вещей пользователя с id = {}", ownerId);

        userService.getUserById(ownerId);

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

        Item item = itemDao.getItemById(id).orElseThrow(() -> {
            log.warn("Вещь с id = {} не найдена", id);
            return new NotFoundException("Вещь не найдена");
        });

        log.info("Вещь с id = {} успешно найдена", id);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        log.info("Запрос на добавление вещи владельцем с Id = {}", ownerId);

        userService.getUserById(ownerId);

        Item createItem = itemDao.create(ItemMapper.toItem(itemDto, ownerId));

        log.info("Вещь с id = {} владельца с id = {} успешно добавлена", createItem.getId(), ownerId);
        return ItemMapper.toItemDto(createItem);
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long ownerId) {
        log.info("Запрос на обновление вещи с id = {}", itemId);

        User user = UserMapper.toUser(userService.getUserById(ownerId));

        Item oldItem = itemDao.getItemById(itemId).orElseThrow(() -> {
            log.warn("Вещь с id = {} не найдена", itemId);
            return new NotFoundException("Вещь не найдена");
        });

        if (!oldItem.getOwnerId().equals(user.getId())) {
            log.warn("Вещь с id = {} не принадлежит пользователю с id = {}", itemId, ownerId);
            throw new ValidationException("Вещь не принадлежит пользователю");
        }

        if (itemDto.getName() != null) oldItem.setName(itemDto.getName());
        if (itemDto.getDescription() != null) oldItem.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) oldItem.setAvailable(itemDto.getAvailable());

        Item updateItem = itemDao.update(oldItem);

        log.info("Вещь с id = {} успешно обновлена", updateItem.getId());
        return ItemMapper.toItemDto(updateItem);
    }

    @Override
    public ItemDto delete(Long itemId, Long ownerId) {
        log.info("Запрос на удаление вещи с id = {}", itemId);

        User user = UserMapper.toUser(userService.getUserById(ownerId));

        Item item = itemDao.getItemById(itemId).orElseThrow(() -> {
            log.warn("Вещь с id = {} не найдена", itemId);
            return new NotFoundException("Вещь не найдена");
        });

        if (!item.getOwnerId().equals(user.getId())) {
            log.warn("Вещь с id = {} не принадлежит пользователю с id = {}", itemId, ownerId);
            throw new ValidationException("Вещь не принадлежит пользователю");
        }

        Item deleteItem = itemDao.delete(itemId);

        log.info("Вещь с id = {} успешно удалена", deleteItem.getId());
        return ItemMapper.toItemDto(deleteItem);
    }
}
