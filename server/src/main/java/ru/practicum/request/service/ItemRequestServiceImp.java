package ru.practicum.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.exception.NotFoundException;
import ru.practicum.item.model.Item;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.request.dto.ItemRequestFullDto;
import ru.practicum.request.dto.ItemRequestShortDto;
import ru.practicum.request.mapper.ItemRequestMapper;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.request.repository.ItemRequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemRequestServiceImp implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public ItemRequestServiceImp(
            ItemRequestRepository requestRepository,
            UserRepository userRepository,
            ItemRepository itemRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemRequestShortDto create(ItemRequestShortDto itemRequestShortDto, Long requesterId, LocalDateTime created) {
        log.info("Запрос на создания запроса от пользователя с id = {}", requesterId);

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> {
                    log.warn("Пользователя с id = {} не существует", requesterId);
                    return new NotFoundException("Пользователя не существует");
                });

        ItemRequest createItemRequest = requestRepository.save(
                ItemRequestMapper.toItemRequest(itemRequestShortDto, requester, created)
        );

        log.info("Запрос с id = {} успешно создан", createItemRequest.getId());
        return ItemRequestMapper.toItemRequestShortDto(createItemRequest);
    }

    @Override
    public ItemRequestFullDto getItemRequestById(Long itemRequestId, Long userId) {
        log.info("Запрос на получение запроса от пользователя с id = {}", userId);

        if (!userRepository.existsById(userId)) {
            log.warn("Пользователя с id = {} не существует", userId);
            throw new NotFoundException("Пользователя не существует");
        }

        ItemRequest itemRequest = requestRepository.findById(itemRequestId)
                .orElseThrow(() -> {
                    log.warn("Запрос с id = {} не существует", itemRequestId);
                    return new NotFoundException("Запрос не существует");
                });

        Collection<Item> items = itemRepository.findAllByRequestId(itemRequest.getId());

        log.info("Запрос с id = {} успешно найден", itemRequest.getId());
        return ItemRequestMapper.toItemRequestFullDto(itemRequest, items);
    }

    @Override
    public Collection<ItemRequestFullDto> getOwnItemRequests(Long requesterId) {
        log.info("Запрос на весь список запросов пользователя с id = {}", requesterId);

        if (!userRepository.existsById(requesterId)) {
            log.warn("Пользователя с id = {} не существует", requesterId);
            throw new NotFoundException("Пользователя не существует");
        }

        Collection<ItemRequest> itemRequests = requestRepository.findAllByRequesterId(requesterId);

        Collection<Long> itemRequestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .toList();

        Collection<Item> items = itemRepository.findAllByRequestIds(itemRequestIds);

        Map<Long, List<Item>> itemsByRequestId = items.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        log.info("Найден список запросов в количестве {}", itemRequests.size());
        return itemRequests.stream()
                .map(itemRequest -> {
                    List<Item> requestItems = itemsByRequestId.getOrDefault(itemRequest.getId(), List.of());
                    return ItemRequestMapper.toItemRequestFullDto(itemRequest, requestItems);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemRequestFullDto> getAllItemRequests(Long userId, Integer from, Integer size) {
        log.info("Запрос на все созданные запросы от пользователя с id = {}", userId);

        if (!userRepository.existsById(userId)) {
            log.warn("Пользователя с id = {} не существует", userId);
            throw new NotFoundException("Пользователя не существует");
        }

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));

        Page<ItemRequest> itemRequestsPage = requestRepository.findAllByRequesterIdNot(userId, pageable);

        Collection<ItemRequest> itemRequests = itemRequestsPage.getContent();

        Collection<Long> itemRequestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .toList();

        Collection<Item> items = itemRepository.findAllByRequestIds(itemRequestIds);

        Map<Long, List<Item>> itemsByRequestId = items.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return itemRequests.stream()
                .map(itemRequest -> {
                    List<Item> requestItems = itemsByRequestId.getOrDefault(itemRequest.getId(), List.of());
                    return ItemRequestMapper.toItemRequestFullDto(itemRequest, requestItems);
                })
                .collect(Collectors.toList());
    }
}

