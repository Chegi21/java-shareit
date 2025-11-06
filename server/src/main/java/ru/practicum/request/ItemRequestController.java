package ru.practicum.request;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ItemRequestFullDto;
import ru.practicum.request.dto.ItemRequestShortDto;
import ru.practicum.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final ItemRequestService service;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.service = itemRequestService;
    }

    @ResponseBody
    @PostMapping
    public ItemRequestShortDto create(@RequestBody ItemRequestShortDto itemRequestShortDto,
                                      @RequestHeader(USER_ID) Long requesterId) {
        return service.create(itemRequestShortDto, requesterId, LocalDateTime.now());
    }

    @GetMapping("/{requestId}")
    public ItemRequestFullDto getItemRequestById(@PathVariable("requestId") Long itemRequestId,
                                                 @RequestHeader(USER_ID) Long userId) {
        return service.getItemRequestById(itemRequestId, userId);
    }

    @GetMapping
    public Collection<ItemRequestFullDto> getOwnItemRequests(@RequestHeader(USER_ID) Long userId) {
        return service.getOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestFullDto> getAllItemRequests(@RequestHeader(USER_ID) Long userId,
                                                              @RequestParam(defaultValue = "0") Integer from,
                                                              @RequestParam(required = false) Integer size) {
        return service.getAllItemRequests(userId, from, size);
    }
}

