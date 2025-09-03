package com.cinemo.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "movie_providers")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MovieProvider {

    @EmbeddedId
    private MovieProviderId id;

    @ManyToOne(fetch = FetchType.LAZY) @MapsId("movieId")
    @JoinColumn(name = "movie_id", referencedColumnName = "movie_id")
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY) @MapsId("providerId")
    @JoinColumn(name = "provider_id", referencedColumnName = "provider_id")
    private Provider provider;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
