package com.cinemo.api.dto;

import com.cinemo.api.entity.Movie;

public record ScoredMovie(Movie movie, int score) {}
