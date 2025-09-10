package com.cinemo.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "movie_emotions")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MovieEmotion {

    @EmbeddedId
    private MovieEmotionId id;

    @ManyToOne(fetch = FetchType.LAZY) @MapsId("movieId")
    @JoinColumn(name = "movie_id", referencedColumnName = "movie_id")
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY) @MapsId("emotionId")
    @JoinColumn(name = "emotion_id", referencedColumnName = "emotion_id")
    private Emotion emotion;

    @Column(name = "rank", nullable = false)
    private Integer rank; // CHECK (1,2,3) の想定

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
