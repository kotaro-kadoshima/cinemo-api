package com.cinemo.api.service;


import com.cinemo.api.entity.Movie;
import com.cinemo.api.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    // 全映画を取得
    public List<Movie> getAllMovies() {
        return movieRepository.findAllMovies(); // カスタムクエリを使用
        // または標準的な方法: return movieRepository.findAll();
    }

    // IDで映画を取得
    public Optional<Movie> getMovieById(Integer id) {
        return movieRepository.findById(id);
    }

    // タイトルで検索
    public List<Movie> searchMoviesByTitle(String title) {
        return movieRepository.findByTitleContaining(title);
    }

    // 言語で絞り込み
    public List<Movie> getMoviesByLanguage(String language) {
        return movieRepository.findByOriginalLanguage(language);
    }

    // 評価で絞り込み
    public List<Movie> getMoviesByRating(Double minRating) {
        return movieRepository.findByRatingGreaterThanEqual(minRating);
    }

    // 映画を保存
    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    // 映画を削除
    public void deleteMovie(Integer id) {
        movieRepository.deleteById(id);
    }
}

