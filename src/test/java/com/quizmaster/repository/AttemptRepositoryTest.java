package com.quizmaster.repository;
import com.quizmaster.model.QuizAttempt;
import com.quizmaster.model.QuizResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
class AttemptRepositoryTest {
    @TempDir
    Path tempDir;
    @Test
    void shouldSaveAndLoadQuizAttempts() {
        String filePath = tempDir.resolve("attempts.json").toString();
        AttemptRepository repository = new AttemptRepository(filePath);
        QuizAttempt attempt = new QuizAttempt(
                "attempt1",
                "user1",
                "quiz1"
        );
        attempt.finish(new QuizResult(5, 3, 30));
        repository.save(List.of(attempt));
        List<QuizAttempt> loadedAttempts = repository.load();
        assertEquals(1, loadedAttempts.size());
        QuizAttempt loaded = loadedAttempts.get(0);
        assertEquals("attempt1", loaded.getAttemptId());
        assertEquals("user1", loaded.getUserId());
        assertEquals("quiz1", loaded.getQuizId());
        assertTrue(loaded.isCompleted());
        assertEquals(5, loaded.getResult().getTotalQuestions());
        assertEquals(3, loaded.getResult().getCorrectAnswers());
        assertEquals(30, loaded.getResult().getScore());
        assertEquals(60.0, loaded.getResult().getPercentage());
    }
}