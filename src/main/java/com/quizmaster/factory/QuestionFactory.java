package com.quizmaster.factory;

import com.quizmaster.model.Answer;
import com.quizmaster.model.Difficulty;
import com.quizmaster.model.Question;
import com.quizmaster.model.QuestionType;

import java.util.List;

public abstract class QuestionFactory {

    public abstract Question createQuestion(
            String id,
            String text,
            Difficulty difficulty,
            int points,
            String hint,
            List<Answer> answers
    );

    /** Question without a hint. */
    public Question createQuestion(
            String id,
            String text,
            Difficulty difficulty,
            int points,
            List<Answer> answers
    ) {
        return createQuestion(id, text, difficulty, points, null, answers);
    }

    /** Picks the concrete factory for a question type. */
    public static QuestionFactory forType(QuestionType type) {
        if (type == null) {
            throw new IllegalArgumentException("Question type cannot be null");
        }
        switch (type) {
            case MULTIPLE_CHOICE:
                return new MultipleChoiceFactory();
            case TRUE_FALSE:
                return new TrueFalseFactory();
            case MULTIPLE_SELECT:
                return new MultipleSelectFactory();
            default:
                throw new IllegalArgumentException("Unsupported question type.");
        }
    }
}
