package com.quizmaster.strategy;

import java.util.Map;

/** Стратегия, которой нужно время ответа на каждый вопрос (передаёт QuizFacade). */
public interface TimeAwareScoring extends ScoringStrategy {
    /** id вопроса -> секунды, потраченные на ответ. */
    void setAnswerTimes(Map<String, Long> secondsByQuestionId);
}
