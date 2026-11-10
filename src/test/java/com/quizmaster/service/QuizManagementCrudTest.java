
package com.quizmaster.service;

import com.quizmaster.builder.QuizBuilder;
import com.quizmaster.factory.MultipleChoiceFactory;
import com.quizmaster.model.Answer;
import com.quizmaster.model.Difficulty;
import com.quizmaster.model.Question;
import com.quizmaster.model.Quiz;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class QuizManagementCrudTest {

    private QuizManagementService service;

    @BeforeEach
    void setUp() {
        service = new QuizManagementService();
    }

    @Test
    void shouldCreateAndReadQuiz() {
        Quiz quiz = createQuiz("Java Basics");
        service.createQuiz(quiz);

        Quiz savedQuiz = service.getQuiz(quiz.getId());

        assertNotNull(savedQuiz);
        assertEquals("Java Basics", savedQuiz.getTitle());
        assertEquals(1, service.getQuizCount());
    }

    @Test
    void shouldReturnAllQuizzes() {
        service.createQuiz(createQuiz("Java Basics"));
        service.createQuiz(createQuiz("Python Basics"));

        assertEquals(2, service.getAllQuizzes().size());
    }

    @Test
    void shouldUpdateQuiz() {
        Quiz originalQuiz = createQuiz("Java Basics");
        service.createQuiz(originalQuiz);

        Quiz updatedQuiz = new QuizBuilder()
                .setId(originalQuiz.getId())
                .setTitle("Advanced Java")
                .setDescription("Advanced Java questions")
                .setCategory("Programming")
                .setDifficulty(Difficulty.HARD)
                .setTimeLimitSeconds(900)
                .setQuestionLimit(10)
                .addQuestion(createQuestion("What is inheritance?"))
                .build();

        service.updateQuiz(originalQuiz.getId(), updatedQuiz);

        Quiz result = service.getQuiz(originalQuiz.getId());

        assertEquals("Advanced Java", result.getTitle());
        assertEquals(Difficulty.HARD, result.getDifficulty());
    }

    @Test
    void shouldDeleteQuiz() {
        Quiz quiz = createQuiz("Java Basics");
        service.createQuiz(quiz);

        service.deleteQuiz(quiz.getId());

        assertEquals(0, service.getQuizCount());
        assertTrue(service.getAllQuizzes().isEmpty());
    }

    @Test
    void shouldAddQuestionToQuiz() {
        Quiz quiz = createQuiz("Java Basics");
        service.createQuiz(quiz);

        Question newQuestion =
                createQuestion("What is a constructor?");

        service.addQuestion(quiz.getId(), newQuestion);

        Quiz result = service.getQuiz(quiz.getId());

        assertEquals(2, result.getQuestions().size());
        assertTrue(result.getQuestions().stream()
                .anyMatch(question ->
                        question.getId().equals(newQuestion.getId())));
    }

    @Test
    void shouldRemoveQuestionFromQuiz() {
        Quiz quiz = createQuiz("Java Basics");
        service.createQuiz(quiz);

        String questionId =
                quiz.getQuestions().get(0).getId();

        service.removeQuestion(quiz.getId(), questionId);

        assertTrue(service.getQuiz(quiz.getId())
                .getQuestions().isEmpty());
    }

    @Test
    void shouldUpdateQuestion() {
        Quiz quiz = createQuiz("Java Basics");
        service.createQuiz(quiz);

        Question originalQuestion =
                quiz.getQuestions().get(0);

        Question updatedQuestion =
                new MultipleChoiceFactory().createQuestion(
                        originalQuestion.getId(),
                        "What is polymorphism?",
                        Difficulty.MEDIUM,
                        2,
                        List.of(
                                new Answer(
                                        UUID.randomUUID().toString(),
                                        "Inheritance",
                                        false
                                ),
                                new Answer(
                                        UUID.randomUUID().toString(),
                                        "Many forms",
                                        true
                                ),
                                new Answer(
                                        UUID.randomUUID().toString(),
                                        "Compilation",
                                        false
                                )
                        )
                );

        service.updateQuestion(
                quiz.getId(),
                originalQuestion.getId(),
                updatedQuestion
        );

        Question result = service.getQuiz(quiz.getId())
                .getQuestions()
                .get(0);

        assertEquals(
                "What is polymorphism?",
                result.getText()
        );

        assertEquals(2, result.getPoints());
    }

    @Test
    void shouldFindQuizzesByCategory() {
        service.createQuiz(createQuiz("Java Basics"));
        service.createQuiz(createQuiz("Python Basics"));

        List<Quiz> results =
                service.findByCategory("Programming");

        assertEquals(2, results.size());
    }

    @Test
    void shouldSearchQuizzesByTitle() {
        service.createQuiz(createQuiz("Java Basics"));
        service.createQuiz(createQuiz("Mathematics"));

        List<Quiz> results =
                service.searchByTitle("Java");

        assertEquals(1, results.size());
        assertEquals("Java Basics", results.get(0).getTitle());
    }

    private Quiz createQuiz(String title) {
        return new QuizBuilder()
                .setId(UUID.randomUUID().toString())
                .setTitle(title)
                .setDescription("Test quiz")
                .setCategory("Programming")
                .setDifficulty(Difficulty.EASY)
                .setTimeLimitSeconds(600)
                .setQuestionLimit(10)
                .addQuestion(createQuestion("What is Java?"))
                .build();
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
                                "Programming language",
                                true
                        ),
                        new Answer(
                                UUID.randomUUID().toString(),
                                "Operating system",
                                false
                        ),
                        new Answer(
                                UUID.randomUUID().toString(),
                                "Database",
                                false
                        )
                )
        );
    }
}
