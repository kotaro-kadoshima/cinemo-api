package com.cinemo.api.service;

import com.cinemo.api.agent.MovieRecommendAgent;
import com.cinemo.api.agent.MovieRecommendationResponse;
import com.cinemo.api.dto.AnalysisMovieDto;
import com.cinemo.api.dto.ScoredMovie;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class MovieRecommendService {

    private final MovieRecommendAgent movieRecommendAgent;

    /**
     * 候補になる映画と質問から最終的な映画を3つ抽出する
     *
     * @param question     ユーザーの質問
     * @param scoredMovies 事前に絞った映画の候補（スコア付き）
     * @return AIが選定した映画のリスト
     */
    public List<AnalysisMovieDto> analysisMovie(String question, List<ScoredMovie> scoredMovies, String sessionId) {
        // プロンプトの生成
        String movieListStr = scoredMovies.stream()
                .map(sm -> "\n{ movieId: " + sm.movie().getMovieId()
                        + ", title: " + sm.movie().getTitle()
                        + ", overview: " + sm.movie().getOverview()
                        + ", emotionScore: " + sm.score() + "}")
                .collect(Collectors.joining());
        String prompt = movieRecommendAgent.createPrompt(question, movieListStr);
        log.info("prompt: {}", prompt);
        // agentをcall
        MovieRecommendationResponse response = movieRecommendAgent.call(prompt, sessionId);
        log.info("movieRecommendAgentResponse: {}", response);
        // 結果から映画を抽出してリストで返却
        List<AnalysisMovieDto> pickMovies = new ArrayList<>();
        for (MovieRecommendationResponse.Recommendation recommendation : response.recommendations()) {
            for (ScoredMovie sm : scoredMovies) {
                if (Objects.equals(sm.movie().getMovieId(), recommendation.movieId())) {
                    pickMovies.add(new AnalysisMovieDto(sm.movie(), recommendation.reason()));
                }
            }
        }
        return pickMovies;
    }
}
