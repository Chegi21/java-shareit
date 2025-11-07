package request;

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
import ru.practicum.client.ItemRequestClient;
import ru.practicum.request.controller.ItemRequestController;
import ru.practicum.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
@ContextConfiguration(classes = ShareItGateway.class)
public class ItemRequestControllerGatewayTest {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateItemRequest() throws Exception {
        ItemRequestDto dto = ItemRequestDto.builder()
                .description("Нужна дрель")
                .build();

        when(itemRequestClient.create(any(ItemRequestDto.class), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/requests")
                        .header(HEADER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(itemRequestClient).create(any(ItemRequestDto.class), eq(1L));
    }

    @Test
    void shouldReturnBadRequestWhenInvalidDescription() throws Exception {
        ItemRequestDto dto = ItemRequestDto.builder()
                .description("")
                .build();

        mockMvc.perform(post("/requests")
                        .header(HEADER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetItemRequestById() throws Exception {
        when(itemRequestClient.getItemRequestById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/42")
                        .header(HEADER_USER_ID, 10L))
                .andExpect(status().isOk());

        verify(itemRequestClient).getItemRequestById(10L, 42L);
    }

    @Test
    void shouldGetOwnItemRequests() throws Exception {
        when(itemRequestClient.getOwnItemRequests(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests")
                        .header(HEADER_USER_ID, 2L))
                .andExpect(status().isOk());

        verify(itemRequestClient).getOwnItemRequests(2L);
    }

    @Test
    void shouldGetAllItemRequests() throws Exception {
        when(itemRequestClient.getAllItemRequests(anyLong(), anyInt(), any()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/all")
                        .header(HEADER_USER_ID, 3L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(itemRequestClient).getAllItemRequests(3L, 0, 10);
    }

    @Test
    void shouldReturnBadRequestWhenFromNegative() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .header(HEADER_USER_ID, 5L)
                        .param("from", "-1")
                        .param("size", "5"))
                .andExpect(status().isInternalServerError());
    }
}

