package com.cinemo.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.*;

@Embeddable
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode
public class MovieEmotionId implements Serializable {
    @Column(name = "movie_id", nullable = false)
    private Long movieId;

    @Column(name = "emotion_id", nullable = false)
    private Long emotionId;
}