package com.quizmaster.strategy;

import com.quizmaster.decorator.BasicQuestionView;
import com.quizmaster.decorator.QuestionView;
import com.quizmaster.factory.MultipleChoiceFactory;
import com.quizmaster.model.Answer;
import com.quizmaster.model.Difficulty;
import com.quizmaster.model.Question;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TimeBonusScoringTest {

    private Question question(String id) {
        return new MultipleChoiceFactory().createQuestion(id, "Q" + id, Difficulty.EASY, 1,
                List.of(new Answer("a", "A", true), new Answer("b", "B", false)));
    }

    @Test
    void fasterAnswerGivesMorePoints() {
        TimeBonusScoring scoring = new TimeBonusScoring();
        scoring.setAnswerTimes(Map.of("fast", 3L, "medium", 15L, "slow", 60L));

        assertEquals(3, scoring.pointsFor(question("fast")));
        assertEquals(2, scoring.pointsFor(question("medium")));
        assertEquals(1, scoring.pointsFor(question("slow")));
        assertEquals(1, scoring.pointsFor(question("unknown")));
        assertEquals(3, scoring.maxPointsFor(question("slow")));
    }

    @Test
    void worksInsideScoreCalculatorWithFixedMaximum() {
        TimeBonusScoring scoring = new TimeBonusScoring();
        scoring.setAnswerTimes(Map.of("1", 5L, "2", 50L));
        List<QuestionView> views = List.of(
                new BasicQuestionView(question("1")), new BasicQuestionView(question("2")));

        ScoreReport report = new ScoreCalculator(scoring)
                .calculate(views, Map.of("1", Set.of("a"), "2", Set.of("a")));

        assertEquals(4, report.getScore());      // 3 + 1
        assertEquals(6, report.getMaxScore());   // 2 questions * 3
    }
}
