package booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.ShareItGateway;
import ru.practicum.booking.dto.BookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
class BookingDtoJsonTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 2, 12, 0);

        BookingDto dto = BookingDto.builder()
                .itemId(5L)
                .start(start)
                .end(end)
                .build();

        JsonContent<BookingDto> result = json.write(dto);

        assertThat(result).hasJsonPathNumberValue("$.itemId");
        assertThat(result).hasJsonPathStringValue("$.start");
        assertThat(result).hasJsonPathStringValue("$.end");

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(5);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2025-01-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-01-02T12:00:00");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"itemId\":7,\"start\":\"2025-02-15T09:30:00\",\"end\":\"2025-02-16T11:00:00\"}";

        BookingDto dto = json.parseObject(content);

        assertThat(dto.getItemId()).isEqualTo(7L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2025, 2, 15, 9, 30));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2025, 2, 16, 11, 0));
    }
}

