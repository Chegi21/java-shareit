package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
public class ItemController {
    private static final String OWNER = "X-Sharer-User-Id";
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public Collection<ItemDto> getItemsByOwner(@RequestHeader(OWNER) Long ownerId) {
        return itemService.getItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getItemsBySearchQuery(@RequestParam String text) {
        return itemService.getItemsBySearchQuery(text);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto itemDto, @RequestHeader(OWNER) Long ownerId) {
        return itemService.create(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable Long itemId, @RequestHeader(OWNER) Long ownerId) {
        return itemService.update(itemDto, itemId, ownerId);
    }

    @DeleteMapping("/{itemId}")
    public ItemDto delete(@PathVariable Long itemId, @RequestHeader(OWNER) Long ownerId) {
        return itemService.delete(itemId, ownerId);
    }
}
