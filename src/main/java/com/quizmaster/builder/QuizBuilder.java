package com.quizmaster.builder;

import com.quizmaster.model.Difficulty;
import com.quizmaster.model.Question;
import com.quizmaster.model.Quiz;

import java.util.ArrayList;
import java.util.List;

public class QuizBuilder {

    private String id;
    private String title;
    private String description = "";
    private String category;
    private Difficulty difficulty;
    private int timeLimitSeconds;
    private int questionLimit;
    private final List<Question> questions = new ArrayList<>();

    public QuizBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public QuizBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public QuizBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public QuizBuilder setCategory(String category) {
        this.category = category;
        return this;
    }

    public QuizBuilder setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
        return this;
    }

    public QuizBuilder setTimeLimitSeconds(int timeLimitSeconds) {
        this.timeLimitSeconds = timeLimitSeconds;
        return this;
    }

    public QuizBuilder setQuestionLimit(int questionLimit) {
        this.questionLimit = questionLimit;
        return this;
    }

    public QuizBuilder addQuestion(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("Question cannot be null");
        }

        questions.add(question);
        return this;
    }

    public QuizBuilder addQuestions(List<Question> questions) {
        if (questions == null) {
            throw new IllegalArgumentException("Questions cannot be null");
        }

        for (Question question : questions) {
            addQuestion(question);
        }

        return this;
    }

    public Quiz build() {
        if (id == null || id.isBlank()) {
            throw new IllegalStateException("Quiz ID is required");
        }

        if (title == null || title.isBlank()) {
            throw new IllegalStateException("Quiz title is required");
        }

        if (category == null || category.isBlank()) {
            throw new IllegalStateException("Quiz category is required");
        }

        if (difficulty == null) {
            throw new IllegalStateException("Quiz difficulty is required");
        }

        if (timeLimitSeconds <= 0) {
            throw new IllegalStateException("Time limit must be greater than zero");
        }

        if (questionLimit <= 0) {
            throw new IllegalStateException("Question limit must be greater than zero");
        }

        if (questions.isEmpty()) {
            throw new IllegalStateException("Quiz must contain at least one question");
        }

        if (questions.size() > questionLimit) {
            throw new IllegalStateException(
                    "Number of questions exceeds the question limit"
            );
        }

        Quiz quiz = new Quiz(
                id,
                title,
                description,
                category,
                difficulty,
                timeLimitSeconds,
                questionLimit
        );

        for (Question question : questions) {
            quiz.addQuestion(question);
        }

        return quiz;
    }
}