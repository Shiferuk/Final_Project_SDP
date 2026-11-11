package com.quizmaster.model;

import java.util.List;

public class MultipleChoiceQuestion extends Question {

    public MultipleChoiceQuestion(String id, String text,
                                  Difficulty difficulty, int points,
                                  List<Answer> answers) {

        super(id, text, difficulty, points, answers);

        validateAnswers();
    }

    public MultipleChoiceQuestion(String id, String text,
                                  Difficulty difficulty, int points,
                                  String hint, List<Answer> answers) {

        super(id, text, difficulty, points, hint, answers);

        validateAnswers();
    }

    @Override
    public QuestionType getType() {
        return QuestionType.MULTIPLE_CHOICE;
    }

    @Override
    protected void validateAnswers() {
        if (getAnswers().size() < 2) {
            throw new IllegalArgumentException(
                    "Multiple Choice requires at least two answers"
            );
        }

        long correctCount = getAnswers().stream()
                .filter(Answer::isCorrect)
                .count();

        if (correctCount != 1) {
            throw new IllegalArgumentException(
                    "Multiple Choice must have exactly one correct answer"
            );
        }
    }
}