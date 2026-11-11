package com.quizmaster.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Quiz {

    private final String id;
    private String title;
    private String description;
    private String category;
    private Difficulty difficulty;
    private int timeLimitSeconds;
    private int questionLimit;

    private final List<Question> questions;

    public Quiz(String id, String title, String description,
                String category, Difficulty difficulty,
                int timeLimitSeconds, int questionLimit) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Quiz ID cannot be empty");
        }

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Quiz title cannot be empty");
        }

        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category cannot be empty");
        }

        if (difficulty == null) {
            throw new IllegalArgumentException("Difficulty cannot be null");
        }

        if (timeLimitSeconds <= 0) {
            throw new IllegalArgumentException("Time must be greater than zero");
        }

        if (questionLimit <= 0) {
            throw new IllegalArgumentException("Question limit must be greater than zero");
        }

        this.id = id;
        this.title = title;
        this.description = description == null ? "" : description;
        this.category = category;
        this.difficulty = difficulty;
        this.timeLimitSeconds = timeLimitSeconds;
        this.questionLimit = questionLimit;
        this.questions = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public int getTimeLimitSeconds() {
        return timeLimitSeconds;
    }

    public int getQuestionLimit() {
        return questionLimit;
    }

    public List<Question> getQuestions() {
        return Collections.unmodifiableList(questions);
    }

    public void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Quiz title cannot be empty");
        }

        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description;
    }

    public void setCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category cannot be empty");
        }

        this.category = category;
    }

    public void setDifficulty(Difficulty difficulty) {
        if (difficulty == null) {
            throw new IllegalArgumentException("Difficulty cannot be null");
        }

        this.difficulty = difficulty;
    }

    public void setTimeLimitSeconds(int timeLimitSeconds) {
        if (timeLimitSeconds <= 0) {
            throw new IllegalArgumentException("Time must be greater than zero");
        }

        this.timeLimitSeconds = timeLimitSeconds;
    }

    public void setQuestionLimit(int questionLimit) {
        if (questionLimit <= 0 || questionLimit < questions.size()) {
            throw new IllegalArgumentException(
                    "Question limit cannot be less than the current number of questions"
            );
        }

        this.questionLimit = questionLimit;
    }

    public void addQuestion(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("Question cannot be null");
        }

        if (questions.size() >= questionLimit) {
            throw new IllegalStateException("Quiz has reached its question limit");
        }

        for (Question existing : questions) {
            if (existing.getId().equals(question.getId())) {
                throw new IllegalArgumentException("Duplicate question ID");
            }
        }

        questions.add(question);
    }

    public void removeQuestion(String questionId) {
        boolean removed = questions.removeIf(
                question -> question.getId().equals(questionId)
        );

        if (!removed) {
            throw new IllegalArgumentException("Question not found");
        }
    }

    public void replaceQuestion(String questionId, Question newQuestion) {
        if (newQuestion == null) {
            throw new IllegalArgumentException("Question cannot be null");
        }

        int index = -1;

        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getId().equals(questionId)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            throw new IllegalArgumentException("Question not found");
        }

        for (int i = 0; i < questions.size(); i++) {
            if (i != index &&
                    questions.get(i).getId().equals(newQuestion.getId())) {
                throw new IllegalArgumentException("Duplicate question ID");
            }
        }

        questions.set(index, newQuestion);
    }
}