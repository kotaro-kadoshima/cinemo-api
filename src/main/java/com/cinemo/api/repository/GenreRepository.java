package com.cinemo.api.repository;

import com.cinemo.api.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// genres
@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
}
