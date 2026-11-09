package com.quizmaster.model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class QuizResultTest {
    @Test
    void shouldCalculatePercentage() {
        QuizResult result = new QuizResult(10, 8, 80);
        assertEquals(80.0, result.getPercentage());
        assertEquals(10, result.getTotalQuestions());
        assertEquals(8, result.getCorrectAnswers());
        assertEquals(80, result.getScore());
    }
    @Test
    void shouldCalculateZeroPercentage() {
        QuizResult result = new QuizResult(10, 0, 0);
        assertEquals(0.0, result.getPercentage());
    }
    @Test
    void shouldRejectInvalidTotalQuestions() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new QuizResult(0, 0, 0)
        );
    }
    @Test
    void shouldRejectInvalidCorrectAnswers() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new QuizResult(10, 11, 100)
        );
    }
}
