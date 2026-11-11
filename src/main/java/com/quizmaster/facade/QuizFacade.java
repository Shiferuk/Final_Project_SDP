package com.quizmaster.facade;
import com.quizmaster.decorator.BasicQuestionView;
import com.quizmaster.decorator.BonusDecorator;
import com.quizmaster.decorator.FiftyFiftyDecorator;
import com.quizmaster.decorator.HintDecorator;
import com.quizmaster.decorator.QuestionView;
import com.quizmaster.model.Question;
import com.quizmaster.model.Quiz;
import com.quizmaster.model.QuizAttempt;
import com.quizmaster.model.QuizResult;
import com.quizmaster.observer.QuizTimer;
import com.quizmaster.observer.TimerObserver;
import com.quizmaster.repository.AttemptRepository;
import com.quizmaster.strategy.ScoreCalculator;
import com.quizmaster.strategy.ScoreReport;
import com.quizmaster.strategy.ScoringStrategy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
public class QuizFacade implements TimerObserver, AutoCloseable {
    private QuizTimer timer;
    private QuizAttempt currentAttempt;
    private final AttemptRepository repository;
    private int totalQuestions;
    private int correctAnswers;
    private int answeredQuestions;
    private int score;
    private final List<QuizAttempt> history = new ArrayList<>();
    private final List<TimerObserver> observers = new ArrayList<>();
    // Data for a quiz started with startQuiz(userId, Quiz, strategy)
    private final Map<String, QuestionView> views = new LinkedHashMap<>();
    private final Map<String, Set<String>> selectedAnswers = new HashMap<>();
    private ScoreCalculator calculator;
    private ScoreReport lastReport;

    public QuizFacade() {
        this(new AttemptRepository("data/attempts.json"));
    }

    public QuizFacade(AttemptRepository repository) {
        this.repository = repository;
        history.addAll(repository.load());
    }

    /**
     * Full scenario: starts a real quiz. Every question is wrapped in a
     * QuestionView (Decorator), the score is calculated by the chosen
     * ScoringStrategy and the timer (Observer) finishes the quiz automatically.
     */
    public synchronized void startQuiz(String userId, Quiz quiz, ScoringStrategy strategy) {
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz cannot be null");
        }
        if (quiz.getQuestions().isEmpty()) {
            throw new IllegalArgumentException("Quiz has no questions");
        }
        // the ScoreCalculator constructor rejects a null strategy
        ScoreCalculator newCalculator = new ScoreCalculator(strategy);

        startQuiz(userId, quiz.getId(), quiz.getTimeLimitSeconds(),
                quiz.getQuestions().size());

