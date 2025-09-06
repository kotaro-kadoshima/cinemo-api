package com.cinemo.api.repository;

import com.cinemo.api.entity.Emotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

// emotions
@Repository
public interface EmotionRepository extends JpaRepository<Emotion, Long> {

    // 複数の感情名で検索
    List<Emotion> findByNameIn(Collection<String> names);
}
