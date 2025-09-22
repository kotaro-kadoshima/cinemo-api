package com.cinemo.api.agent;

import com.cinemo.api.util.AgentUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@DependsOn({"vertexAiGeminiChatModelFromLocations"}) // Bean名を指定
public class EmotionAnalysisAgent {
    private final List<ChatClient> agents;

    public EmotionAnalysisAgent(@Qualifier("vertexAiGeminiChatModelFromLocations") List<ChatModel> chatModels) {
        String systemPrompt = """
                あなたは感情を読み取るプロフェッショナルです。
                質問から感情を読み取って、感情マスタから抽出してください。
                """;
        this.agents = AgentUtil.createChatClients(chatModels, systemPrompt);
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
        return AgentUtil.callAgentWithFallback(agents, input, sessionId, EmotionAnalysisResponse.class);
    }

}