        calculator = newCalculator;
        lastReport = null;
        views.clear();
        selectedAnswers.clear();
        for (Question question : quiz.getQuestions()) {
            views.put(question.getId(), new BasicQuestionView(question));
        }
    }

    public synchronized List<QuestionView> getQuestionViews() {
        return new ArrayList<>(views.values());
    }

    public synchronized QuestionView getQuestionView(String questionId) {
        QuestionView view = views.get(questionId);
        if (view == null) {
            throw new IllegalArgumentException("Question not found: " + questionId);
        }
        return view;
    }

    /** Decorator: Hint. */
    public synchronized Optional<String> useHint(String questionId) {
        QuestionView view = new HintDecorator(getQuestionView(questionId));
        views.put(questionId, view);
        return view.getHint();
    }

    /** Decorator: Fifty-Fifty. */
    public synchronized QuestionView useFiftyFifty(String questionId) {
        QuestionView view = new FiftyFiftyDecorator(getQuestionView(questionId));
        views.put(questionId, view);
        return view;
    }

    /** Decorator: Bonus. */
    public synchronized QuestionView useBonus(String questionId) {
        QuestionView view = new BonusDecorator(getQuestionView(questionId));
        views.put(questionId, view);
        return view;
    }

    /** Remembers the answer of the user (can be changed until the quiz is finished). */
    public synchronized void selectAnswer(String questionId, Set<String> answerIds) {
        if (currentAttempt == null || currentAttempt.isCompleted()) {
            throw new IllegalStateException("No active quiz");
        }
        getQuestionView(questionId);
        selectedAnswers.put(questionId, Set.copyOf(answerIds));
    }

    /** Strategy: the algorithm can be changed while the quiz is running. */
    public synchronized void changeStrategy(ScoringStrategy strategy) {
        if (calculator == null) {
            throw new IllegalStateException("No active quiz");
        }
        calculator.setStrategy(strategy);
    }

    public synchronized ScoreReport getLastReport() {
        return lastReport;
    }
    public synchronized void startQuiz(String userId, String quizId,
                                       int durationSeconds, int totalQuestions) {
        if (currentAttempt != null && !currentAttempt.isCompleted()) {
            throw new IllegalStateException("A quiz is already running");
        }
        if (totalQuestions <= 0) {
            throw new IllegalArgumentException(
                    "Total questions must be greater than zero"
            );
        }
        if (timer != null) {
            timer.close();
        }
        this.totalQuestions = totalQuestions;
        this.correctAnswers = 0;
        this.answeredQuestions = 0;
        this.score = 0;
        this.calculator = null;
        this.lastReport = null;
        this.views.clear();
        this.selectedAnswers.clear();
        currentAttempt = new QuizAttempt(
                UUID.randomUUID().toString(),
                userId,
                quizId
        );
        timer = new QuizTimer(durationSeconds);
        timer.addObserver(this);
        timer.start();
    }
    public void submitAnswer(boolean correct, int points) {
        if (currentAttempt == null || currentAttempt.isCompleted()) {
            throw new IllegalStateException("No active quiz");
        }
        if (answeredQuestions >= totalQuestions) {
            throw new IllegalStateException("All questions are answered");
        }
        if (points < 0) {
            throw new IllegalArgumentException("Points cannot be negative");
        }
        answeredQuestions++;
        if (correct) {
            correctAnswers++;
            score += points;
        }
    }
    public synchronized QuizResult finishQuiz() {
        if (currentAttempt == null || currentAttempt.isCompleted()) {
            throw new IllegalStateException("No active quiz");
        }
        if (timer != null) {
            timer.stop();
        }
        if (calculator != null && !views.isEmpty()) {
            lastReport = calculator.calculate(
                    new ArrayList<>(views.values()), selectedAnswers);
            correctAnswers = lastReport.getCorrectCount();
            score = lastReport.getScore();
        }
        QuizResult result = new QuizResult(
                totalQuestions,
                correctAnswers,
                score
        );
        currentAttempt.finish(result);
        history.add(currentAttempt);
        repository.save(history);
        return result;
    }
    /** Cancels the running quiz; nothing is saved to history. */
    public synchronized void cancelQuiz() {
        if (currentAttempt == null || currentAttempt.isCompleted()) {
            throw new IllegalStateException("No active quiz");
        }
        if (timer != null) {
            timer.stop();
        }
        currentAttempt = null;
        calculator = null;
        views.clear();
        selectedAnswers.clear();
    }

    public void addObserver(TimerObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Observer cannot be null");
        }
        observers.add(observer);
    }
    public void removeObserver(TimerObserver observer) {
        observers.remove(observer);
    }
    public List<QuizAttempt> getHistory() {
        return Collections.unmodifiableList(history);
    }
    public QuizAttempt getCurrentAttempt() {
        return currentAttempt;
    }
    @Override
    public void onTimeChanged(int remainingSeconds) {
        for (TimerObserver observer : observers) {
            observer.onTimeChanged(remainingSeconds);
        }
    }
    @Override
    public synchronized void onTimeFinished() {
        if (currentAttempt != null && !currentAttempt.isCompleted()) {
            finishQuiz();
        }
        for (TimerObserver observer : observers) {
            observer.onTimeFinished();
        }
    }
    @Override
    public void close() {
        if (timer != null) {
            timer.close();
        }
    }
}
