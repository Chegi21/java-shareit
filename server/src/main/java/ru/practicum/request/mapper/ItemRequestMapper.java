package ru.practicum.request.mapper;

import ru.practicum.item.dto.ItemShortDto;
import ru.practicum.item.model.Item;
import ru.practicum.request.dto.ItemRequestFullDto;
import ru.practicum.request.dto.ItemRequestShortDto;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestShortDto itemRequestShortDto, User requester, LocalDateTime created) {
        return new ItemRequest(itemRequestShortDto.getDescription(), requester, created);
    }

    public static ItemRequestShortDto toItemRequestShortDto(ItemRequest itemRequest) {
        return ItemRequestShortDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(itemRequest.getRequester())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequestFullDto toItemRequestFullDto(ItemRequest itemRequest, Collection<Item> items) {
        UserDto userDto = UserMapper.toUserDto(itemRequest.getRequester());

        Collection<ItemShortDto> itemShortDtoCollection = items.stream()
                .map(item -> ItemShortDto.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .build())
                .collect(Collectors.toList());

        return ItemRequestFullDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(userDto)
                .created(itemRequest.getCreated())
                .items(itemShortDtoCollection)
                .build();
    }
}
