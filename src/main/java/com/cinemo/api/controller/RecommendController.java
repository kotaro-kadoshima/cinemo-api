package com.cinemo.api.controller;

import com.cinemo.api.entity.Emotion;
import com.cinemo.api.service.EmotionAnalysisService;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import com.cinemo.api.dto.RecommendRequestDto;
import com.cinemo.api.dto.RecommendResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class RecommendController {
    private final EmotionAnalysisService emotionAnalysisService;
    //requestをを受け取る
    //
    @PostMapping("/recommend")
    public RecommendResponseDto recommend(@RequestBody RecommendRequestDto req) {

        // --- 1. リクエスト受領 & バリデーション ---
        // mood（気分）は必須
        String mood = (req.getMood() != null) ? req.getMood().trim() : "";
        if (mood.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "mood is required");
        }

        // country（制作国）はnullなら空文字にしておく
        String country = (req.getCountry() != null) ? req.getCountry().trim() : "";

        // genres（ジャンル）はnullなら空リストに
        List<String> genres = (req.getGenres() != null) ? req.getGenres() : List.of();

        // limit（推薦件数）は指定がなければデフォルト3件
        int limit = (req.getLimit() != null && req.getLimit() > 0) ? req.getLimit() : 3;

        log.info("recommend request: mood={}, country={}, genres={}, limit={}",
                mood, country, genres, limit);

        // --- 次のステップに渡す ---
        // ここから EmotionAnalysisService を呼んで感情抽出 → DB検索に進む

        // --- 2. 感情抽出（AI → DB突合） ---
        List<Emotion> emotions;
        try {
            emotions = emotionAnalysisService.analysisEmotion(mood); // AI呼び出し→名前突合→List<Emotion>
        } catch (Exception e) {
            log.warn("emotion analysis failed. proceed without emotion filter. cause={}", e.getMessage());
            emotions = List.of();
        }

        // 検索用に emotion_id の配列へ変換
        List<Integer> emotionIds = emotions.stream()
                .map(Emotion::getEmotionId)
                .toList();

        // （任意）ログ
        log.info("detected emotions: names={}, ids={}",
                emotions.stream().map(Emotion::getName).toList(),
                emotionIds);

        // ここで emotionIds を次の DB 検索ステップへ渡す
        return new RecommendResponseDto();
    }
}
