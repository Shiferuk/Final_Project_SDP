
package com.quizmaster.factory;

import com.quizmaster.model.Answer;
import com.quizmaster.model.Difficulty;
import com.quizmaster.model.Question;
import com.quizmaster.model.QuestionType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestionFactoryTest {

    @Test
    void shouldCreateMultipleChoiceQuestion() {

        QuestionFactory factory = new MultipleChoiceFactory();

        List<Answer> answers = List.of(
                new Answer("a1", "Java", true),
                new Answer("a2", "Python", false)
        );

        Question question = factory.createQuestion(
                "q1",
                "Which language is used with JVM?",
                Difficulty.EASY,
                1,
                answers
        );

        assertNotNull(question);
        assertEquals(QuestionType.MULTIPLE_CHOICE, question.getType());
        assertEquals("q1", question.getId());
    }

    @Test
    void shouldCreateTrueFalseQuestion() {

        QuestionFactory factory = new TrueFalseFactory();

        List<Answer> answers = List.of(
                new Answer("a1", "True", true),
                new Answer("a2", "False", false)
        );

        Question question = factory.createQuestion(
                "q2",
                "Java is an object-oriented language.",
                Difficulty.EASY,
                1,
                answers
        );

        assertNotNull(question);
        assertEquals(QuestionType.TRUE_FALSE, question.getType());
    }

    @Test
    void shouldRejectInvalidMultipleChoiceQuestion() {

        QuestionFactory factory = new MultipleChoiceFactory();

        List<Answer> answers = List.of(
                new Answer("a1", "Java", true),
                new Answer("a2", "Python", true)
        );

        assertThrows(IllegalArgumentException.class, () ->
                factory.createQuestion(
                        "q3",
                        "Choose a language.",
                        Difficulty.EASY,
                        1,
                        answers
                )
        );
    }
}
