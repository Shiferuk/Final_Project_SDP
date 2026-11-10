
package com.quizmaster.builder;

import com.quizmaster.factory.MultipleChoiceFactory;
import com.quizmaster.model.Answer;
import com.quizmaster.model.Difficulty;
import com.quizmaster.model.Question;
import com.quizmaster.model.Quiz;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class QuizBuilderValidationTest {

    @Test
    void shouldRejectQuizWithoutTitle() {
        QuizBuilder builder = new QuizBuilder()
                .setId(UUID.randomUUID().toString())
                .setDescription("Test description")
                .setCategory("Programming")
                .setDifficulty(Difficulty.EASY)
                .setTimeLimitSeconds(600)
                .setQuestionLimit(5)
                .addQuestion(createQuestion("What is Java?"));

        assertThrows(
                IllegalArgumentException.class,
                builder::build
        );
    }

    @Test
    void shouldRejectQuizWithoutQuestions() {
        QuizBuilder builder = new QuizBuilder()
                .setId(UUID.randomUUID().toString())
                .setTitle("Java Basics")
                .setDescription("Test description")
                .setCategory("Programming")
                .setDifficulty(Difficulty.EASY)
                .setTimeLimitSeconds(600)
                .setQuestionLimit(5);

        assertThrows(
                IllegalArgumentException.class,
                builder::build
        );
    }

    @Test
    void shouldRejectQuizWhenQuestionCountExceedsLimit() {
        QuizBuilder builder = new QuizBuilder()
                .setId(UUID.randomUUID().toString())
                .setTitle("Java Basics")
                .setDescription("Test description")
                .setCategory("Programming")
                .setDifficulty(Difficulty.EASY)
                .setTimeLimitSeconds(600)
                .setQuestionLimit(1)
                .addQuestion(createQuestion("Question one?"))
                .addQuestion(createQuestion("Question two?"));

        assertThrows(
                IllegalArgumentException.class,
                builder::build
        );
    }

    @Test
    void shouldBuildQuizWhenQuestionCountEqualsLimit() {
        Quiz quiz = new QuizBuilder()
                .setId(UUID.randomUUID().toString())
                .setTitle("Java Basics")
                .setDescription("Test description")
                .setCategory("Programming")
                .setDifficulty(Difficulty.EASY)
                .setTimeLimitSeconds(600)
                .setQuestionLimit(2)
                .addQuestion(createQuestion("Question one?"))
                .addQuestion(createQuestion("Question two?"))
                .build();

        assertNotNull(quiz);
        assertEquals(2, quiz.getQuestions().size());
        assertEquals(2, quiz.getQuestionLimit());
    }

    private Question createQuestion(String text) {
        return new MultipleChoiceFactory().createQuestion(
                UUID.randomUUID().toString(),
                text,
                Difficulty.EASY,
                1,
                List.of(
                        new Answer(
                                UUID.randomUUID().toString(),
                                "Correct answer",
                                true
                        ),
                        new Answer(
                                UUID.randomUUID().toString(),
                                "Wrong answer",
                                false
                        )
                )
        );
    }
}
