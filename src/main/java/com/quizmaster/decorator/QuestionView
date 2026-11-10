package com.quizmaster.decorator;

import com.quizmaster.model.Answer;
import com.quizmaster.model.Question;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/** Component для паттерна Decorator: вопрос в том виде, в котором его видит пользователь. */
public interface QuestionView {
    Question getQuestion();

    /** Варианты ответов, которые сейчас показаны (50/50 скрывает часть). */
    List<Answer> getVisibleAnswers();

    /** Подсказка, если пользователь её запросил. */
    Optional<String> getHint();

    /** Дополнительные баллы за верный ответ (Bonus). */
    int getBonusPoints();

    boolean isCorrect(Set<String> selectedIds);
}
