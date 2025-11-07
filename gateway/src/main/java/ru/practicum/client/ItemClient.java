package ru.practicum.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;

public class ItemClient extends BaseClient {
    public ItemClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> create(Long userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> getItemById(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItemsByOwner(Long userId, Integer from, Integer size) {
        String path = "?from=" + from;
        if (size != null) {
            path += "&size=" + size;
        }
        return get(path, userId);
    }

    public ResponseEntity<Object> update(ItemDto itemDto, Long itemId, Long userId) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> delete(Long itemId, Long userId) {
        return delete("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItemsBySearchQuery(String text, Integer from, Integer size) {
        String path = "/search?text=" + text + "&from=" + from;
        if (size != null) {
            path += "&size=" + size;
        }
        return get(path);
    }

    public ResponseEntity<Object> createComment(CommentDto commentDto, Long itemId, Long userId) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}
