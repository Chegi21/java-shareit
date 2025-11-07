package ru.practicum.item.mapper;

import ru.practicum.booking.dto.BookingShortDto;
import ru.practicum.item.dto.CommentResponseDto;
import ru.practicum.item.dto.ItemFullDto;
import ru.practicum.item.dto.ItemShortDto;
import ru.practicum.item.model.Item;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.user.model.User;

import java.util.Collection;

public class ItemMapper {
    public static Item toItem(ItemShortDto item, User owner, ItemRequest itemRequest) {
        return new Item(
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                owner,
                itemRequest);
    }

    public static ItemShortDto toItemShortDto(Item item) {
        return ItemShortDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static ItemFullDto toItemDto(
            Item item,
            Collection<CommentResponseDto> comments,
            BookingShortDto lastBooking,
            BookingShortDto nextBooking) {
        return ItemFullDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner().getId())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .comments(comments)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();
    }

}
