package com.cinemo.api.batch;

import com.cinemo.api.entity.*;
import com.cinemo.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.ai.ollama.OllamaChatModel;

import java.util.*;

@Component
@Profile("batch")
public class MotionBatch implements CommandLineRunner {

    private static final String TARGET_STATUS = "処理前";
    private static final String PROCESSED_STATUS = "処理済";
    private static final int BATCH_SIZE = 50;

    @Autowired private MovieRepository movieRepository;
    @Autowired private EmotionRepository emotionRepository;
    @Autowired private MovieEmotionRepository movieEmotionRepository;
    @Autowired private OllamaChatModel ollama; // AI呼び出し

    @Override
    public void run(String... args) {
        List<Emotion> allEmotions = emotionRepository.findAll();
        Map<String, Integer> emotionNameToId = new HashMap<>();
        allEmotions.forEach(e -> emotionNameToId.put(e.getName(), e.getEmotionId()));

        int page = 0;
        while (true) {
            Pageable pageable = PageRequest.of(page, BATCH_SIZE, Sort.by("movieId").ascending());
            Page<Movie> chunk = movieRepository.findByEmotionStatus(TARGET_STATUS, pageable);
            if (chunk.isEmpty()) break;

            for (Movie m : chunk) {
                System.out.printf("Processing movie %d | %s%n", m.getMovieId(), m.getTitle());

                // AIへ問い合わせ
                String prompt = buildPrompt(m.getOverview(), allEmotions);
                String response = ollama.call(prompt);

                // フォーマット検証 & パース
                List<String> parsed = parseResponse(response);
                if (parsed.size() == 3) {
                    for (int i = 0; i < parsed.size(); i++) {
                        String emotionName = parsed.get(i);
                        Integer emotionId = emotionNameToId.get(emotionName);
                        if (emotionId != null) {
                            MovieEmotionId id = new MovieEmotionId(m.getMovieId(), emotionId);
                            MovieEmotion me = new MovieEmotion(
                                    id,
                                    m,
                                    new Emotion(emotionId, emotionName),
                                    i + 1, // rank 1,2,3
                                    java.time.LocalDateTime.now()
                            );
                            movieEmotionRepository.save(me);
                        }
                    }
                    // 処理が成功したら status を更新
                    m.setEmotionStatus(PROCESSED_STATUS);
                    movieRepository.save(m);
                } else {
                    System.err.printf("フォーマット不正 (movieId=%d): %s%n", m.getMovieId(), response);
                }
            }

            if (!chunk.hasNext()) break;
            page++;
        }
    }

    private String buildPrompt(String overview, List<Emotion> emotions) {
        return """
            次の映画の概要から、鑑賞時に得られる感情を上位三つ抽出してください。
            以下のフォーマットで返してください。

            フォーマット:
            1:<感情名>
            2:<感情名>
            3:<感情名>

            映画概要:
            """ + overview + """

            使用可能な感情候補:
            """ + String.join(",", emotions.stream().map(Emotion::getName).toList());
    }

    private List<String> parseResponse(String response) {
        List<String> result = new ArrayList<>();
        if (!StringUtils.hasText(response)) return result;

        for (String line : response.split("\n")) {
            line = line.trim();
            if (line.startsWith("1:") || line.startsWith("2:") || line.startsWith("3:")) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    result.add(parts[1].trim());
                }
            }
        }
        return result;
    }
}
