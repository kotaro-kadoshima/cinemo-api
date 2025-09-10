package com.cinemo.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "emotions")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Emotion {

    @Id
    @Column(name = "emotion_id")
    private Integer emotionId;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Override
    public String toString(){
        return name;
    }
}
