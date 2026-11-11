package com.quizmaster.factory;

import com.quizmaster.model.Answer;
import com.quizmaster.model.Difficulty;
import com.quizmaster.model.MultipleChoiceQuestion;
import com.quizmaster.model.Question;

import java.util.List;

public class MultipleChoiceFactory extends QuestionFactory {

    @Override
    public Question createQuestion(
            String id,
            String text,
            Difficulty difficulty,
            int points,
            List<Answer> answers
    ) {
        return new MultipleChoiceQuestion(
                id, text, difficulty, points, answers
        );
    }
}