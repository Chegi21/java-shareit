package ru.practicum.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.practicum.client.BookingClient;
import ru.practicum.client.ItemClient;
import ru.practicum.client.ItemRequestClient;
import ru.practicum.client.UserClient;

@Configuration
public class ClientConfig {
    @Bean
    public BookingClient bookingClient(@Qualifier("bookingRestTemplate") RestTemplate bookingRestTemplate) {
        return new BookingClient(bookingRestTemplate);
    }

    @Bean
    public ItemClient itemClient(@Qualifier("itemRestTemplate") RestTemplate itemRestTemplate) {
        return new ItemClient(itemRestTemplate);
    }

    @Bean
    public ItemRequestClient itemRequestClient(@Qualifier("requestRestTemplate") RestTemplate requestRestTemplate) {
        return new ItemRequestClient(requestRestTemplate);
    }

    @Bean
    public UserClient userClient(@Qualifier("userRestTemplate") RestTemplate userRestTemplate) {
        return new UserClient(userRestTemplate);
    }
}
