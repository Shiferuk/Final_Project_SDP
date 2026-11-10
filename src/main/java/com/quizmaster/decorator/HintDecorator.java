package com.quizmaster.decorator;

import java.util.Optional;

/** Hint: показывает подсказку к вопросу. */
public class HintDecorator extends QuestionDecorator {
    public HintDecorator(QuestionView wrapped) { super(wrapped); }

    @Override
    public Optional<String> getHint() {
        String hint = getQuestion().getHint();
        if (hint == null || hint.isBlank()) return Optional.of("Для этого вопроса подсказки нет.");
        return Optional.of(hint);
    }
}
