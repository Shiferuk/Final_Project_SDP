package com.quizmaster.facade;

import com.quizmaster.model.QuizAttempt;
import com.quizmaster.model.QuizResult;
import com.quizmaster.repository.AttemptRepository;
import com.quizmaster.service.QuizManagementService;
import com.quizmaster.service.SampleQuizData;
import com.quizmaster.strategy.StandardScoring;
import com.quizmaster.strategy.TimeBonusScoring;
import com.quizmaster.model.Question;
import com.quizmaster.model.Quiz;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HistoryStatisticsTest {

    @TempDir
    Path tempDir;

    private QuizAttempt attempt(String quizId, int total, int correct, int score) {
        QuizAttempt attempt = new QuizAttempt("id", "u", quizId);
        attempt.finish(new QuizResult(total, correct, score));
        return attempt;
    }

    @Test
    void emptyHistoryGivesZeros() {
        HistoryStatistics stats = HistoryStatistics.from(new ArrayList<>());
        assertEquals(0, stats.getTotalAttempts());
        assertEquals(0.0, stats.getAveragePercent(), 0.001);
        assertEquals(0.0, stats.getBestPercent(), 0.001);
    }

    @Test
    void calculatesBestAverageAndPerQuiz() {
        HistoryStatistics stats = HistoryStatistics.from(List.of(
                attempt("q1", 4, 2, 2),     // 50%
                attempt("q1", 4, 4, 4),     // 100%
                attempt("q2", 5, 1, 1)));   // 20%

        assertEquals(3, stats.getTotalAttempts());
        assertEquals(100.0, stats.getBestPercent(), 0.001);
        assertEquals((50 + 100 + 20) / 3.0, stats.getAveragePercent(), 0.001);
        assertEquals(7, stats.getTotalCorrect());
        assertEquals(6, stats.getTotalWrong());
        assertEquals(2, stats.getPerQuiz().get("q1").getAttempts());
        assertEquals(75.0, stats.getPerQuiz().get("q1").getAveragePercent(), 0.001);
    }

    @Test
    void facadeUsesTimeBonusStrategyAndReturnsStatistics() {
        QuizFacade facade = new QuizFacade(new AttemptRepository(tempDir.resolve("s.json").toString()));
        try {
            QuizManagementService service = new QuizManagementService();
            SampleQuizData.load(service);
            Quiz quiz = service.getAllQuizzes().get(0);

            facade.startQuiz("u", quiz, new TimeBonusScoring());
            Question first = quiz.getQuestions().get(0);
            facade.questionShown(first.getId());
            Set<String> correct = new HashSet<>();
            first.getAnswers().forEach(a -> { if (a.isCorrect()) correct.add(a.getId()); });
            facade.selectAnswer(first.getId(), correct);   // answered immediately -> +2 bonus

            assertEquals(3, facade.finishQuiz().getScore());
            assertEquals(1, facade.getStatistics().getTotalAttempts());
        } finally {
            facade.close();
        }
    }
}
