package com.quizmaster.model;
import java.time.LocalDateTime;
public class QuizAttempt {
    private String attemptId;
    private String userId;
    private String quizId;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private QuizResult result;
    private boolean completed;
    public QuizAttempt() {
    }
    public QuizAttempt(String attemptId, String userId, String quizId) {
        this.attemptId = attemptId;
        this.userId = userId;
        this.quizId = quizId;
        this.startedAt = LocalDateTime.now();
        this.completed = false;
    }
    public void finish(QuizResult result) {
        if (completed) {
            throw new IllegalStateException("Attempt is already completed");
        }
        if (result == null) {
            throw new IllegalArgumentException("Result cannot be null");
        }
        this.result = result;
        this.finishedAt = LocalDateTime.now();
        this.completed = true;
    }
    public String getAttemptId() {
        return attemptId;
    }
    public String getUserId() {
        return userId;
    }
    public String getQuizId() {
        return quizId;
    }
    public LocalDateTime getStartedAt() {
        return startedAt;
    }
    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }
    public QuizResult getResult() {
        return result;
    }
    public boolean isCompleted() {
        return completed;
    }
    public void setAttemptId(String attemptId) {
        this.attemptId = attemptId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }
    public void setResult(QuizResult result) {
        this.result = result;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
