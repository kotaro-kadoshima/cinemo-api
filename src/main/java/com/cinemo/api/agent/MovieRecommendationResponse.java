package com.cinemo.api.agent;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"recommendations"})
public record MovieRecommendationResponse(
        @JsonProperty("recommendations")
        List<Recommendation> recommendations
) {

    @JsonPropertyOrder({"movieId", "title", "reason"})
    public record Recommendation(
            @JsonProperty("movieId")
            Long movieId,

            @JsonProperty("title")
            String title,

            @JsonProperty("reason")
            String reason
    ) {
    }
}