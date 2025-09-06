package com.cinemo.api.agent;

import java.util.List;

public record EmotionAnalysisResponse(
        /**
         * 分析対象となったユーザーの入力テキスト
         */
        String inputText,

        /**
         * 検出された感情のリスト（信頼度順にランク付け）
         */
        List<DetectedEmotion> detectedEmotions,

        /**
         * 感情分析の総合的な評価・判断
         */
        String overallAssessment
) {
    /**
     * 検出された個別の感情情報
     */
    public record DetectedEmotion(
            /**
             * 感情の順位（1が最も可能性が高い）
             */
            int rank,

            /**
             * 感情名
             */
            String emotion,

            /**
             * 信頼度（0-100の範囲）
             */
            int confidence,

            /**
             * この感情と判断した理由・根拠
             */
            String reason
    ) {
    }
}