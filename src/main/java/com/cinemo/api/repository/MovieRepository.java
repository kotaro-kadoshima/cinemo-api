package com.cinemo.api.repository;

import com.cinemo.api.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;

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

  // ① 1つ目・2つ目・3つ目の感情がすべて一致
    @Query(value = """
        SELECT DISTINCT m.*
        FROM movies m
        WHERE EXISTS (
            SELECT 1 FROM movie_emotions me
            WHERE me.movie_id = m.movie_id AND me.rank = 1 AND me.emotion_id = :r1
        )
        AND EXISTS (
            SELECT 1 FROM movie_emotions me
            WHERE me.movie_id = m.movie_id AND me.rank = 2 AND me.emotion_id = :r2
        )
        AND EXISTS (
            SELECT 1 FROM movie_emotions me
            WHERE me.movie_id = m.movie_id AND me.rank = 3 AND me.emotion_id = :r3
        )
        """, nativeQuery = true)
    List<Movie> findByExactRanks123(@Param("r1") Integer r1,
                                    @Param("r2") Integer r2,
                                    @Param("r3") Integer r3);

    // ② 1つ目・2つ目の感情が一致
    @Query(value = """
        SELECT DISTINCT m.*
        FROM movies m
        WHERE EXISTS (
            SELECT 1 FROM movie_emotions me
            WHERE me.movie_id = m.movie_id AND me.rank = 1 AND me.emotion_id = :r1
        )
        AND EXISTS (
            SELECT 1 FROM movie_emotions me
            WHERE me.movie_id = m.movie_id AND me.rank = 2 AND me.emotion_id = :r2
        )
        """, nativeQuery = true)
    List<Movie> findByExactRanks12(@Param("r1") Integer r1,
                                   @Param("r2") Integer r2);

    // ③ 1つ目の感情が一致
    @Query(value = """
        SELECT DISTINCT m.*
        FROM movies m
        WHERE EXISTS (
            SELECT 1 FROM movie_emotions me
            WHERE me.movie_id = m.movie_id AND me.rank = 1 AND me.emotion_id = :r1
        )
        """, nativeQuery = true)
    List<Movie> findByExactRank1(@Param("r1") Integer r1);

    default List<Movie> searchMovies(List<Integer> emotionIds) {
        if (emotionIds == null || emotionIds.size() != 3) {
            throw new IllegalArgumentException("emotionIds must contain exactly 3 elements in order [rank1, rank2, rank3].");
        }
        final Integer r1 = emotionIds.get(0);
        final Integer r2 = emotionIds.get(1);
        final Integer r3 = emotionIds.get(2);

        // ① 3つ一致
        List<Movie> tier1 = findByExactRanks123(r1, r2, r3);
        // ② 上位2つ一致
        List<Movie> tier2 = findByExactRanks12(r1, r2);
        // ③ 1つ目一致
        List<Movie> tier3 = findByExactRank1(r1);

        // 重複排除しつつ優先順（①→②→③）を維持
        Map<Long, Movie> ordered = new LinkedHashMap<>();
        for (Movie m : tier1) ordered.putIfAbsent(m.getMovieId(), m);
        for (Movie m : tier2) ordered.putIfAbsent(m.getMovieId(), m);
        for (Movie m : tier3) ordered.putIfAbsent(m.getMovieId(), m);

        return new ArrayList<>(ordered.values());
    }

}
