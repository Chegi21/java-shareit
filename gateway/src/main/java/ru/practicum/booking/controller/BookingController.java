package ru.practicum.booking.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.model.BookingState;
import ru.practicum.client.BookingClient;

@RestController
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @Autowired
    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsUser(@RequestHeader(USER_ID) long userId,
                                                  @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsOwner(@RequestHeader(USER_ID) long userId,
                                                   @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID) long userId,
                                             @PathVariable Long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID) Long userId,
                                         @Valid @RequestBody BookingDto requestDto) {
        return bookingClient.create(userId, requestDto);
    }

    @ResponseBody
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(@PathVariable Long bookingId,
                                         @RequestHeader(USER_ID) Long userId,
                                         @RequestParam Boolean approved) {
        return bookingClient.update(bookingId, userId, approved);
    }
}
