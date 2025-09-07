package com.cinemo.api.dto;

import lombok.Data;
import java.util.List;

@Data
public class RecommendItemDto {
    private String title;
    private String posterUrl;
    private String reason;
    private Integer tmdbId;
    private Integer duration;
    private Double rating;
    private List<String> genres;
    private List<String> emotionTags;
    private String origin;

    public RecommendItemDto() {}

    public RecommendItemDto(String title, String posterUrl, String reason,
                            Integer tmdbId, Integer duration, Double rating,
                            List<String> genres, List<String> emotionTags,
                            String origin) {
        this.title = title;
        this.posterUrl = posterUrl;
        this.reason = reason;
        this.tmdbId = tmdbId;
        this.duration = duration;
        this.rating = rating;
        this.genres = genres;
        this.emotionTags = emotionTags;
        this.origin = origin;
    }
}