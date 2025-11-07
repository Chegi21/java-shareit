package ru.practicum.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.model.BookingState;

import java.util.Map;

public class BookingClient extends BaseClient {
    public BookingClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> getBookings(long userId, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> create(long userId, BookingDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> update(Long bookingId, Long userId, Boolean approved) {
        String path = "/" + bookingId + "?approved=" + approved;
        return patch(path, userId, null, null);
    }

}
