package com.cinemo.api.util;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;

import java.util.ArrayList;
import java.util.List;

public class AgentUtil {

    /**
     * ChatClientをChatModel分作成して返却
     *
     * @param chatModels
     * @param systemPrompt
     * @return
     */
    public static List<ChatClient> createChatClients(List<ChatModel> chatModels, String systemPrompt) {
        List<ChatClient> agents = new ArrayList<>();
        for (ChatModel chatModel : chatModels) {
            ChatClient chatClient = ChatClient.builder(chatModel)
                    .defaultSystem(systemPrompt)
                    .defaultAdvisors(MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build())
                    .build();
            agents.add(chatClient);
        }
        return agents;
    }


    /**
     * フォールバック機能付きでエージェントを呼び出す汎用メソッド
     *
     * @param agents       ChatClientのリスト（リージョン順）
     * @param input        入力テキスト
     * @param sessionId    セッションID
     * @param responseType レスポンスの型
     * @return 指定された型のレスポンス
     */
    public static <T> T callAgentWithFallback(
            List<ChatClient> agents,
            String input,
            String sessionId,
            Class<T> responseType) {

        RuntimeException lastException = null;

        for (int i = 0; i < agents.size(); i++) {
            try {
                ChatClient agent = agents.get(i);

                T response = callAgent(agent, input, sessionId, responseType);

                System.out.println("Success with region: ");
                return response;

            } catch (Exception e) {
                lastException = new RuntimeException("Failed in region: ", e);
                System.err.println("Failed in region:  - " + e.getMessage());

                // 最後のリージョンでない場合は続行
                if (i < agents.size() - 1) {
                    System.out.println("Falling back to next region...");
                    continue;
                }
            }
        }

        // すべてのリージョンで失敗した場合
        throw new RuntimeException("All regions failed", lastException);
    }

    private static <T> T callAgent(ChatClient agent, String input, String sessionId, Class<T> responseType) {
        return agent.prompt(input)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId))
                .call()
                .entity(responseType);
    }

}
