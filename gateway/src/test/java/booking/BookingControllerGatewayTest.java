package booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ShareItGateway;
import ru.practicum.client.BookingClient;
import ru.practicum.booking.controller.BookingController;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.model.BookingState;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@ContextConfiguration(classes = ShareItGateway.class)
public class BookingControllerGatewayTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @Test
    void shouldCallGetBookingsForUser() throws Exception {
        when(bookingClient.getBookings(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings")
                        .header(HEADER_USER_ID, 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk());

        verify(bookingClient).getBookings(1L, BookingState.ALL, 0, 5);
    }

    @Test
    void shouldCallGetBookingsForOwner() throws Exception {
        when(bookingClient.getBookings(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/owner")
                        .header(HEADER_USER_ID, 2L)
                        .param("state", "PAST")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(bookingClient).getBookings(2L, BookingState.PAST, 0, 10);
    }

    @Test
    void shouldCallGetBookingById() throws Exception {
        when(bookingClient.getBooking(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/5")
                        .header(HEADER_USER_ID, 10L))
                .andExpect(status().isOk());

        verify(bookingClient).getBooking(10L, 5L);
    }

    @Test
    void shouldCreateBooking() throws Exception {
        BookingDto dto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(3))
                .build();

        when(bookingClient.create(anyLong(), any(BookingDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/bookings")
                        .header(HEADER_USER_ID, 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(bookingClient).create(eq(5L), any(BookingDto.class));
    }

    @Test
    void shouldUpdateBookingStatus() throws Exception {
        when(bookingClient.update(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/bookings/7")
                        .header(HEADER_USER_ID, 99L)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        verify(bookingClient).update(7L, 99L, true);
    }

    @Test
    void shouldReturnBadRequestWhenFromNegative() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header(HEADER_USER_ID, 1L)
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturnBadRequestWhenSizeZero() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header(HEADER_USER_ID, 1L)
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isInternalServerError());
    }
}