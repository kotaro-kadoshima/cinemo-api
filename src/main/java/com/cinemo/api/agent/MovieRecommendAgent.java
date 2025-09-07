package com.cinemo.api.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MovieRecommendAgent {
    private final ChatClient agent;

    public MovieRecommendAgent(@Qualifier("ollamaChatModel") ChatModel chatModel) {
//    public MovieRecommendAgent(@Qualifier("vertexAiGeminiChatModel") ChatModel chatModel) {
        this.agent = ChatClient.builder(chatModel)
                .defaultSystem("""
                        あなたは映画を選定するプロフェッショナルです。
                        """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build())
                .build();
    }

    public String createPrompt(String userQuestion, String movieList) {
        String prompt = """
                # 指示
                1. 「ユーザーの質問」からオススメする映画を3つ抽出
                2. 映画の候補は「候補映画一覧」から抽出すること
                
                ## ユーザーの質問
                %s
                
                ## 映画の候補一覧
                %s
                
                """.formatted(userQuestion, movieList);

        return prompt;
    }

    public MovieRecommendationResponse call(String input) {
        return agent.prompt(input).call().entity(MovieRecommendationResponse.class);
    }


}
