
package com.quizmaster.service;

import com.quizmaster.model.Question;
import com.quizmaster.model.QuestionType;
import com.quizmaster.model.Quiz;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class SampleQuizDataTest {

    @Test
    void shouldLoadThreeSampleQuizzes() {
        QuizManagementService service = new QuizManagementService();

        SampleQuizData.load(service);

        assertEquals(3, service.getQuizCount());
    }

    @Test
    void shouldLoadAllExpectedQuizTitles() {
        QuizManagementService service = new QuizManagementService();

        SampleQuizData.load(service);

        Set<String> titles = service.getAllQuizzes()
                .stream()
                .map(Quiz::getTitle)
                .collect(Collectors.toSet());

        assertTrue(titles.contains("Programming Basics"));
        assertTrue(titles.contains("Mathematics Basics"));
        assertTrue(titles.contains("English Grammar"));
    }

    @Test
    void shouldCreateQuestionsForEverySampleQuiz() {
        QuizManagementService service = new QuizManagementService();

        SampleQuizData.load(service);

        for (Quiz quiz : service.getAllQuizzes()) {
            assertFalse(quiz.getQuestions().isEmpty());
        }
    }

    @Test
    void shouldCreateDifferentQuestionTypes() {
        QuizManagementService service = new QuizManagementService();

        SampleQuizData.load(service);

        List<QuestionType> types = service.getAllQuizzes()
                .stream()
                .flatMap(quiz -> quiz.getQuestions().stream())
                .map(Question::getType)
                .toList();

        assertTrue(types.contains(QuestionType.MULTIPLE_CHOICE));
        assertTrue(types.contains(QuestionType.TRUE_FALSE));
        assertTrue(types.contains(QuestionType.MULTIPLE_SELECT));
    }

    @Test
    void shouldNotLoadDuplicateQuizzes() {
        QuizManagementService service = new QuizManagementService();

        SampleQuizData.load(service);
        SampleQuizData.load(service);

        assertEquals(3, service.getQuizCount());
    }

    @Test
    void shouldSetValidTimeAndQuestionLimits() {
        QuizManagementService service = new QuizManagementService();

        SampleQuizData.load(service);

        for (Quiz quiz : service.getAllQuizzes()) {
            assertTrue(quiz.getTimeLimitSeconds() > 0);
            assertTrue(quiz.getQuestionLimit() > 0);
            assertTrue(
                    quiz.getQuestions().size() <= quiz.getQuestionLimit()
            );
        }
    }
}
