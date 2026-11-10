package com.quizmaster.model;

/** Вариант ответа на вопрос. */
public class Answer {
    private final String id;
    private final String text;
    private final boolean correct;

    public Answer(String id, String text, boolean correct) {
        this.id = id;
        this.text = text;
        this.correct = correct;
    }

    public String getId() { return id; }
    public String getText() { return text; }
    public boolean isCorrect() { return correct; }

    @Override
    public String toString() { return id + ": " + text; }
}
