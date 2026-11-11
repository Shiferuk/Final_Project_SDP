package com.quizmaster.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Question {

    private final String id;
    private String text;
    private Difficulty difficulty;
    private int points;
    private final List<Answer> answers;

    public Question(
            String id,
            String text,
            Difficulty difficulty,
            int points,
            List<Answer> answers
    ) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Question ID cannot be empty");
        }

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Question text cannot be empty");
        }

        if (difficulty == null) {
            throw new IllegalArgumentException("Difficulty cannot be null");
        }

        if (points <= 0) {
            throw new IllegalArgumentException("Points must be greater than zero");
        }

        if (answers == null) {
            throw new IllegalArgumentException("Answers cannot be null");
        }

        this.id = id;
        this.text = text;
        this.difficulty = difficulty;
        this.points = points;
        this.answers = new ArrayList<>(answers);

        validateAnswerIds();
    }

    public abstract QuestionType getType();

    protected abstract void validateAnswers();

    private void validateAnswerIds() {
        Set<String> ids = new HashSet<>();

        for (Answer answer : answers) {
            if (answer == null) {
                throw new IllegalArgumentException("Answer cannot be null");
            }

            if (answer.getId() == null || answer.getId().isBlank()) {
                throw new IllegalArgumentException("Answer ID cannot be empty");
            }

            if (!ids.add(answer.getId())) {
                throw new IllegalArgumentException("Duplicate answer ID: " + answer.getId());
            }
        }
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public int getPoints() {
        return points;
    }

    public List<Answer> getAnswers() {
        return Collections.unmodifiableList(answers);
    }

    public void setText(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Question text cannot be empty");
        }

        this.text = text;
    }

    public void setDifficulty(Difficulty difficulty) {
        if (difficulty == null) {
            throw new IllegalArgumentException("Difficulty cannot be null");
        }

        this.difficulty = difficulty;
    }

    public void setPoints(int points) {
        if (points <= 0) {
            throw new IllegalArgumentException("Points must be greater than zero");
        }

        this.points = points;
    }

    public void addAnswer(Answer answer) {
        if (answer == null) {
            throw new IllegalArgumentException("Answer cannot be null");
        }

        for (Answer existing : answers) {
            if (existing.getId().equals(answer.getId())) {
                throw new IllegalArgumentException("Answer ID already exists");
            }
        }

        answers.add(answer);

        try {
            validateAnswers();
        } catch (IllegalArgumentException exception) {
            answers.remove(answers.size() - 1);
            throw exception;
        }
    }

    public void removeAnswer(String answerId) {
        for (int i = 0; i < answers.size(); i++) {
            if (answers.get(i).getId().equals(answerId)) {
                Answer removed = answers.remove(i);

                try {
                    validateAnswers();
                } catch (IllegalArgumentException exception) {
                    answers.add(i, removed);
                    throw exception;
                }

                return;
            }
        }

        throw new IllegalArgumentException("Answer not found: " + answerId);
    }
}