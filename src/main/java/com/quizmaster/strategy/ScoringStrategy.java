package com.quizmaster.strategy;

import com.quizmaster.model.Question;

/** Strategy: алгоритм подсчёта баллов. */
public interface ScoringStrategy {
    /** Название алгоритма для интерфейса. */
    String getName();

    /** Баллы за вопрос, если на него ответили верно. */
    int pointsFor(Question question);
}
