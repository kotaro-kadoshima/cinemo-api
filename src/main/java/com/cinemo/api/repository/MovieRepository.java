package com.cinemo.api.repository;

import com.cinemo.api.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;


import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {

    // カスタムクエリ：全映画を取得（SQLを明示的に指定）
    @Query(value = "SELECT movie_id, title, original_title, duration, release_date, " +
            "poster_url, rating, overview, adult, original_language, " +
            "emotion_status, created_at, updated_at FROM public.movies",
            nativeQuery = true)
    List<Movie> findAllMovies();

    // 追加のカスタムクエリ例
    List<Movie> findByTitleContaining(String title);

    List<Movie> findByOriginalLanguage(String language);

    List<Movie> findByRatingGreaterThanEqual(Double rating);

    // emotion_statusを条件に上位50件を取得
    List<Movie> findTop50ByEmotionStatus(String emotionStatus);

    Page<Movie> findByEmotionStatus(String emotionStatus, Pageable pageable);

}
