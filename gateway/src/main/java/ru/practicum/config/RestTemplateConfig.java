package ru.practicum.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class RestTemplateConfig {

    @Value("${shareit-server.url}")
    private String serverUrl;

    private RestTemplate createRestTemplate(RestTemplateBuilder builder, String apiPrefix) {
        return builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + apiPrefix))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();
    }

    @Bean("bookingRestTemplate")
    public RestTemplate bookingRestTemplate(RestTemplateBuilder builder) {
        return createRestTemplate(builder, "/bookings");
    }

    @Bean("itemRestTemplate")
    public RestTemplate itemRestTemplate(RestTemplateBuilder builder) {
        return createRestTemplate(builder, "/items");
    }

    @Bean("requestRestTemplate")
    public RestTemplate requestRestTemplate(RestTemplateBuilder builder) {
        return createRestTemplate(builder, "/requests");
    }

    @Bean("userRestTemplate")
    public RestTemplate userRestTemplate(RestTemplateBuilder builder) {
        return createRestTemplate(builder, "/users");
    }
}

