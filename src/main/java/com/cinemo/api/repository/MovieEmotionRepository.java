package com.cinemo.api.repository;

import com.cinemo.api.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// movie_emotions (複合キー)
@Repository
public interface MovieEmotionRepository extends JpaRepository<MovieEmotion, MovieEmotionId> {
    // 例: 映画ごとの感情を取得
    java.util.List<MovieEmotion> findByMovie_MovieId(Long movieId);
}