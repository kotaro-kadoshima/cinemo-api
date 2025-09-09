package com.cinemo.api.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class EmotionAnalysisAgent {
    private final ChatClient agent;

//    public EmotionAnalysisAgent(@Qualifier("ollamaChatModel") ChatModel chatModel) {
    public EmotionAnalysisAgent(@Qualifier("vertexAiGeminiChatModel") ChatModel chatModel) {
            this.agent = ChatClient.builder(chatModel)
                .defaultSystem("""
                        あなたは感情を読み取るプロフェッショナルです。
                        質問から感情を読み取って、感情マスタから抽出してください。
                        """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build())
                .build();
    }

    public String createPrompt(String userQuestion, String emotions) {
        String prompt = """
                # 指示
                1. 「ユーザーの質問」の感情を分析
                2. 「感情一覧」から近い感情を3つ抽出
                
                ## ユーザーの質問
                %s
                
                ## 感情一覧
                %s
                
                """.formatted(userQuestion, emotions);

        return prompt;
    }

    public EmotionAnalysisResponse call(String input, String sessionId) {
        return agent.prompt(input)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId))
                .call()
                .entity(EmotionAnalysisResponse.class);
    }

}
