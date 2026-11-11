package com.quizmaster.strategy;

import java.util.Collections;
import java.util.List;

/** Результат подсчёта: баллы, максимум, процент, список ошибок. */
public class ScoreReport {
    private final int score;
    private final int maxScore;
    private final int correctCount;
    private final int totalQuestions;
    private final List<String> wrongQuestionIds;

    public ScoreReport(int score, int maxScore, int correctCount, int totalQuestions, List<String> wrongQuestionIds) {
        this.score = score;
        this.maxScore = maxScore;
        this.correctCount = correctCount;
        this.totalQuestions = totalQuestions;
        this.wrongQuestionIds = Collections.unmodifiableList(wrongQuestionIds);
    }

    public int getScore() { return score; }
    public int getMaxScore() { return maxScore; }
    public int getCorrectCount() { return correctCount; }
    public int getTotalQuestions() { return totalQuestions; }
    public List<String> getWrongQuestionIds() { return wrongQuestionIds; }

    /** Процент от максимума (0..100). Бонусы могут давать значение выше 100 - ограничиваем. */
    public double getPercent() {
        if (maxScore == 0) return 0;
        return Math.min(100.0, score * 100.0 / maxScore);
    }

    @Override
    public String toString() {
        return String.format("%d/%d (%.1f%%), верно %d из %d", score, maxScore, getPercent(), correctCount, totalQuestions);
    }
}
