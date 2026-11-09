package com.quizmaster.facade;
import com.quizmaster.model.QuizResult;
import com.quizmaster.observer.TimerObserver;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;
class QuizFacadeTest {
    @Test
    void shouldStartAndFinishQuiz() {
        QuizFacade facade = new QuizFacade();
        try {
            int previousHistorySize = facade.getHistory().size();
            facade.startQuiz("user1", "quiz1", 60, 5);
            facade.submitAnswer(true, 10);
            facade.submitAnswer(false, 0);
            facade.submitAnswer(true, 10);
            QuizResult result = facade.finishQuiz();
            assertEquals(5, result.getTotalQuestions());
            assertEquals(2, result.getCorrectAnswers());
            assertEquals(20, result.getScore());
            assertEquals(40.0, result.getPercentage());
            assertTrue(facade.getCurrentAttempt().isCompleted());
            assertEquals(previousHistorySize + 1, facade.getHistory().size());
        } finally {
            facade.close();
        }
    }
    @Test
    void shouldRejectAnswerWithoutActiveQuiz() {
        QuizFacade facade = new QuizFacade();
        try {
            assertThrows(
                    IllegalStateException.class,
                    () -> facade.submitAnswer(true, 10)
            );
        } finally {
            facade.close();
        }
    }
    @Test
    void shouldRejectFinishingWithoutActiveQuiz() {
        QuizFacade facade = new QuizFacade();
        try {
            assertThrows(
                    IllegalStateException.class,
                    facade::finishQuiz
            );
        } finally {
            facade.close();
        }
    }
    @Test
    void shouldRejectAnswerAfterAllQuestionsAnswered() {
        QuizFacade facade = new QuizFacade();
        try {
            facade.startQuiz("user1", "quiz1", 60, 1);
            facade.submitAnswer(true, 10);
            assertThrows(
                    IllegalStateException.class,
                    () -> facade.submitAnswer(false, 0)
            );
        } finally {
            facade.close();
        }
    }
    @Test
    void shouldAutomaticallyFinishQuizWhenTimeRunsOut() throws Exception {
        QuizFacade facade = new QuizFacade();
        CountDownLatch finished = new CountDownLatch(1);
        try {
            int previousHistorySize = facade.getHistory().size();
            facade.addObserver(new TimerObserver() {
                @Override
                public void onTimeChanged(int remainingSeconds) {
                }
                @Override
                public void onTimeFinished() {
                    finished.countDown();
                }
            });
            facade.startQuiz("user1", "quiz1", 1, 5);
            facade.submitAnswer(true, 10);
            assertTrue(finished.await(3, TimeUnit.SECONDS));
            assertTrue(facade.getCurrentAttempt().isCompleted());
            assertEquals(previousHistorySize + 1, facade.getHistory().size());
            assertEquals(10, facade.getCurrentAttempt().getResult().getScore());
        } finally {
            facade.close();
        }
    }
}