package com.cinemo.api;

import com.cinemo.api.entity.*;
import com.cinemo.api.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RepositoriesIntegrationTest {

    @Autowired private MovieRepository movieRepository;
    @Autowired private ProviderRepository providerRepository;
    @Autowired private EmotionRepository emotionRepository;
    @Autowired private MovieEmotionRepository movieEmotionRepository;
    @Autowired private MovieProviderRepository movieProviderRepository;
    @Autowired private MovieGenreRepository movieGenreRepository;

    @Test
    void selectMovies() {
        System.out.println("=== Movies ===");
        movieRepository.findAll().stream().findFirst()
                .ifPresent(m -> System.out.println(m.getMovieId() + " : " + m.getTitle()));
    }

    @Test
    void selectProviders() {
        System.out.println("=== Providers ===");
        providerRepository.findAll().stream().findFirst()
                .ifPresent(p -> System.out.println(p.getProviderId() + " : " + p.getProviderName()));
    }

    @Test
    void selectEmotions() {
        System.out.println("=== Emotions ===");
        emotionRepository.findAll().stream().findFirst()
                .ifPresent(e -> System.out.println(e.getEmotionId() + " : " + e.getName()));
    }

    @Test
    void selectMovieEmotions() {
        System.out.println("=== MovieEmotions ===");
        movieEmotionRepository.findAll().stream().findFirst()
                .ifPresent(me -> System.out.println(
                        me.getId().getMovieId() + " - " + me.getId().getEmotionId() + " rank=" + me.getRank()
                ));
    }

    @Test
    void selectMovieProviders() {
        System.out.println("=== MovieProviders ===");
        movieProviderRepository.findAll().stream().findFirst()
                .ifPresent(mp -> System.out.println(
                        mp.getId().getMovieId() + " - " + mp.getId().getProviderId()
                ));
    }

    @Test
    void selectMovieGenres() {
        System.out.println("=== MovieGenres ===");
        movieGenreRepository.findAll().stream().findFirst()
                .ifPresent(mg -> System.out.println(
                        mg.getId().getMovieId() + " - " + mg.getId().getGenreId()
                ));
    }
}
