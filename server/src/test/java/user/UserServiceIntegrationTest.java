package user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ShareItServer;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.user.service.UserServiceImp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ContextConfiguration(classes = ShareItServer.class)
class UserServiceIntegrationTest {
    @Autowired
    private UserServiceImp userService;

    @Autowired
    private UserRepository userRepository;

    private User savedUser;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("Петр Иванов");
        user.setEmail("petr.ivanov@example.com");
        savedUser = userRepository.save(user);
    }

    @Test
    void shouldReturnUserById() {
        UserDto result = userService.findById(savedUser.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedUser.getId());
        assertThat(result.getName()).isEqualTo(savedUser.getName());
        assertThat(result.getEmail()).isEqualTo(savedUser.getEmail());
    }

    @Test
    void shouldThrowIfUserNotFound() {
        assertThatThrownBy(() -> userService.findById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }
}
