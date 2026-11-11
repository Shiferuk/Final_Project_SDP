package com.quizmaster.facade;

import com.quizmaster.model.QuizAttempt;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Статистика по истории попыток (считается на лету, ничего не хранится). */
public class HistoryStatistics {

    /** Статистика по одному тесту. */
    public static class QuizStats {
        private final String quizId;
        private int attempts;
        private double bestPercent;
        private double sumPercent;

        QuizStats(String quizId) { this.quizId = quizId; }

        public String getQuizId() { return quizId; }
        public int getAttempts() { return attempts; }
        public double getBestPercent() { return bestPercent; }
        public double getAveragePercent() { return attempts == 0 ? 0 : sumPercent / attempts; }
    }

    private final int totalAttempts;
    private final int totalQuestions;
    private final int totalCorrect;
    private final int totalScore;
    private final double bestPercent;
    private final double averagePercent;
    private final Map<String, QuizStats> perQuiz;

    private HistoryStatistics(int attempts, int questions, int correct, int score,
                              double best, double average, Map<String, QuizStats> perQuiz) {
        this.totalAttempts = attempts;
        this.totalQuestions = questions;
        this.totalCorrect = correct;
        this.totalScore = score;
        this.bestPercent = best;
        this.averagePercent = average;
        this.perQuiz = perQuiz;
    }

    public static HistoryStatistics from(List<QuizAttempt> history) {
        int attempts = 0, questions = 0, correct = 0, score = 0;
        double best = 0, sum = 0;
        Map<String, QuizStats> perQuiz = new LinkedHashMap<>();

        for (QuizAttempt attempt : history) {
            if (attempt.getResult() == null) {
                continue;
            }
            double percent = attempt.getResult().getPercentage();
            attempts++;
            questions += attempt.getResult().getTotalQuestions();
            correct += attempt.getResult().getCorrectAnswers();
            score += attempt.getResult().getScore();
            sum += percent;
            best = Math.max(best, percent);

            QuizStats stats = perQuiz.computeIfAbsent(attempt.getQuizId(), QuizStats::new);
            stats.attempts++;
            stats.sumPercent += percent;
            stats.bestPercent = Math.max(stats.bestPercent, percent);
        }

        return new HistoryStatistics(attempts, questions, correct, score, best,
                attempts == 0 ? 0 : sum / attempts, perQuiz);
    }

    public int getTotalAttempts() { return totalAttempts; }
    public int getTotalQuestions() { return totalQuestions; }
    public int getTotalCorrect() { return totalCorrect; }
    public int getTotalWrong() { return totalQuestions - totalCorrect; }
    public int getTotalScore() { return totalScore; }
    public double getBestPercent() { return bestPercent; }
    public double getAveragePercent() { return averagePercent; }
    public Map<String, QuizStats> getPerQuiz() { return perQuiz; }
}
