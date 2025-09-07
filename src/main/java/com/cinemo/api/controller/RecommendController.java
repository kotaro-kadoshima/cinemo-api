package com.cinemo.api.controller;

import com.cinemo.api.entity.Emotion;
import com.cinemo.api.service.EmotionAnalysisService;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import com.cinemo.api.dto.RecommendRequestDto;
import com.cinemo.api.dto.RecommendResponseDto;
import com.cinemo.api.dto.RecommendItemDto;
import com.cinemo.api.repository.MovieRepository;
import com.cinemo.api.entity.Movie;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"}, allowCredentials = "true")
public class RecommendController {
    private final EmotionAnalysisService emotionAnalysisService;
    private final MovieRepository movieRepository;
    //requestをを受け取る
    //
    @PostMapping(value = "/recommend", consumes = "application/json", produces = "application/json")
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

        log.info(
                "recommend request: mood={}, country={}, genres={}, limit={}",
                mood, country, genres.toString(), Integer.valueOf(limit)
        );

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

        // --- 3. 映画検索（DB） パラメータ整形 ---
        // country を original_language 向けに正規化
        String countryCode = mapCountry(country.toLowerCase());
        boolean filterByCountry = !countryCode.isEmpty() && !"other".equals(countryCode);
        boolean countryOther    = "other".equals(countryCode);

        // 感情/ジャンルの適用フラグ
        boolean filterByEmotion = !emotionIds.isEmpty();
        boolean filterByGenre   = !genres.isEmpty();

        // ジャンル名を小文字に正規化（IDで来るならこの処理は後で置き換え）
        List<String> normalizedGenres = genres.stream()
                .map(String::toLowerCase)
                .toList();

        // バッファ付き件数（AI理由生成の失敗保険）
        int queryLimit = Math.max(limit * 3, 10);

        log.info("search params: countryCode={}, filterByCountry={}, countryOther={}, filterByEmotion={}, filterByGenre={}, limit={}",
                countryCode, filterByCountry, countryOther, filterByEmotion, filterByGenre, queryLimit);

        // --- 3.x Repository を呼び出して映画検索を実行する ---
        // MovieRepository#searchMovies は、以下の条件を受け取って候補を返す実装を想定しています。
        //  - emotionIds:     絞り込み対象の感情ID一覧（空の場合は感情条件なし）
        //  - filterByEmotion: 感情での絞り込みを行うか
        //  - normalizedGenres: 正規化済みのジャンル名一覧（空の場合はジャンル条件なし）
        //  - filterByGenre:   ジャンルでの絞り込みを行うか
        //  - countryCode:     original_language 対象（"ja"/"en"/… または "other"）
        //  - filterByCountry: original_language での絞り込みを行うか
        //  - countryOther:    original_language が上記以外（= "other"）を指す場合のフラグ
        //  - limit:           取得件数（上限）
        List<Movie> candidates = movieRepository.searchMovies(
                emotionIds,
                filterByEmotion,
                normalizedGenres,
                filterByGenre,
                countryCode,
                filterByCountry,
                countryOther,
                queryLimit
        );
        log.info("repository returned: {} candidates", (candidates == null ? 0 : candidates.size()));

        // --- 4. 上位抽出 ---
        // Repository 実装後は、以下のように candidates から上位 limit 件を抽出します。
        // 例) List<MovieRow> picked = topK(candidates, limit);
        // 現時点（Repository 未結線）は空で進める
        List<Movie> picked = topK(candidates, limit);
        log.info("topK: limit={}, picked={}", limit, picked.size());

        // --- 5. 推薦理由生成（AI or 仮置き） ---
        // 仮置き実装：候補ごとに短い定型文を生成（後で Gemini 呼び出しに差し替え）
        List<String> reasons = picked.stream()
                .map(item -> buildReasonMock(mood, item))
                .toList();
        log.info("generated reasons: count={}", reasons.size());

        // --- 6. レスポンス整形 ---
        // 仮の picked(Object) から RecommendItemDto を作成（後で Movie 型に差し替え）
        var items = IntStream.range(0, picked.size())
                .mapToObj(i -> {
                    Movie m = picked.get(i);
                    return new RecommendItemDto(
                            m.getTitle(),                // title
                            m.getPosterUrl(),            // posterUrl
                            reasons.get(i),              // reason
                            m.getMovieId(),              // tmdbId or movie_id
                            m.getDuration(),             // duration (minutes)
                            m.getRating() == null ? null : m.getRating().doubleValue(),  // rating (0.0 - 10.0)
                            List.of(),                   // genres（JOINは後続で追加）
                            List.of(),                   // emotionTags（JOINは後続で追加）
                            m.getOriginalLanguage()      // origin
                    );
                })
                .toList();

        RecommendResponseDto res = new RecommendResponseDto();
        res.setItems(items);
        return res;
    }
    /** candidates から先頭 k 件を返す（null セーフ） */
    private static <T> List<T> topK(List<T> list, int k) {
        if (list == null || list.isEmpty() || k <= 0) return List.of();
        return list.stream().limit(k).toList();
    }

    private String mapCountry(String country) {
        if (country == null || country.isBlank()) return "";
        return switch (country) {
            case "japan" -> "ja";
            case "korea" -> "ko";
            case "india" -> "hi";
            case "other" -> "other"; // 特殊扱い
            default -> country; // 既に "ja" などの言語コードが来た場合を許容
        };
    }
    /** 仮の推薦理由生成。後で Gemini 呼び出しに置き換える */
    private String buildReasonMock(String mood, Object movie) {
        // ここではまだ Movie の型が未確定のため Object を受け、タイトルなどは未使用
        // 実装接続後は movie.getTitle(), movie.getOverview() 等を使って文面を組み立てる
        String trimmed = (mood == null || mood.isBlank()) ? "いまの気分" : mood.replaceAll("\\s+", " ").trim();
        return String.format("%sにそっと寄り添う一本。肩の力を抜いて楽しめます。", trimmed);
    }
}
