package user;

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
import ru.practicum.user.UserClient;
import ru.practicum.user.UserController;
import ru.practicum.user.dto.UserDto;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@ContextConfiguration(classes = ShareItGateway.class)
public class UserControllerGatewayTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetAllUsers() throws Exception {
        when(userClient.getUsers()).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userClient).getUsers();
    }

    @Test
    void shouldGetUserById() throws Exception {
        when(userClient.getUserById(anyLong())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/users/15"))
                .andExpect(status().isOk());

        verify(userClient).getUserById(15L);
    }

    @Test
    void shouldCreateUser() throws Exception {
        UserDto dto = UserDto.builder()
                .name("Alex")
                .email("alex@example.com")
                .build();

        when(userClient.create(any(UserDto.class))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(userClient).create(any(UserDto.class));
    }

    @Test
    void shouldReturnBadRequestWhenInvalidEmail() throws Exception {
        UserDto dto = UserDto.builder()
                .name("Bob")
                .email("invalid-email") // невалидный формат
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateUser() throws Exception {
        UserDto dto = UserDto.builder()
                .name("Updated name")
                .email("new@example.com")
                .build();

        when(userClient.update(any(UserDto.class), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/users/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(userClient).update(any(UserDto.class), eq(3L));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        when(userClient.delete(anyLong())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/users/9"))
                .andExpect(status().isOk());

        verify(userClient).delete(9L);
    }
}

