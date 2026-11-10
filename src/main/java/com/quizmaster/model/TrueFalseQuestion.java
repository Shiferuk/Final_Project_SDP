package com.quizmaster.model;

import java.util.List;

public class TrueFalseQuestion extends Question {

    public TrueFalseQuestion(String id, String text,
                             Difficulty difficulty, int points,
                             List<Answer> answers) {

        super(id, text, difficulty, points, answers);

        validateAnswers();
    }

    @Override
    public QuestionType getType() {
        return QuestionType.TRUE_FALSE;
    }
    @Override
    protected void validateAnswers() {
        if (getAnswers().size() != 2) {
            throw new IllegalArgumentException(
                    "True/False requires exactly two answers"
            );
        }

        long correctCount = getAnswers().stream()
                .filter(Answer::isCorrect)
                .count();

        if (correctCount != 1) {
            throw new IllegalArgumentException(
                    "True/False must have exactly one correct answer"
            );
        }

        boolean hasTrue = getAnswers().stream()
                .anyMatch(answer -> answer.getText().equalsIgnoreCase("True"));

        boolean hasFalse = getAnswers().stream()
                .anyMatch(answer -> answer.getText().equalsIgnoreCase("False"));

        if (!hasTrue || !hasFalse) {
            throw new IllegalArgumentException(
                    "Answers must be True and False"
            );
        }
    }
}