package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        return UserMapper.toUserDto(userService.getUserById(userId));
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        return UserMapper.toUserDto(userService.create(UserMapper.toUser(userDto)));
    }

    @PatchMapping("/{userId}")
    public UserDto upgrade(@RequestBody UserDto userDto, @PathVariable Long userId) {
        return UserMapper.toUserDto(userService.update(UserMapper.toUser(userDto), userId));
    }

    @DeleteMapping("/{userId}")
    public UserDto userDto(@PathVariable Long userId) {
        return UserMapper.toUserDto(userService.delete(userId));
    }
}
