package com.quizmaster.observer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
public class QuizTimer implements AutoCloseable {
    private final int durationSeconds;
    private volatile int remainingSeconds;
    private final List<TimerObserver> observers =
            new CopyOnWriteArrayList<>();
    private ScheduledExecutorService scheduler;
    public QuizTimer(int durationSeconds) {
        if (durationSeconds <= 0) {
            throw new IllegalArgumentException(
                    "Duration must be greater than zero"
            );
        }
        this.durationSeconds = durationSeconds;
        this.remainingSeconds = durationSeconds;
    }
    public void addObserver(TimerObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException(
                    "Observer cannot be null"
            );
        }
        observers.add(observer);
    }
    public void removeObserver(TimerObserver observer) {
        observers.remove(observer);
    }
    public synchronized void start() {
        if (scheduler != null && !scheduler.isShutdown()) {
            return;
        }
        if (remainingSeconds <= 0) {
            remainingSeconds = durationSeconds;
        }
        scheduler = Executors.newSingleThreadScheduledExecutor();
        notifyTimeChanged();
        scheduler.scheduleAtFixedRate(() -> {
            remainingSeconds--;
            notifyTimeChanged();
            if (remainingSeconds <= 0) {
                notifyTimeFinished();
                ScheduledExecutorService current = scheduler;
                if (current != null) {
                    current.shutdown();
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }
    public synchronized void stop() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
    }
    public synchronized void reset() {
        stop();
        remainingSeconds = durationSeconds;
        notifyTimeChanged();
    }
    public int getRemainingSeconds() {
        return remainingSeconds;
    }
    private void notifyTimeChanged() {
        for (TimerObserver observer : observers) {
            observer.onTimeChanged(remainingSeconds);
        }
    }
    private void notifyTimeFinished() {
        for (TimerObserver observer : observers) {
            observer.onTimeFinished();
        }
    }
    @Override
    public void close() {
        stop();
    }
}
