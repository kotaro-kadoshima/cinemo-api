package com.cinemo.api.dto;

import com.cinemo.api.entity.Movie;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AnalysisMovieDto {
    public Movie movie;
    public String reason;
}
