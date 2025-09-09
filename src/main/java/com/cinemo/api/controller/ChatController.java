package com.cinemo.api.controller;

import com.cinemo.api.service.EmotionAnalysisService;
import com.cinemo.api.util.AiUtil;
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
    private final EmotionAnalysisService emotionAnalysisService;


    @GetMapping("/ollama")
    public String chatOllama(@RequestParam String message) {
        return ollama.call(message);
    }

    @GetMapping("/gemini")
    public String chatGemini(@RequestParam String message) {
        return gemini.call(message);
    }

    @GetMapping("/emotion")
    public String emotionExtraction(@RequestParam String message) {
        String sessionId = AiUtil.generateUuidBased();
        return emotionAnalysisService.analysisEmotion(message, sessionId).toString();
    }

}
