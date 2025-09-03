package com.cinemo.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "movie_genres")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MovieGenre {

    @EmbeddedId
    private MovieGenreId id;

    @ManyToOne(fetch = FetchType.LAZY) @MapsId("movieId")
    @JoinColumn(name = "movie_id", referencedColumnName = "movie_id")
    private Movie movie;

    // マスタが無い想定なので単なる数値
    @Column(name = "genre_id", insertable = false, updatable = false)
    private Integer genreId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
