package com.cinemo.api.config;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Value("${spring.ai.ollama.base-url}")
    private String ollamaBaseUrl;
    @Value("${spring.ai.ollama.chat.options.model}")
    private String model;
    @Value("${spring.ai.ollama.chat.options.temperature}")
    private double temperature;

    @Bean
    public OllamaApi ollamaApi() {
        return new OllamaApi.Builder().baseUrl(ollamaBaseUrl).build();
    }

    @Bean
    public OllamaChatModel ollamaChatModel(OllamaApi ollamaApi) {
        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(OllamaOptions.builder()
                        .model(model)
                        .temperature(temperature)
                        .build())
                .build();
    }
}