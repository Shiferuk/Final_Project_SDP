package com.quizmaster.observer;
public interface TimerObserver {
    void onTimeChanged(int remainingSeconds);
    void onTimeFinished();
}
