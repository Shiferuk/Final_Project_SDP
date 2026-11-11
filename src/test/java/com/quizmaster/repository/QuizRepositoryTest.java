package com.quizmaster.repository;

import com.quizmaster.model.Quiz;
import com.quizmaster.service.QuizManagementService;
import com.quizmaster.service.SampleQuizData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuizRepositoryTest {

    @TempDir
    Path tempDir;

    @Test
    void quizzesSurviveRestart() {
        String file = tempDir.resolve("quizzes.json").toString();

        QuizManagementService first = new QuizManagementService(new QuizRepository(file));
        SampleQuizData.load(first);
        int count = first.getQuizCount();
        assertTrue(count > 0);

        QuizManagementService second = new QuizManagementService(new QuizRepository(file));
        assertEquals(count, second.getQuizCount());

        List<Quiz> loaded = second.getAllQuizzes();
        Quiz original = first.getAllQuizzes().get(0);
        Quiz copy = loaded.get(0);
        assertEquals(original.getTitle(), copy.getTitle());
        assertEquals(original.getQuestions().size(), copy.getQuestions().size());
        assertEquals(original.getQuestions().get(0).getType(), copy.getQuestions().get(0).getType());
        assertEquals(original.getQuestions().get(0).getHint(), copy.getQuestions().get(0).getHint());
    }

    @Test
    void deleteIsPersisted() {
        String file = tempDir.resolve("quizzes.json").toString();
        QuizManagementService service = new QuizManagementService(new QuizRepository(file));
        SampleQuizData.load(service);
        String id = service.getAllQuizzes().get(0).getId();
        service.deleteQuiz(id);

        assertEquals(service.getQuizCount(),
                new QuizManagementService(new QuizRepository(file)).getQuizCount());
    }
}
