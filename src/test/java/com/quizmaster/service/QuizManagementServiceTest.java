package com.quizmaster.service;

import com.quizmaster.builder.QuizBuilder;
import com.quizmaster.model.Difficulty;
import com.quizmaster.model.Quiz;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuizManagementServiceTest {

    private QuizManagementService service;
    private Quiz quiz;

    @BeforeEach
    void setUp() {
        service = new QuizManagementService();

        quiz = new QuizBuilder()
                .setId("quiz1")
                .setTitle("Java Basics")
                .setDescription("Java beginner test")
                .setCategory("Programming")
                .setDifficulty(Difficulty.EASY)
                .setTimeLimitSeconds(600)
                .setQuestionLimit(10)
                .addQuestion(
                        new com.quizmaster.factory.MultipleChoiceFactory()
                                .createQuestion(
                                        "q1",
                                        "What is Java?",
                                        Difficulty.EASY,
                                        1,
                                        java.util.List.of(
                                                new com.quizmaster.model.Answer(
                                                        "a1", "Language", true
                                                ),
                                                new com.quizmaster.model.Answer(
                                                        "a2", "Browser", false
                                                )
                                        )
                                )
                )
                .build();
    }

    @Test
    void shouldCreateQuiz() {
        service.createQuiz(quiz);

        assertEquals(1, service.getQuizCount());
        assertEquals("Java Basics", service.getQuiz("quiz1").getTitle());
    }

    @Test
    void shouldRejectDuplicateQuiz() {
        service.createQuiz(quiz);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.createQuiz(quiz)
        );
    }

    @Test
    void shouldDeleteQuiz() {
        service.createQuiz(quiz);

        service.deleteQuiz("quiz1");

        assertEquals(0, service.getQuizCount());
    }

    @Test
    void shouldFindQuizByCategory() {
        service.createQuiz(quiz);

        assertEquals(1, service.findByCategory("Programming").size());
    }

    @Test
    void shouldRejectUnknownQuiz() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.getQuiz("unknown")
        );
    }
}