package com.quizmaster.factory;

import com.quizmaster.model.Answer;
import com.quizmaster.model.Difficulty;
import com.quizmaster.model.Question;

import java.util.List;

public abstract class QuestionFactory {

    public abstract Question createQuestion(
            String id,
            String text,
            Difficulty difficulty,
            int points,
            List<Answer> answers
    );
}