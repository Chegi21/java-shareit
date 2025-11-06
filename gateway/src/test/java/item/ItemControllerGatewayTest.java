package item;

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
import ru.practicum.item.ItemClient;
import ru.practicum.item.ItemController;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
@ContextConfiguration(classes = ShareItGateway.class)
public class ItemControllerGatewayTest {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCallGetItemsByOwner() throws Exception {
        when(itemClient.getItemsByOwner(anyLong(), anyInt(), any()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items")
                        .header(HEADER_USER_ID, 1L)
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk());

        verify(itemClient).getItemsByOwner(1L, 0, 5);
    }

    @Test
    void shouldReturnBadRequestWhenFromNegative() throws Exception {
        mockMvc.perform(get("/items")
                        .header(HEADER_USER_ID, 1L)
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldCallGetItemsBySearchQuery() throws Exception {
        when(itemClient.getItemsBySearchQuery(anyString(), anyInt(), any()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/search")
                        .param("text", "hammer")
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk());

        verify(itemClient).getItemsBySearchQuery("hammer", 0, 5);
    }

    @Test
    void shouldReturnBadRequestOnSearchWhenFromNegative() throws Exception {
        mockMvc.perform(get("/items/search")
                        .param("text", "tool")
                        .param("from", "-2")
                        .param("size", "10"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldCallGetItemById() throws Exception {
        when(itemClient.getItemById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/42")
                        .header(HEADER_USER_ID, 10L))
                .andExpect(status().isOk());

        verify(itemClient).getItemById(10L, 42L);
    }

    @Test
    void shouldCreateItem() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .build();

        when(itemClient.create(anyLong(), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items")
                        .header(HEADER_USER_ID, 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());

        verify(itemClient).create(eq(5L), any(ItemDto.class));
    }

    @Test
    void shouldReturnBadRequestWhenInvalidItem() throws Exception {
        ItemDto invalid = ItemDto.builder()
                .name("") // невалидное поле
                .description("desc")
                .available(null)
                .build();

        mockMvc.perform(post("/items")
                        .header(HEADER_USER_ID, 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateItem() throws Exception {
        ItemDto dto = ItemDto.builder()
                .name("Updated")
                .description("Updated description")
                .available(true)
                .build();

        when(itemClient.update(any(ItemDto.class), anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/items/15")
                        .header(HEADER_USER_ID, 3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(itemClient).update(any(ItemDto.class), eq(15L), eq(3L));
    }

    @Test
    void shouldCallDeleteItem() throws Exception {
        when(itemClient.delete(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/items/33")
                        .header(HEADER_USER_ID, 2L))
                .andExpect(status().isOk());

        verify(itemClient).delete(33L, 2L);
    }

    @Test
    void shouldCreateComment() throws Exception {
        CommentDto comment = CommentDto.builder()
                .text("Nice tool!")
                .created(LocalDateTime.now())
                .build();

        when(itemClient.createComment(any(CommentDto.class), anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items/1/comment")
                        .header(HEADER_USER_ID, 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk());

        verify(itemClient).createComment(any(CommentDto.class), eq(1L), eq(99L));
    }

    @Test
    void shouldReturnBadRequestWhenCommentInvalid() throws Exception {
        CommentDto comment = CommentDto.builder()
                .text("") // невалидно
                .build();

        mockMvc.perform(post("/items/1/comment")
                        .header(HEADER_USER_ID, 5L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isBadRequest());
    }
}

