package com.quizmaster.facade;
import com.quizmaster.model.QuizAttempt;
import com.quizmaster.model.QuizResult;
import com.quizmaster.observer.QuizTimer;
import com.quizmaster.observer.TimerObserver;
import com.quizmaster.repository.AttemptRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
public class QuizFacade implements TimerObserver, AutoCloseable {
    private QuizTimer timer;
    private QuizAttempt currentAttempt;
    private final AttemptRepository repository;
    private int totalQuestions;
    private int correctAnswers;
    private int answeredQuestions;
    private int score;
    private final List<QuizAttempt> history = new ArrayList<>();
    private final List<TimerObserver> observers = new ArrayList<>();
    public QuizFacade() {
        repository = new AttemptRepository("data/attempts.json");
        history.addAll(repository.load());
    }
    public void startQuiz(String userId, String quizId,
                          int durationSeconds, int totalQuestions) {
        if (currentAttempt != null && !currentAttempt.isCompleted()) {
            throw new IllegalStateException("A quiz is already running");
        }
        if (totalQuestions <= 0) {
            throw new IllegalArgumentException(
                    "Total questions must be greater than zero"
            );
        }
        if (timer != null) {
            timer.close();
        }
        this.totalQuestions = totalQuestions;
        this.correctAnswers = 0;
        this.answeredQuestions = 0;
        this.score = 0;
        currentAttempt = new QuizAttempt(
                UUID.randomUUID().toString(),
                userId,
                quizId
        );
        timer = new QuizTimer(durationSeconds);
        timer.addObserver(this);
        timer.start();
    }
    public void submitAnswer(boolean correct, int points) {
        if (currentAttempt == null || currentAttempt.isCompleted()) {
            throw new IllegalStateException("No active quiz");
        }
        if (answeredQuestions >= totalQuestions) {
            throw new IllegalStateException("All questions are answered");
        }
        if (points < 0) {
            throw new IllegalArgumentException("Points cannot be negative");
        }
        answeredQuestions++;
        if (correct) {
            correctAnswers++;
            score += points;
        }
    }
    public QuizResult finishQuiz() {
        if (currentAttempt == null || currentAttempt.isCompleted()) {
            throw new IllegalStateException("No active quiz");
        }
        if (timer != null) {
            timer.stop();
        }
        QuizResult result = new QuizResult(
                totalQuestions,
                correctAnswers,
                score
        );
        currentAttempt.finish(result);
        history.add(currentAttempt);
        repository.save(history);
        return result;
    }
    public void addObserver(TimerObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Observer cannot be null");
        }
        observers.add(observer);
    }
    public void removeObserver(TimerObserver observer) {
        observers.remove(observer);
    }
    public List<QuizAttempt> getHistory() {
        return Collections.unmodifiableList(history);
    }
    public QuizAttempt getCurrentAttempt() {
        return currentAttempt;
    }
    @Override
    public void onTimeChanged(int remainingSeconds) {
        for (TimerObserver observer : observers) {
            observer.onTimeChanged(remainingSeconds);
        }
    }
    @Override
    public void onTimeFinished() {
        if (currentAttempt != null && !currentAttempt.isCompleted()) {
            finishQuiz();
        }
        for (TimerObserver observer : observers) {
            observer.onTimeFinished();
        }
    }
    @Override
    public void close() {
        if (timer != null) {
            timer.close();
        }
    }
}
