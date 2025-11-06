package request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.ShareItGateway;
import ru.practicum.request.dto.ItemRequestDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
class ItemRequestDtoJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testSerialize() throws IOException {
        ItemRequestDto dto = ItemRequestDto.builder()
                .description("Нужна дрель")
                .requesterId(10L)
                .created(LocalDateTime.of(2025, 11, 5, 12, 0))
                .build();

        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Нужна дрель");
        assertThat(result).extractingJsonPathNumberValue("$.requesterId").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2025-11-05T12:00:00");
    }

    @Test
    void testDeserialize() throws IOException {
        String content = "{\"description\":\"Мощность 1500 Вт\",\"requesterId\":\"12\",\"created\":\"2025-11-05T15:30:00\"}";

        ItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getDescription()).isEqualTo("Мощность 1500 Вт");
        assertThat(dto.getRequesterId()).isEqualTo(12L);
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2025, 11, 5, 15, 30));
    }
}
