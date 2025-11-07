package ru.practicum.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.request.dto.ItemRequestDto;

public class ItemRequestClient extends BaseClient {
    public ItemRequestClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> create(ItemRequestDto requestDto, Long requesterId) {
        return post("", requesterId, requestDto);
    }

    public ResponseEntity<Object> getItemRequestById(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> getOwnItemRequests(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAllItemRequests(Long userId, Integer from, Integer size) {
        String path = "/all" + "?from=" + from;
        if (size != null) {
            path += "&size=" + size;
        }
        return get(path, userId, null);
    }
}

