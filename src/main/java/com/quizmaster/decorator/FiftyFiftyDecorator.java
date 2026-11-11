package com.quizmaster.decorator;

import com.quizmaster.model.Answer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Fifty-Fifty: убирает два неправильных варианта.
 * Если неправильных меньше трёх (например, True/False), убирается столько,
 * чтобы остался хотя бы один неправильный вариант и выбор не исчез.
 * Набор скрытых вариантов вычисляется один раз, чтобы он не менялся при перерисовке экрана.
 */
public class FiftyFiftyDecorator extends QuestionDecorator {
    private static final int TO_REMOVE = 2;
    private final List<Answer> visible;

    public FiftyFiftyDecorator(QuestionView wrapped) { this(wrapped, new Random()); }

    /** Конструктор с Random нужен для воспроизводимых тестов. */
    public FiftyFiftyDecorator(QuestionView wrapped, Random random) {
        super(wrapped);
        List<Answer> current = wrapped.getVisibleAnswers();
        List<Answer> wrong = new ArrayList<>();
        for (Answer a : current) if (!a.isCorrect()) wrong.add(a);
        int removeCount = Math.max(0, Math.min(TO_REMOVE, wrong.size() - 1));
        Collections.shuffle(wrong, random);
        List<Answer> removed = wrong.subList(0, removeCount);
        List<Answer> result = new ArrayList<>();
        for (Answer a : current) if (!removed.contains(a)) result.add(a);
        this.visible = Collections.unmodifiableList(result);
    }

    @Override
    public List<Answer> getVisibleAnswers() { return visible; }
}
