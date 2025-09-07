package com.cinemo.api.service;

import com.cinemo.api.agent.MovieRecommendAgent;
import com.cinemo.api.agent.MovieRecommendationResponse;
import com.cinemo.api.dto.AnalysisMovieDto;
import com.cinemo.api.entity.Movie;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
@Service
public class MovieRecommendService {

    private final MovieRecommendAgent movieRecommendAgent;

    /**
     * 候補になる映画と質問から最終的な映画を3つ抽出する
     *
     * @param question  　ユーザーの質問
     * @param movieList 　事前に絞った映画の候補
     * @return AIが選定した映画のリスト
     */
    public List<AnalysisMovieDto> analysisMovie(String question, List<Movie> movieList) {
        // プロンプトの生成
        String prompt = movieRecommendAgent.createPrompt(question, movieList.toString());
        // agentをcall
        MovieRecommendationResponse response = movieRecommendAgent.call(prompt);
        // 結果から映画を抽出してリストで返却
        List<AnalysisMovieDto> pickMovies = new ArrayList<>();
        for (MovieRecommendationResponse.Recommendation recommendation : response.recommendations()) {
            for (Movie movie : movieList) {
                if (Objects.equals(movie.getMovieId(), recommendation.movieId())) {
                    pickMovies.add(new AnalysisMovieDto(movie, recommendation.reason()));
                }
            }
        }
        return pickMovies;
    }
}
