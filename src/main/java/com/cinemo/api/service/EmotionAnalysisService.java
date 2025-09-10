package com.cinemo.api.service;

import com.cinemo.api.agent.EmotionAnalysisAgent;
import com.cinemo.api.agent.EmotionAnalysisResponse;
import com.cinemo.api.entity.Emotion;
import com.cinemo.api.repository.EmotionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class EmotionAnalysisService {
    private final EmotionAnalysisAgent emotionAnalysisAgent;
    private final EmotionRepository emotionRepository;

    /**
     * 質問から感情を分析して、emotionsテーブルのentityを返却
     *
     * @param message 　ユーザー質問
     * @return List<Emotion> 抽出した感情
     */
    public List<Emotion> analysisEmotion(String message) {
        List<Emotion> list = emotionRepository.findAll();
        String prompt = emotionAnalysisAgent.createPrompt(message, list.toString());
        log.info("prompt: {}", prompt);
        EmotionAnalysisResponse emotionAnalysisResponse = emotionAnalysisAgent.call(prompt);
        log.info("emotionAnalysis: {}", emotionAnalysisResponse);
        List<Emotion> emotions = getEmotionsInOrder(emotionAnalysisResponse);
        log.info("emotions: {}", emotions);
        return emotions;
    }

    /**
     * AIの分析結果から感情名のリストを返却
     *
     * @param response AIの分析結果
     * @return 抽出した感情名のリスト
     */
    private List<String> getEmotionNames(EmotionAnalysisResponse response) {
        return response.detectedEmotions().stream()
                .map(EmotionAnalysisResponse.DetectedEmotion::emotion)
                .collect(Collectors.toList());
    }

    private List<Emotion> getEmotionsInOrder(EmotionAnalysisResponse response) {
        List<String> emotionNames = getEmotionNames(response);
        List<Emotion> foundEmotions = emotionRepository.findByNameIn(emotionNames);

        // 元の順序に合わせてソート
        Map<String, Emotion> emotionMap = foundEmotions.stream()
                .collect(Collectors.toMap(Emotion::getName, emotion -> emotion));

        return emotionNames.stream()
                .map(emotionMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
