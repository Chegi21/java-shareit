package item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ShareItServer;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.Status;
import ru.practicum.booking.repository.BookingRepository;
import ru.practicum.item.dto.ItemFullDto;
import ru.practicum.item.model.Item;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.item.service.ItemServiceImpl;
import ru.practicum.user.model.User;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ContextConfiguration(classes = ShareItServer.class)
class ItemServiceIntegrationTest {
    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;

    private User booker;

    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner@example.com");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("Бронирующий");
        booker.setEmail("booker@example.com");
        booker = userRepository.save(booker);

        item = new Item();
        item.setName("Дрель");
        item.setDescription("Мощная дрель 1500 Вт");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(null);
        item = itemRepository.save(item);

        Booking pastBooking = new Booking();
        pastBooking.setStartDate(LocalDateTime.now().minusDays(5));
        pastBooking.setEndDate(LocalDateTime.now().minusDays(3));
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        pastBooking.setStatus(Status.APPROVED);
        bookingRepository.save(pastBooking);

        Booking futureBooking = new Booking();
        futureBooking.setStartDate(LocalDateTime.now().plusDays(1));
        futureBooking.setEndDate(LocalDateTime.now().plusDays(2));
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        futureBooking.setStatus(Status.APPROVED);
        bookingRepository.save(futureBooking);
    }

    @Test
    void shouldReturnOwnerItemsWithBookings() {
        Collection<ItemFullDto> result = itemService.getItemsByOwner(owner.getId());

        assertThat(result).hasSize(1);

        ItemFullDto dto = result.iterator().next();
        assertThat(dto.getName()).isEqualTo(item.getName());
        assertThat(dto.getLastBooking()).isNotNull();
        assertThat(dto.getNextBooking()).isNotNull();
        assertThat(dto.getLastBooking().getBookerId()).isEqualTo(booker.getId());
        assertThat(dto.getNextBooking().getBookerId()).isEqualTo(booker.getId());
    }

    @Test
    void shouldThrowIfOwnerNotFound() {
        assertThatThrownBy(() -> itemService.getItemsByOwner(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }
}

