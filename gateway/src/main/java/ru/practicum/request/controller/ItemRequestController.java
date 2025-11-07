package ru.practicum.request.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.ItemRequestClient;
import ru.practicum.request.dto.ItemRequestDto;

@Controller
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @Autowired
    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @ResponseBody
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                         @RequestHeader(USER_ID) Long requesterId) {
        return itemRequestClient.create(itemRequestDto, requesterId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable("requestId") Long itemRequestId,
                                                     @RequestHeader(USER_ID) Long userId) {
        return itemRequestClient.getItemRequestById(userId, itemRequestId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerItemRequests(@RequestHeader(USER_ID) Long userId) {
        return itemRequestClient.getOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(USER_ID) Long userId,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @RequestParam(required = false) Integer size) {
        return itemRequestClient.getAllItemRequests(userId, from, size);
    }
}

