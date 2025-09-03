package com.cinemo.api.repository;

import com.cinemo.api.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// emotions
@Repository
public interface EmotionRepository extends JpaRepository<Emotion, Long> {
}
