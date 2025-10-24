package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class ItemMapper {
    public static Item toItem(ItemShortDto item, Long ownerId) {
        return Item.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(ownerId)
                .requestId(item.getRequestId())
                .build();
    }

    public static ItemShortDto toItemShortDto(Item item) {
        return ItemShortDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwnerId())
                .requestId(item.getRequestId())
                .build();
    }

    public static ItemDto toItemDto(Item item, Collection<CommentResponseDto> comments, BookingResponseDto lastBooking, BookingResponseDto nextBooking) {

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwnerId())
                .requestId(item.getRequestId())
                .comments(new ArrayList<>(comments))
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();
    }
}
