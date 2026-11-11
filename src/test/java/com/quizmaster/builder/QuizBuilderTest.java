package com.quizmaster.builder;

import com.quizmaster.factory.MultipleChoiceFactory;
import com.quizmaster.factory.QuestionFactory;
import com.quizmaster.model.Answer;
import com.quizmaster.model.Difficulty;
import com.quizmaster.model.Question;
import com.quizmaster.model.Quiz;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuizBuilderTest {

    @Test
    void shouldBuildQuizSuccessfully() {

        QuestionFactory factory = new MultipleChoiceFactory();

        Question question = factory.createQuestion(
                "q1",
                "What is Java?",
                Difficulty.EASY,
                1,
                List.of(
                        new Answer("a1", "Programming language", true),
                        new Answer("a2", "Operating system", false)
                )
        );

        Quiz quiz = new QuizBuilder()
                .setId("quiz1")
                .setTitle("Java Basics")
                .setDescription("Basic Java test")
                .setCategory("Programming")
                .setDifficulty(Difficulty.EASY)
                .setTimeLimitSeconds(600)
                .setQuestionLimit(10)
                .addQuestion(question)
                .build();

        assertNotNull(quiz);
        assertEquals("Java Basics", quiz.getTitle());
        assertEquals(1, quiz.getQuestions().size());
    }

    @Test
    void shouldRejectQuizWithoutTitle() {

        QuizBuilder builder = new QuizBuilder()
                .setId("quiz2")
                .setCategory("Programming")
                .setDifficulty(Difficulty.EASY)
                .setTimeLimitSeconds(600)
                .setQuestionLimit(10);

        assertThrows(IllegalStateException.class, builder::build);
    }
}