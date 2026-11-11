package com.quizmaster.factory;

import com.quizmaster.model.Answer;
import com.quizmaster.model.Difficulty;
import com.quizmaster.model.MultipleSelectQuestion;
import com.quizmaster.model.Question;

import java.util.List;

public class MultipleSelectFactory extends QuestionFactory {

    @Override
    public Question createQuestion(
            String id,
            String text,
            Difficulty difficulty,
            int points,
            List<Answer> answers
    ) {
        return new MultipleSelectQuestion(
                id, text, difficulty, points, answers
        );
    }
}