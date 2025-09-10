package com.cinemo.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode
public class MovieGenreId implements Serializable {
    @Column(name = "movie_id", nullable = false)
    private Long movieId;

    @Column(name = "genre_id", nullable = false)
    private Long genreId;
}
