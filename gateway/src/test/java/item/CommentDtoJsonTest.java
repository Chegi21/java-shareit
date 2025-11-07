package item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.ShareItGateway;
import ru.practicum.item.dto.CommentDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
class CommentDtoJsonTest {
    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testSerialize() throws IOException {
        CommentDto dto = CommentDto.builder()
                .text("Отличный инструмент!")
                .authorName("Иван")
                .created(LocalDateTime.of(2025, 11, 5, 12, 30))
                .build();

        JsonContent<CommentDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Отличный инструмент!");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Иван");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2025-11-05T12:30:00");
    }

    @Test
    void testDeserialize() throws IOException {
        String content = "{\"text\":\"Отличный инструмент\",\"authorName\":\"Иван\",\"created\":\"2025-11-05T14:00:00\"}";

        CommentDto dto = json.parseObject(content);

        assertThat(dto.getText()).isEqualTo("Отличный инструмент");
        assertThat(dto.getAuthorName()).isEqualTo("Иван");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2025, 11, 5, 14, 0));
    }
}

