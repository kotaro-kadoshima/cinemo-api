package com.cinemo.api.repository;

import com.cinemo.api.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


// movie_genre (複合キー)
@Repository
public interface MovieGenreRepository extends JpaRepository<MovieGenre, MovieGenreId> {
    java.util.List<MovieGenre> findByMovie_MovieId(Long movieId);
}