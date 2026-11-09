package com.quizmaster.model;
public class QuizResult {
    private int totalQuestions;
    private int correctAnswers;
    private int score;
    private double percentage;
    public QuizResult() {
    }
    public QuizResult(int totalQuestions, int correctAnswers, int score) {
        if (totalQuestions <= 0) {
            throw new IllegalArgumentException(
                    "Total questions must be greater than zero"
            );
        }
        if (correctAnswers < 0 || correctAnswers > totalQuestions) {
            throw new IllegalArgumentException(
                    "Invalid number of correct answers"
            );
        }
        if (score < 0) {
            throw new IllegalArgumentException(
                    "Score cannot be negative"
            );
        }
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.score = score;
        this.percentage = (double) correctAnswers / totalQuestions * 100;
    }
    public int getTotalQuestions() {
        return totalQuestions;
    }
    public int getCorrectAnswers() {
        return correctAnswers;
    }
    public int getScore() {
        return score;
    }
    public double getPercentage() {
        return percentage;
    }
    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }
    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }
    public void setScore(int score) {
        this.score = score;
    }
    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
