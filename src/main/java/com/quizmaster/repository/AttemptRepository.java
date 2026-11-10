package com.quizmaster.repository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.quizmaster.model.QuizAttempt;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class AttemptRepository {
    private final File file;
    private final ObjectMapper mapper;
    public AttemptRepository(String filePath) {
        this.file = new File(filePath);
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }
    public void save(List<QuizAttempt> attempts) {
        try {
            File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(file, attempts);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to save quiz history",
                    e
            );
        }
    }
    public List<QuizAttempt> load() {
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(
                    file,
                    new TypeReference<List<QuizAttempt>>() {}
            );
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load quiz history",
                    e
            );
        }
    }
    public void add(QuizAttempt attempt) {
        List<QuizAttempt> attempts = load();
        attempts.add(attempt);
        save(attempts);
    }
}
