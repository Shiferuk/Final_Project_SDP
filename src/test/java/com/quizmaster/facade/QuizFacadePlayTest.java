package com.quizmaster.facade;

import com.quizmaster.model.Question;
import com.quizmaster.model.Quiz;
import com.quizmaster.model.QuizResult;
import com.quizmaster.repository.AttemptRepository;
import com.quizmaster.service.QuizManagementService;
import com.quizmaster.service.SampleQuizData;
import com.quizmaster.strategy.DifficultyBasedScoring;
import com.quizmaster.strategy.StandardScoring;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/** The Facade really uses Strategy, Decorator and Observer (timer). */
class QuizFacadePlayTest {

    @TempDir
    Path tempDir;

    private Quiz sampleQuiz() {
        QuizManagementService service = new QuizManagementService();
        SampleQuizData.load(service);
        return service.getAllQuizzes().get(0);
    }

    private Set<String> correctIds(Question question) {
        Set<String> ids = new java.util.HashSet<>();
        question.getAnswers().forEach(a -> { if (a.isCorrect()) ids.add(a.getId()); });
        return ids;
    }

    @Test
    void playWithStrategyAndDecorators() {
        QuizFacade facade = new QuizFacade(new AttemptRepository(tempDir.resolve("a.json").toString()));
        try {
            Quiz quiz = sampleQuiz();
            facade.startQuiz("u1", quiz, new StandardScoring());

            Question first = quiz.getQuestions().get(0);
            assertTrue(facade.useHint(first.getId()).isPresent());
            facade.useBonus(first.getId());
            facade.selectAnswer(first.getId(), correctIds(first));

            QuizResult result = facade.finishQuiz();
            assertEquals(1, result.getCorrectAnswers());
            assertEquals(2, result.getScore());                  // 1 point + bonus 1
            assertEquals(quiz.getQuestions().size(), result.getTotalQuestions());
            assertEquals(1, facade.getHistory().size());
        } finally {
            facade.close();
        }
    }

    @Test
    void switchingStrategyChangesScore() {
        QuizFacade facade = new QuizFacade(new AttemptRepository(tempDir.resolve("b.json").toString()));
        try {
            Quiz quiz = sampleQuiz();
            facade.startQuiz("u1", quiz, new StandardScoring());
            for (Question q : quiz.getQuestions()) {
                facade.selectAnswer(q.getId(), correctIds(q));
            }
            facade.changeStrategy(new DifficultyBasedScoring());
            int expected = 0;
            for (Question q : quiz.getQuestions()) {
                expected += new DifficultyBasedScoring().pointsFor(q);
            }
            assertEquals(expected, facade.finishQuiz().getScore());
        } finally {
            facade.close();
        }
    }

    @Test
    void historyIsLoadedAfterRestartAndCancelSavesNothing() {
        String file = tempDir.resolve("c.json").toString();
        QuizFacade facade = new QuizFacade(new AttemptRepository(file));
        try {
            facade.startQuiz("u1", sampleQuiz(), new StandardScoring());
            facade.finishQuiz();
            facade.startQuiz("u1", sampleQuiz(), new StandardScoring());
            facade.cancelQuiz();
            assertEquals(1, facade.getHistory().size());
        } finally {
            facade.close();
        }
        QuizFacade restarted = new QuizFacade(new AttemptRepository(file));
        assertEquals(1, restarted.getHistory().size());
        restarted.close();
    }

    @Test
    void rejectsMissingStrategyAndQuiz() {
        QuizFacade facade = new QuizFacade(new AttemptRepository(tempDir.resolve("d.json").toString()));
        try {
            assertThrows(IllegalArgumentException.class, () -> facade.startQuiz("u", sampleQuiz(), null));
            assertThrows(IllegalArgumentException.class, () -> facade.startQuiz("u", null, new StandardScoring()));
        } finally {
            facade.close();
        }
    }
}
