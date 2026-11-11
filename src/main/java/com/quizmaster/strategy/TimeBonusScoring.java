package com.quizmaster.strategy;

import com.quizmaster.model.Question;

import java.util.HashMap;
import java.util.Map;

/**
 * Time Bonus Scoring: чем быстрее верный ответ, тем больше баллов.
 * 1 балл за верный ответ + бонус: до 10 секунд +2, до 20 секунд +1, дольше +0.
 * Максимум за вопрос всегда 3 балла.
 */
public class TimeBonusScoring implements TimeAwareScoring {

    public static final int BASE_POINTS = 1;
    public static final int FAST_SECONDS = 10;
    public static final int MEDIUM_SECONDS = 20;
    public static final int MAX_POINTS = BASE_POINTS + 2;

    private final Map<String, Long> answerTimes = new HashMap<>();

    @Override
    public String getName() { return "Time Bonus Scoring"; }

    @Override
    public void setAnswerTimes(Map<String, Long> secondsByQuestionId) {
        answerTimes.clear();
        if (secondsByQuestionId != null) {
            answerTimes.putAll(secondsByQuestionId);
        }
    }

    @Override
    public int pointsFor(Question question) {
        Long seconds = answerTimes.get(question.getId());
        if (seconds == null) {
            return BASE_POINTS;          // время неизвестно - без бонуса
        }
        if (seconds <= FAST_SECONDS) {
            return BASE_POINTS + 2;
        }
        if (seconds <= MEDIUM_SECONDS) {
            return BASE_POINTS + 1;
        }
        return BASE_POINTS;
    }

    @Override
    public int maxPointsFor(Question question) { return MAX_POINTS; }
}
