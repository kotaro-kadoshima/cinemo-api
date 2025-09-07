package com.cinemo.api.dto;

import java.util.List;

public class RecommendResponseDto {
    private List<RecommendItemDto> items;

    public RecommendResponseDto() {}

    public RecommendResponseDto(List<RecommendItemDto> items) {
        this.items = items;
    }

    public List<RecommendItemDto> getItems() {
        return items;
    }

    public void setItems(List<RecommendItemDto> items) {
        this.items = items;
    }
}
