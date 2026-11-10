package com.quizmaster.strategy;

import com.quizmaster.model.Question;

/** Difficulty-Based Scoring: Easy = 1, Medium = 2, Hard = 3. */
public class DifficultyBasedScoring implements ScoringStrategy {
    @Override
    public String getName() { return "Difficulty-Based Scoring"; }

    @Override
    public int pointsFor(Question question) {
        switch (question.getDifficulty()) {
            case EASY:   return 1;
            case MEDIUM: return 2;
            case HARD:   return 3;
            default: throw new IllegalStateException("Unknown difficulty");
        }
    }
}
