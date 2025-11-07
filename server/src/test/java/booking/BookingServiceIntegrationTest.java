package booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ShareItServer;
import ru.practicum.booking.dto.BookingResponseDto;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.Status;
import ru.practicum.booking.service.BookingServiceImpl;
import ru.practicum.item.model.Item;
import ru.practicum.item.repository.ItemRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.booking.repository.BookingRepository;
import ru.practicum.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ContextConfiguration(classes = ShareItServer.class)
class BookingServiceIntegrationTest {
    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Иван");
        user.setEmail("ivan@example.com");
        user = userRepository.save(user);

        item = new Item();
        item.setName("Перфоратор");
        item.setDescription("Мощный инструмент");
        item.setAvailable(true);
        item.setOwner(user);
        item = itemRepository.save(item);
    }

    @Test
    void shouldReturnAllBookings_whenStateAll() {
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStartDate(LocalDateTime.now().minusDays(2));
        booking.setEndDate(LocalDateTime.now().plusDays(1));
        booking.setStatus(Status.APPROVED);
        bookingRepository.save(booking);

        Collection<BookingResponseDto> result = bookingService.findAllBookingsUser("ALL", user.getId());

        assertThat(result).hasSize(1);
        BookingResponseDto dto = result.iterator().next();
        assertThat(dto.getBooker().getId()).isEqualTo(user.getId());
        assertThat(dto.getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    void shouldReturnWaitingBookings_whenStateWaiting() {
        Booking waitingBooking = new Booking();
        waitingBooking.setBooker(user);
        waitingBooking.setItem(item);
        waitingBooking.setStartDate(LocalDateTime.now().plusDays(1));
        waitingBooking.setEndDate(LocalDateTime.now().plusDays(2));
        waitingBooking.setStatus(Status.WAITING);
        bookingRepository.save(waitingBooking);

        Collection<BookingResponseDto> result = bookingService.findAllBookingsUser("WAITING", user.getId());

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getStatus()).isEqualTo(Status.WAITING);
    }

    @Test
    void shouldThrowException_whenUserNotFound() {
        assertThatThrownBy(() -> bookingService.findAllBookingsUser("ALL", 999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователя не существует");
    }
}

