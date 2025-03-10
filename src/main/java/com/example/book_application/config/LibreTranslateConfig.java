package com.example.book_application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class LibreTranslateConfig {
    
    @Value("${libretranslate.api.url:https://libretranslate.de}")
    private String apiUrl;
    
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.rootUri(apiUrl).build();
    }
} 