package user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.ShareItGateway;
import ru.practicum.user.dto.UserDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItGateway.class)
class UserDtoJsonTest {
    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testSerialize() throws IOException {
        UserDto dto = UserDto.builder()
                .name("Иванов Петр")
                .email("petr.ivanov@example.com")
                .build();

        JsonContent<UserDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Иванов Петр");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("petr.ivanov@example.com");
    }

    @Test
    void testDeserialize() throws IOException {
        String content = "{\"name\":\"Иванов Петр\",\"email\":\"petr.ivanov@example.com\"}";

        UserDto dto = json.parseObject(content);

        assertThat(dto.getName()).isEqualTo("Иванов Петр");
        assertThat(dto.getEmail()).isEqualTo("petr.ivanov@example.com");
    }
}

