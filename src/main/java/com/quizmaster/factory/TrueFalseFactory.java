
package com.quizmaster.factory;

import com.quizmaster.model.Answer;
import com.quizmaster.model.Difficulty;
import com.quizmaster.model.Question;
import com.quizmaster.model.TrueFalseQuestion;

import java.util.List;

public class TrueFalseFactory extends QuestionFactory {

    @Override
    public Question createQuestion(
            String id,
            String text,
            Difficulty difficulty,
            int points,
            List<Answer> answers
    ) {
        return new TrueFalseQuestion(
                id, text, difficulty, points, answers
        );
    }
}
