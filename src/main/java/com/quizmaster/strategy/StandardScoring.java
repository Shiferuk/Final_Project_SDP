package com.quizmaster.strategy;

import com.quizmaster.model.Question;

/** Standard Scoring: каждый правильный ответ = 1 балл. */
public class StandardScoring implements ScoringStrategy {
    @Override
    public String getName() { return "Standard Scoring"; }

    @Override
    public int pointsFor(Question question) { return 1; }
}
