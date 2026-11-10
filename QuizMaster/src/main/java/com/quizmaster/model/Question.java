package com.quizmaster.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Вопрос теста. Базовый класс; конкретные типы (MultipleChoice, TrueFalse,
 * MultipleSelect) создаёт Участник 1 через Factory Method.
 * Здесь - минимальный контракт, от которого зависят Strategy и Decorator.
 */
public abstract class Question {
    private final String id;
    private final String text;
    private final Difficulty difficulty;
    private final String hint;
    private final List<Answer> answers;

    protected Question(String id, String text, Difficulty difficulty, String hint, List<Answer> answers) {
        this.id = id;
        this.text = text;
        this.difficulty = difficulty;
        this.hint = hint;
        this.answers = new ArrayList<>(answers);
    }

    public String getId() { return id; }
    public String getText() { return text; }
    public Difficulty getDifficulty() { return difficulty; }
    /** Текст подсказки; может быть null. */
    public String getHint() { return hint; }
    public List<Answer> getAnswers() { return Collections.unmodifiableList(answers); }

    /** Верно, если выбранные id совпадают ровно с множеством правильных ответов. */
    public boolean isCorrect(Set<String> selectedIds) {
        Set<String> correct = answers.stream()
                .filter(Answer::isCorrect).map(Answer::getId).collect(Collectors.toSet());
        return correct.equals(selectedIds);
    }
}
