package com.quizmaster.observer;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;
class QuizTimerTest {
    @Test
    void shouldNotifyObserverWhenTimeChanges() throws Exception {
        QuizTimer timer = new QuizTimer(2);
        List<Integer> times = new CopyOnWriteArrayList<>();
        CountDownLatch tick = new CountDownLatch(2);
        timer.addObserver(new TimerObserver() {
            @Override
            public void onTimeChanged(int remainingSeconds) {
                times.add(remainingSeconds);
                tick.countDown();
            }
            @Override
            public void onTimeFinished() {
            }
        });
        try {
            timer.start();
            assertTrue(tick.await(3, TimeUnit.SECONDS));
            assertTrue(times.contains(2));
            assertTrue(times.contains(1));
        } finally {
            timer.close();
        }
    }
    @Test
    void shouldNotifyWhenTimeFinishes() throws Exception {
        QuizTimer timer = new QuizTimer(1);
        CountDownLatch finished = new CountDownLatch(1);
        timer.addObserver(new TimerObserver() {
            @Override
            public void onTimeChanged(int remainingSeconds) {
            }
            @Override
            public void onTimeFinished() {
                finished.countDown();
            }
        });
        try {
            timer.start();
            assertTrue(finished.await(3, TimeUnit.SECONDS));
        } finally {
            timer.close();
        }
    }
    @Test
    void shouldRejectInvalidDuration() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new QuizTimer(0)
        );
    }
}
