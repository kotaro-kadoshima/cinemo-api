package com.cinemo.api.config;

import com.google.cloud.vertexai.VertexAI;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    // Ollama設定
    @Value("${spring.ai.ollama.base-url}")
    private String ollamaBaseUrl;
    @Value("${spring.ai.ollama.chat.options.model}")
    private String model;
    @Value("${spring.ai.ollama.chat.options.temperature}")
    private double temperature;

    // Gemini設定
    @Value("${spring.ai.vertex.ai.gemini.project-id}")
    private String geminiProjectId;
    @Value("${spring.ai.vertex.ai.gemini.location}")
    private String geminiLocation;
    @Value("${spring.ai.vertex.ai.gemini.chat.options.model:gemini-2.5-flash}")
    private String geminiModel;
    @Value("${spring.ai.vertex.ai.gemini.chat.options.temperature:0.7}")
    private double geminiTemperature;
    @Value("${spring.ai.vertex.ai.gemini.chat.options.response-mime-type:application/json}")
    private String responseMimeType;


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

    @Bean
    public VertexAI vertexAI() {
        return new VertexAI(geminiProjectId, geminiLocation);
    }

    @Bean
    public VertexAiGeminiChatModel vertexAiGeminiChatModel(VertexAI vertexAI) {
        return VertexAiGeminiChatModel.builder()
                .vertexAI(vertexAI)
                .defaultOptions(
                        VertexAiGeminiChatOptions.builder()
                                .model(geminiModel)
                                .temperature(geminiTemperature)
                                .responseMimeType(responseMimeType)
                                .build())
                .build();
    }

    @Bean
    public VertexAiGeminiChatModel vertexAiGemini25FlashLiteChatModel() {
        VertexAI vertexAI = new VertexAI(geminiProjectId, "us-central1");
        return VertexAiGeminiChatModel.builder()
                .vertexAI(vertexAI)
                .defaultOptions(
                        VertexAiGeminiChatOptions.builder()
                                .model("gemini-2.5-flash-lite")
                                .temperature(geminiTemperature)
                                .responseMimeType(responseMimeType)
                                .build())
                .build();
    }

}