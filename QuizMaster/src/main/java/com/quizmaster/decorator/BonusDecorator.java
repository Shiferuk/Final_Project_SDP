package com.quizmaster.decorator;

/** Bonus: добавляет дополнительные баллы, если на вопрос ответили верно. */
public class BonusDecorator extends QuestionDecorator {
    public static final int DEFAULT_BONUS = 1;
    private final int bonus;

    public BonusDecorator(QuestionView wrapped) { this(wrapped, DEFAULT_BONUS); }

    public BonusDecorator(QuestionView wrapped, int bonus) {
        super(wrapped);
        if (bonus < 0) throw new IllegalArgumentException("Бонус не может быть отрицательным");
        this.bonus = bonus;
    }

    @Override
    public int getBonusPoints() { return super.getBonusPoints() + bonus; }
}
