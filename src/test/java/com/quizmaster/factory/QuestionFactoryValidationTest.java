package com.quizmaster.factory;

import com.quizmaster.model.Answer;
import com.quizmaster.model.Difficulty;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class QuestionFactoryValidationTest {

    @Test
    void multipleChoiceShouldRejectMoreThanOneCorrectAnswer() {
        MultipleChoiceFactory factory = new MultipleChoiceFactory();

        List<Answer> answers = List.of(
                answer("Java", true),
                answer("Python", true),
                answer("C++", false)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> factory.createQuestion(
                        UUID.randomUUID().toString(),
                        "Which language is used?",
                        Difficulty.EASY,
                        1,
                        answers
                )
        );
    }

    @Test
    void trueFalseShouldRejectMoreThanTwoAnswers() {
        TrueFalseFactory factory = new TrueFalseFactory();

        List<Answer> answers = List.of(
                answer("True", true),
                answer("False", false),
                answer("Maybe", false)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> factory.createQuestion(
                        UUID.randomUUID().toString(),
                        "Java is a programming language.",
                        Difficulty.EASY,
                        1,
                        answers
                )
        );
    }

    @Test
    void multipleSelectShouldRejectNoCorrectAnswers() {
        MultipleSelectFactory factory = new MultipleSelectFactory();

        List<Answer> answers = List.of(
                answer("Java", false),
                answer("Python", false),
                answer("C++", false)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> factory.createQuestion(
                        UUID.randomUUID().toString(),
                        "Select programming languages.",
                        Difficulty.MEDIUM,
                        2,
                        answers
                )
        );
    }

    private Answer answer(String text, boolean correct) {
        return new Answer(
                UUID.randomUUID().toString(),
                text,
                correct
        );
    }
}