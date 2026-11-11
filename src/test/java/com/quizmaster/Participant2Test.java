package com.quizmaster;

import com.quizmaster.decorator.*;
import com.quizmaster.model.*;
import com.quizmaster.strategy.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class Participant2Test {

    /** Простая реализация вопроса для тестов. */
    static class TestQuestion extends Question {
        TestQuestion(String id, Difficulty d, String hint, List<Answer> answers) {
            super(id, "Вопрос " + id, d, 1, hint, answers);
        }

        @Override
        public QuestionType getType() { return QuestionType.MULTIPLE_CHOICE; }

        @Override
        protected void validateAnswers() { }
    }

    static Question q(String id, Difficulty d, String hint) {
        return new TestQuestion(id, d, hint, List.of(
                new Answer("a", "A", true), new Answer("b", "B", false),
                new Answer("c", "C", false), new Answer("d", "D", false)));
    }

    List<QuestionView> quiz() {
        return List.of(new BasicQuestionView(q("1", Difficulty.EASY, null)),
                new BasicQuestionView(q("2", Difficulty.MEDIUM, null)),
                new BasicQuestionView(q("3", Difficulty.HARD, null)));
    }

    @Test
    void standardScoring_oneScorePerCorrectAnswer() {
        Map<String, Set<String>> ans = Map.of("1", Set.of("a"), "2", Set.of("a"), "3", Set.of("b"));
        ScoreReport r = new ScoreCalculator(new StandardScoring()).calculate(quiz(), ans);
        assertEquals(2, r.getScore());
        assertEquals(3, r.getMaxScore());
        assertEquals(List.of("3"), r.getWrongQuestionIds());
    }

    @Test
    void difficultyScoring_hardQuestionsWorthMore() {
        Map<String, Set<String>> ans = Map.of("1", Set.of("a"), "2", Set.of("a"), "3", Set.of("b"));
        ScoreReport r = new ScoreCalculator(new DifficultyBasedScoring()).calculate(quiz(), ans);
        assertEquals(3, r.getScore());      // 1 + 2
        assertEquals(6, r.getMaxScore());   // 1 + 2 + 3
        assertEquals(50.0, r.getPercent(), 0.001);
    }

    @Test
    void strategyCanBeSwappedWithoutChangingCalculator() {
        Map<String, Set<String>> ans = Map.of("1", Set.of("a"), "2", Set.of("a"), "3", Set.of("a"));
        ScoreCalculator calc = new ScoreCalculator(new StandardScoring());
        assertEquals(3, calc.calculate(quiz(), ans).getScore());
        calc.setStrategy(new DifficultyBasedScoring());
        assertEquals(6, calc.calculate(quiz(), ans).getScore());
        assertThrows(IllegalArgumentException.class, () -> calc.setStrategy(null));
    }

    @Test
    void skippedQuestionCountsAsWrong() {
        ScoreReport r = new ScoreCalculator(new StandardScoring()).calculate(quiz(), Map.of());
        assertEquals(0, r.getScore());
        assertEquals(3, r.getWrongQuestionIds().size());
    }

    @Test
    void hintDecorator_showsHint() {
        QuestionView basic = new BasicQuestionView(q("1", Difficulty.EASY, "Подумай о столице"));
        assertTrue(basic.getHint().isEmpty());
        assertEquals("Подумай о столице", new HintDecorator(basic).getHint().orElseThrow());
        QuestionView noHint = new HintDecorator(new BasicQuestionView(q("2", Difficulty.EASY, null)));
        assertTrue(noHint.getHint().isPresent());
    }

    @Test
    void fiftyFifty_removesTwoWrongAndKeepsCorrect() {
        QuestionView v = new FiftyFiftyDecorator(new BasicQuestionView(q("1", Difficulty.EASY, null)), new Random(1));
        assertEquals(2, v.getVisibleAnswers().size());
        assertTrue(v.getVisibleAnswers().stream().anyMatch(Answer::isCorrect));
        assertEquals(v.getVisibleAnswers(), v.getVisibleAnswers()); // стабильно
    }

    @Test
    void fiftyFifty_trueFalseKeepsBothOptions() {
        Question tf = new TestQuestion("t", Difficulty.EASY, null,
                List.of(new Answer("true", "Да", true), new Answer("false", "Нет", false)));
        assertEquals(2, new FiftyFiftyDecorator(new BasicQuestionView(tf)).getVisibleAnswers().size());
    }

    @Test
    void bonusDecorator_addsPointsOnlyForCorrectAnswer() {
        List<QuestionView> views = List.of(
                new BonusDecorator(new BasicQuestionView(q("1", Difficulty.EASY, null)), 2),
                new BonusDecorator(new BasicQuestionView(q("2", Difficulty.EASY, null)), 2));
        Map<String, Set<String>> ans = Map.of("1", Set.of("a"), "2", Set.of("b"));
        ScoreReport r = new ScoreCalculator(new StandardScoring()).calculate(views, ans);
        assertEquals(3, r.getScore());   // 1 + бонус 2; второй вопрос неверный
        assertThrows(IllegalArgumentException.class,
                () -> new BonusDecorator(new BasicQuestionView(q("x", Difficulty.EASY, null)), -1));
    }

    @Test
    void decoratorsCanBeStacked() {
        QuestionView v = new BonusDecorator(new HintDecorator(new FiftyFiftyDecorator(
                new BasicQuestionView(q("1", Difficulty.HARD, "намёк")), new Random(5))));
        assertEquals(2, v.getVisibleAnswers().size());
        assertEquals("намёк", v.getHint().orElseThrow());
        assertEquals(1, v.getBonusPoints());
        assertTrue(v.isCorrect(Set.of("a")));
    }
}
