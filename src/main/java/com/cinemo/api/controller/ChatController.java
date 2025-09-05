package com.cinemo.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ChatController {

    private final OllamaChatModel ollama;
    private final VertexAiGeminiChatModel gemini;


    @GetMapping("/ollama")
    public String chatOllama(@RequestParam String message) {
        return ollama.call(message);
    }

    @GetMapping("/gemini")
    public String chatGemini(@RequestParam String message) {
        return gemini.call(message);
    }
}
