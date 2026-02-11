package com.cinemo.api.repository;

import com.cinemo.api.dto.ScoredMovie;
import com.cinemo.api.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query(value = "SELECT movie_id, title, original_title, duration, release_date, " +
            "poster_url, rating, overview, adult, original_language, " +
            "emotion_status, created_at, updated_at FROM public.movies",
            nativeQuery = true)
    List<Movie> findAllMovies();

    List<Movie> findByTitleContaining(String title);
    List<Movie> findByOriginalLanguage(String language);
    List<Movie> findByRatingGreaterThanEqual(Double rating);
    List<Movie> findTop50ByEmotionStatus(String emotionStatus);

    @Query(value = """
        SELECT me.movie_id,
          SUM(CASE WHEN me.emotion_id = :r1 THEN 3
                   WHEN me.emotion_id = :r2 THEN 2
                   WHEN me.emotion_id = :r3 THEN 1
                   ELSE 0 END) as emotion_score
        FROM cinemo.movie_emotions me
        WHERE me.emotion_id IN (:r1, :r2, :r3)
        GROUP BY me.movie_id
        ORDER BY emotion_score DESC
        """, nativeQuery = true)
    List<Object[]> findMovieScores(@Param("r1") Integer r1,
                                   @Param("r2") Integer r2,
                                   @Param("r3") Integer r3);

    default List<ScoredMovie> searchScoredMovies(List<Integer> emotionIds) {
        if (emotionIds == null || emotionIds.size() != 3) {
            throw new IllegalArgumentException("emotionIds must contain exactly 3 elements in order [rank1, rank2, rank3].");
        }
        final Integer r1 = emotionIds.get(0);
        final Integer r2 = emotionIds.get(1);
        final Integer r3 = emotionIds.get(2);

        List<Object[]> rows = findMovieScores(r1, r2, r3);

        Map<Long, Integer> scoreMap = new LinkedHashMap<>();
        List<Long> movieIds = new ArrayList<>();
        for (Object[] row : rows) {
            Long movieId = ((Number) row[0]).longValue();
            Integer score = ((Number) row[1]).intValue();
            scoreMap.put(movieId, score);
            movieIds.add(movieId);
        }

        List<Movie> movies = findAllById(movieIds);

        Map<Long, Movie> movieMap = new LinkedHashMap<>();
        for (Movie movie : movies) {
            movieMap.put(movie.getMovieId(), movie);
        }

        List<ScoredMovie> result = new ArrayList<>();
        for (Long movieId : movieIds) {
            Movie movie = movieMap.get(movieId);
            if (movie != null) {
                result.add(new ScoredMovie(movie, scoreMap.get(movieId)));
            }
        }

        result.sort(Comparator.comparingInt(ScoredMovie::score).reversed());

        // スコア=6（満点）は必ず含める。残り枠を score >= 2 で埋めて上限20件
        final int PERFECT_SCORE = 6;
        final int SCORE_THRESHOLD = 2;
        final int LIMIT = 20;

        List<ScoredMovie> perfect = new ArrayList<>();
        List<ScoredMovie> others = new ArrayList<>();
        for (ScoredMovie sm : result) {
            if (sm.score() == PERFECT_SCORE) {
                perfect.add(sm);
            } else if (sm.score() >= SCORE_THRESHOLD) {
                others.add(sm);
            }
        }

        List<ScoredMovie> filtered = new ArrayList<>(perfect);
        int remaining = Math.max(0, LIMIT - perfect.size());
        for (int i = 0; i < remaining && i < others.size(); i++) {
            filtered.add(others.get(i));
        }

        return filtered;
    }

}
