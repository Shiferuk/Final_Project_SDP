package com.quizmaster.decorator;

import com.quizmaster.model.Answer;
import com.quizmaster.model.Question;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/** Decorator: делегирует всё обёрнутому объекту; наследники переопределяют нужное. */
public abstract class QuestionDecorator implements QuestionView {
    protected final QuestionView wrapped;

    protected QuestionDecorator(QuestionView wrapped) {
        if (wrapped == null) throw new IllegalArgumentException("wrapped == null");
        this.wrapped = wrapped;
    }

    @Override public Question getQuestion() { return wrapped.getQuestion(); }
    @Override public List<Answer> getVisibleAnswers() { return wrapped.getVisibleAnswers(); }
    @Override public Optional<String> getHint() { return wrapped.getHint(); }
    @Override public int getBonusPoints() { return wrapped.getBonusPoints(); }
    @Override public boolean isCorrect(Set<String> selectedIds) { return wrapped.isCorrect(selectedIds); }
}
