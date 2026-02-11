package com.cinemo.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "genres", schema = "cinemo")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Genre {

    @Id
    @Column(name = "genre_id")
    private Integer genreId; // TMDBのIDをそのまま使うなら Integer

    @Column(name = "name", nullable = false)
    private String name;
}