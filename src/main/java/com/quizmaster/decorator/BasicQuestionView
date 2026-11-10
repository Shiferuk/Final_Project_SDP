package com.quizmaster.decorator;

import com.quizmaster.model.Answer;
import com.quizmaster.model.Question;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/** ConcreteComponent: вопрос без дополнительных возможностей. */
public class BasicQuestionView implements QuestionView {
    private final Question question;

    public BasicQuestionView(Question question) {
        if (question == null) throw new IllegalArgumentException("question == null");
        this.question = question;
    }

    @Override public Question getQuestion() { return question; }
    @Override public List<Answer> getVisibleAnswers() { return question.getAnswers(); }
    @Override public Optional<String> getHint() { return Optional.empty(); }
    @Override public int getBonusPoints() { return 0; }
    @Override public boolean isCorrect(Set<String> selectedIds) { return question.isCorrect(selectedIds); }
}
