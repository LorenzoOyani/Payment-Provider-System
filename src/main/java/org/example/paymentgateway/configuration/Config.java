package org.example.paymentgateway.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Config {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final RestTemplate restTemplate = new RestTemplate();

    @Bean
    public ObjectMapper objectMapper() {
        return OBJECT_MAPPER;
    }

    @Bean
    public RestTemplate restTemplate() {
        return restTemplate;
    }
}
