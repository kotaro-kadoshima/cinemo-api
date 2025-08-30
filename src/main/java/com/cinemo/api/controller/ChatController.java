package com.cinemo.api.controller;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final OllamaChatModel ollama;

    public ChatController(OllamaChatModel ollama) {
        this.ollama = ollama;
    }

    @GetMapping("/ollama")
    public String chatOllama(@RequestParam String message) {
        return ollama.call(message);
    }
}
