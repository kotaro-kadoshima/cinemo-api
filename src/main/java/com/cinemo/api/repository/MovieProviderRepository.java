package com.cinemo.api.repository;

import com.cinemo.api.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// movie_providers (複合キー)
@Repository
public interface MovieProviderRepository extends JpaRepository<MovieProvider, MovieProviderId> {
    java.util.List<MovieProvider> findByMovie_MovieId(Long movieId);
    java.util.List<MovieProvider> findByProvider_ProviderId(Long providerId);
}