package ru.practicum.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.user.dto.UserDto;

public class UserClient extends BaseClient {
    public UserClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> create(UserDto userDto) {
        return post("", userDto);
    }

    public ResponseEntity<Object> getUserById(Long userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> getUsers() {
        return get("");
    }

    public ResponseEntity<Object> update(UserDto userDto, Long userId) {
        return patch("/" + userId, userDto);
    }

    public ResponseEntity<Object> delete(Long userId) {
        return delete("/" + userId);
    }
}
