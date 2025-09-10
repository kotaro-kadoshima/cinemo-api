package com.cinemo.api.controller;

import com.cinemo.api.entity.Movie;
import com.cinemo.api.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/movies")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    // 指定されたIDの映画を取得
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        Optional<Movie> movie = movieService.getMovieById(id);
        return movie.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // タイトルで検索（クエリパラメータ使用）
    @GetMapping
    public ResponseEntity<List<Movie>> getMovies(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Double rating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // パラメータに応じて適切なサービスメソッドを呼び出し
        List<Movie> movies;
        if (title != null) {
            movies = movieService.searchMoviesByTitle(title);
        } else if (language != null) {
            movies = movieService.getMoviesByLanguage(language);
        } else if (rating != null) {
            movies = movieService.getMoviesByRating(rating);
        } else {
            movies = movieService.getAllMovies();
        }

        return ResponseEntity.ok(movies);
    }

    // 映画を新規作成
    @PostMapping
    public ResponseEntity<Movie> createMovie(@RequestBody Movie movie) {
        Movie savedMovie = movieService.saveMovie(movie);
        return ResponseEntity.ok(savedMovie);
    }

    // 映画を更新
    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @RequestBody Movie movie) {
        movie.setMovieId(id);
        Movie updatedMovie = movieService.saveMovie(movie);
        return ResponseEntity.ok(updatedMovie);
    }

    // 映画を削除
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}