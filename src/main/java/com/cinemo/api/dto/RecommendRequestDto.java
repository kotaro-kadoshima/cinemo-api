package com.cinemo.api.dto;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendRequestDto {
    private String mood;
    private String country;
    private List<String> genres;
    private Integer limit;
}
