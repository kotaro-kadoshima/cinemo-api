package com.cinemo.api.agent;

import com.cinemo.api.util.AgentUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovieRecommendAgent {
    private final List<ChatClient> agents;

    //    public MovieRecommendAgent(@Qualifier("ollamaChatModel") ChatModel chatModel) {
    public MovieRecommendAgent(List<ChatModel> chatModels) {
        String systemPrompt = """
                あなたは映画を選定するプロフェッショナルです。
                """;
        this.agents = AgentUtil.createChatClients(chatModels, systemPrompt);

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

    public MovieRecommendationResponse call(String input, String sessionId) {
        return AgentUtil.callAgentWithFallback(agents, input, sessionId, MovieRecommendationResponse.class);
    }


}
