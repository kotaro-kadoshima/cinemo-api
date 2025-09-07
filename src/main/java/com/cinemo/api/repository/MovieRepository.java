package com.cinemo.api.repository;

import com.cinemo.api.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    // ========= ここから：rank順の感情で検索 =========
    /**
     * rank=1,2,3 に対応する感情IDで映画を検索（指定がnullのrankは無視）
     * - r1 = emotionIds[0]（rank1）
     * - r2 = emotionIds[1]（rank2）
     * - r3 = emotionIds[2]（rank3）
     * 提供されたrankだけANDで絞り込みます。
     */
    @Query(value = """
        SELECT DISTINCT m.*
        FROM movies m
        WHERE
          (:r1 IS NULL OR EXISTS (
              SELECT 1 FROM movie_emotions me
              WHERE me.movie_id = m.movie_id AND me.rank = 1 AND me.emotion_id = :r1
          ))
          AND (:r2 IS NULL OR EXISTS (
              SELECT 1 FROM movie_emotions me
              WHERE me.movie_id = m.movie_id AND me.rank = 2 AND me.emotion_id = :r2
          ))
          AND (:r3 IS NULL OR EXISTS (
              SELECT 1 FROM movie_emotions me
              WHERE me.movie_id = m.movie_id AND me.rank = 3 AND me.emotion_id = :r3
          ))
        """, nativeQuery = true)
    List<Movie> searchMoviesByRankedEmotions(@Param("r1") Integer r1,
                                             @Param("r2") Integer r2,
                                             @Param("r3") Integer r3);

    /**
     * List<Integer> を受け取るフロントメソッド。
     * サイズが 0..3 の範囲であれば、その分だけ条件を掛けます。
     */
    default List<Movie> searchMovies(List<Integer> emotionIds) {
        Integer r1 = (emotionIds != null && emotionIds.size() >= 1) ? emotionIds.get(0) : null;
        Integer r2 = (emotionIds != null && emotionIds.size() >= 2) ? emotionIds.get(1) : null;
        Integer r3 = (emotionIds != null && emotionIds.size() >= 3) ? emotionIds.get(2) : null;

        // 何も指定が無ければ全件返す
        if (r1 == null && r2 == null && r3 == null) {
            return findAll();
        }
        return searchMoviesByRankedEmotions(r1, r2, r3);
    }
}
