package request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ShareItServer;
import ru.practicum.exception.NotFoundException;
import ru.practicum.item.model.Item;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.request.dto.ItemRequestFullDto;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.request.repository.ItemRequestRepository;
import ru.practicum.request.service.ItemRequestServiceImp;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ContextConfiguration(classes = ShareItServer.class)
class ItemRequestServiceIntegrationTest {
    @Autowired
    private ItemRequestServiceImp itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository requestRepository;

    private User user;
    private ItemRequest request;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Иван Иванов");
        user.setEmail("ivan@example.com");
        user = userRepository.save(user);

        request = new ItemRequest();
        request.setDescription("Нужен шуруповерт");
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        request = requestRepository.save(request);

        item = new Item();
        item.setName("Шуруповерт");
        item.setDescription("Аккумуляторный шуруповерт");
        item.setAvailable(true);
        item.setOwner(user);
        item.setRequest(request);
        item = itemRepository.save(item);
    }

    @Test
    @DisplayName("Должен вернуть список запросов пользователя с вещами")
    void shouldReturnOwnItemRequestsWithItems() {
        Collection<ItemRequestFullDto> result = itemRequestService.getOwnItemRequests(user.getId());

        assertThat(result).hasSize(1);

        ItemRequestFullDto dto = result.iterator().next();
        assertThat(dto.getDescription()).isEqualTo(request.getDescription());
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getItems().iterator().next().getName()).isEqualTo(item.getName());
    }

    @Test
    @DisplayName("Должен выбросить NotFoundException, если пользователь не существует")
    void shouldThrowIfRequesterNotFound() {
        assertThatThrownBy(() -> itemRequestService.getOwnItemRequests(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователя не существует");
    }
}

