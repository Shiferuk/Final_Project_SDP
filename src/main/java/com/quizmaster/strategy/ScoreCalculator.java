package com.quizmaster.strategy;

import com.quizmaster.decorator.QuestionView;
import com.quizmaster.model.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Context для паттерна Strategy. Код прохождения теста работает только с
 * ScoreCalculator и не знает, какой алгоритм используется - его можно
 * заменить через setStrategy() без изменения остального кода.
 */
public class ScoreCalculator {
    private ScoringStrategy strategy;

    public ScoreCalculator(ScoringStrategy strategy) {
        setStrategy(strategy);
    }

    public void setStrategy(ScoringStrategy strategy) {
        if (strategy == null) throw new IllegalArgumentException("Алгоритм оценки не выбран");
        this.strategy = strategy;
    }

    public ScoringStrategy getStrategy() { return strategy; }

    /**
     * @param views   вопросы теста (возможно, обёрнутые декораторами)
     * @param answers выбранные ответы: id вопроса -> набор id ответов (нет записи = пропуск)
     */
    public ScoreReport calculate(List<? extends QuestionView> views, Map<String, Set<String>> answers) {
        int score = 0, max = 0, correct = 0;
        List<String> wrong = new ArrayList<>();
        for (QuestionView view : views) {
            Question q = view.getQuestion();
            int points = strategy.pointsFor(q);
            max += points;
            Set<String> selected = answers.getOrDefault(q.getId(), Set.of());
            if (view.isCorrect(selected)) {
                score += points + view.getBonusPoints();
                correct++;
            } else {
                wrong.add(q.getId());
            }
        }
        return new ScoreReport(score, max, correct, views.size(), wrong);
    }
}
