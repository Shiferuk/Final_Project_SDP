package com.quizmaster.controller;

import com.quizmaster.decorator.QuestionView;
import com.quizmaster.facade.QuizFacade;
import com.quizmaster.model.Answer;
import com.quizmaster.model.Question;
import com.quizmaster.model.QuestionType;
import com.quizmaster.model.Quiz;
import com.quizmaster.model.QuizAttempt;
import com.quizmaster.model.QuizResult;
import com.quizmaster.observer.TimerObserver;
import com.quizmaster.service.QuizManagementService;
import com.quizmaster.strategy.DifficultyBasedScoring;
import com.quizmaster.strategy.ScoreReport;
import com.quizmaster.strategy.ScoringStrategy;
import com.quizmaster.strategy.StandardScoring;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * "Take a quiz" screen. It talks only to QuizFacade: the facade creates the
 * timer (Observer), wraps questions in decorators and uses the chosen
 * scoring strategy.
 */
public class QuizPlayController implements TimerObserver {

    private static final String USER_ID = "local-user";

    private final QuizManagementService quizService;
    private final QuizFacade facade;

    private final ComboBox<Quiz> quizBox = new ComboBox<>();
    private final ComboBox<ScoringStrategy> strategyBox = new ComboBox<>();
    private final Button startButton = new Button("Start Quiz");
    private final Button cancelButton = new Button("Cancel Quiz");

    private final Label timerLabel = new Label("Time: -");
    private final Label questionLabel = new Label();
    private final Label hintLabel = new Label();
    private final VBox answersBox = new VBox(6);
    private final Button hintButton = new Button("Hint");
    private final Button fiftyButton = new Button("50/50");
    private final Button bonusButton = new Button("Bonus (+1)");
    private final Button previousButton = new Button("Previous");
    private final Button nextButton = new Button("Next");
    private final Button finishButton = new Button("Finish");

    private final Label resultLabel = new Label();
    private final Label statusLabel = new Label();
    private final ListView<String> historyList = new ListView<>();

    private final Map<String, BooleanProperty> shownAnswers = new LinkedHashMap<>();
    private final Map<String, Set<String>> chosen = new HashMap<>();
    private List<QuestionView> questions = List.of();
    private int index;
    private boolean running;

    public QuizPlayController(QuizManagementService quizService, QuizFacade facade) {
        this.quizService = quizService;
        this.facade = facade;
        facade.addObserver(this);
    }

    public VBox createView() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        strategyBox.setItems(FXCollections.observableArrayList(
                new StandardScoring(), new DifficultyBasedScoring()));
        strategyBox.setValue(strategyBox.getItems().get(0));
        strategyBox.setConverter(new StringConverter<>() {
            @Override public String toString(ScoringStrategy s) {
                return s == null ? "" : s.getName();
            }
            @Override public ScoringStrategy fromString(String s) { return null; }
        });

        quizBox.setConverter(new StringConverter<>() {
            @Override public String toString(Quiz q) {
                return q == null ? "" : q.getTitle() + " (" + q.getCategory() + ", "
                        + q.getQuestions().size() + " questions)";
            }
            @Override public Quiz fromString(String s) { return null; }
        });

        questionLabel.setWrapText(true);
        hintLabel.setWrapText(true);
        hintLabel.setStyle("-fx-text-fill: #1565c0;");
        resultLabel.setWrapText(true);
        resultLabel.setStyle("-fx-font-weight: bold;");
        statusLabel.setStyle("-fx-text-fill: #c62828;");
        historyList.setPrefHeight(160);

        startButton.setOnAction(e -> startQuiz());
        cancelButton.setOnAction(e -> cancelQuiz());
        hintButton.setOnAction(e -> useHint());
        fiftyButton.setOnAction(e -> useFiftyFifty());
        bonusButton.setOnAction(e -> useBonus());
        previousButton.setOnAction(e -> move(-1));
        nextButton.setOnAction(e -> move(1));
        finishButton.setOnAction(e -> finishQuiz());

        // Strategy can be changed even while the quiz is running
        strategyBox.setOnAction(e -> {
            if (running && strategyBox.getValue() != null) {
                facade.changeStrategy(strategyBox.getValue());
            }
        });

        root.getChildren().addAll(
                new Label("TAKE A QUIZ"),
                new Label("Quiz"), quizBox,
                new Label("Scoring algorithm"), strategyBox,
                new HBox(10, startButton, cancelButton),
                new Separator(),
                timerLabel, questionLabel, answersBox, hintLabel,
                new HBox(10, hintButton, fiftyButton, bonusButton),
                new HBox(10, previousButton, nextButton, finishButton),
                statusLabel,
                new Separator(),
                new Label("RESULT"), resultLabel,
                new Separator(),
                new Label("HISTORY"), historyList
        );

        refresh();
        setRunning(false);
        return root;
    }

    /** Called when the tab is opened: reloads quiz list and history. */
    public void refresh() {
        Quiz selected = quizBox.getValue();
        quizBox.setItems(FXCollections.observableArrayList(quizService.getAllQuizzes()));
        if (selected != null) {
            for (Quiz quiz : quizBox.getItems()) {
                if (quiz.getId().equals(selected.getId())) {
                    quizBox.setValue(quiz);
                }
            }
        }
        if (quizBox.getValue() == null && !quizBox.getItems().isEmpty()) {
            quizBox.setValue(quizBox.getItems().get(0));
        }
        refreshHistory();
    }

    private void startQuiz() {
        try {
            Quiz quiz = quizBox.getValue();
            if (quiz == null) {
                throw new IllegalArgumentException("Select a quiz first.");
            }
            facade.startQuiz(USER_ID, quiz, strategyBox.getValue());
            questions = facade.getQuestionViews();
            chosen.clear();
            index = 0;
            resultLabel.setText("");
            statusLabel.setText("");
            setRunning(true);
            showQuestion();
        } catch (IllegalArgumentException | IllegalStateException e) {
            statusLabel.setText(e.getMessage());
        }
    }

    private void cancelQuiz() {
        try {
            facade.cancelQuiz();
            setRunning(false);
            clearQuestion();
            resultLabel.setText("Quiz cancelled. Nothing was saved.");
        } catch (IllegalStateException e) {
            statusLabel.setText(e.getMessage());
        }
    }

    private void finishQuiz() {
        try {
            saveSelection();
            facade.finishQuiz();
            showResult();
        } catch (IllegalStateException e) {
            statusLabel.setText(e.getMessage());
        }
    }

    private void showResult() {
        ScoreReport report = facade.getLastReport();
        QuizAttempt attempt = facade.getCurrentAttempt();
        QuizResult result = attempt == null ? null : attempt.getResult();

        StringBuilder text = new StringBuilder();
        if (report != null) {
            text.append(strategyBox.getValue() == null ? "" : strategyBox.getValue().getName())
                    .append(": ").append(report);
            if (!report.getWrongQuestionIds().isEmpty()) {
                text.append("\nMistakes in questions: ");
                for (QuestionView view : questions) {
                    if (report.getWrongQuestionIds().contains(view.getQuestion().getId())) {
                        text.append("\n - ").append(view.getQuestion().getText());
                    }
                }
            }
        } else if (result != null) {
            text.append("Score: ").append(result.getScore());
        }
        resultLabel.setText(text.toString());
        setRunning(false);
        clearQuestion();
        refreshHistory();
    }

    // ----- question display -----

    private void showQuestion() {
        QuestionView view = questions.get(index);
        Question question = view.getQuestion();
        questionLabel.setText("Question " + (index + 1) + "/" + questions.size()
                + " [" + question.getDifficulty() + "]\n" + question.getText());
        hintLabel.setText(view.getHint().orElse(""));
        renderAnswers(view);
        previousButton.setDisable(index == 0);
        nextButton.setDisable(index == questions.size() - 1);
    }

    private void renderAnswers(QuestionView view) {
        answersBox.getChildren().clear();
        shownAnswers.clear();
        Set<String> previous = chosen.getOrDefault(view.getQuestion().getId(), Set.of());
        boolean multiple = view.getQuestion().getType() == QuestionType.MULTIPLE_SELECT;
        ToggleGroup group = new ToggleGroup();

        for (Answer answer : view.getVisibleAnswers()) {
            if (multiple) {
                CheckBox box = new CheckBox(answer.getText());
                box.setSelected(previous.contains(answer.getId()));
                shownAnswers.put(answer.getId(), box.selectedProperty());
                answersBox.getChildren().add(box);
            } else {
                RadioButton radio = new RadioButton(answer.getText());
                radio.setToggleGroup(group);
                radio.setSelected(previous.contains(answer.getId()));
                shownAnswers.put(answer.getId(), radio.selectedProperty());
                answersBox.getChildren().add(radio);
            }
        }
    }

    private void saveSelection() {
        if (!running || questions.isEmpty()) {
            return;
        }
        Set<String> selected = new HashSet<>();
        for (Map.Entry<String, BooleanProperty> entry : shownAnswers.entrySet()) {
            if (entry.getValue().get()) {
                selected.add(entry.getKey());
            }
        }
        String questionId = questions.get(index).getQuestion().getId();
        chosen.put(questionId, selected);
        facade.selectAnswer(questionId, selected);
    }

    private void move(int delta) {
        saveSelection();
        index = Math.max(0, Math.min(questions.size() - 1, index + delta));
        refreshViews();
        showQuestion();
    }

    private void refreshViews() {
        questions = facade.getQuestionViews();
    }

    private void clearQuestion() {
        questionLabel.setText("");
        hintLabel.setText("");
        answersBox.getChildren().clear();
        shownAnswers.clear();
        timerLabel.setText("Time: -");
    }

    // ----- help buttons (Decorator) -----

    private void useHint() {
        saveSelection();
        hintLabel.setText("Hint: " + facade.useHint(currentQuestionId()).orElse(""));
    }

    private void useFiftyFifty() {
        saveSelection();
        facade.useFiftyFifty(currentQuestionId());
        refreshViews();
        showQuestion();
    }

    private void useBonus() {
        saveSelection();
        facade.useBonus(currentQuestionId());
        refreshViews();
        statusLabel.setText("Bonus applied: +1 point if this question is answered correctly.");
    }

    private String currentQuestionId() {
        return questions.get(index).getQuestion().getId();
    }

    private void setRunning(boolean value) {
        running = value;
        startButton.setDisable(value);
        quizBox.setDisable(value);
        for (Button b : List.of(cancelButton, hintButton, fiftyButton, bonusButton,
                previousButton, nextButton, finishButton)) {
            b.setDisable(!value);
        }
    }

    private void refreshHistory() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        historyList.getItems().clear();
        for (QuizAttempt attempt : facade.getHistory()) {
            if (attempt.getResult() == null) {
                continue;
            }
            String title = attempt.getQuizId();
            try {
                title = quizService.getQuiz(attempt.getQuizId()).getTitle();
            } catch (IllegalArgumentException ignored) {
                // quiz was deleted - show its id
            }
            historyList.getItems().add(
                    (attempt.getFinishedAt() == null ? "" : attempt.getFinishedAt().format(format) + "  ")
                            + title + " - score " + attempt.getResult().getScore()
                            + ", correct " + attempt.getResult().getCorrectAnswers()
                            + "/" + attempt.getResult().getTotalQuestions()
                            + String.format(" (%.0f%%)", attempt.getResult().getPercentage()));
        }
    }

    // ----- Observer: timer events arrive on a background thread -----

    @Override
    public void onTimeChanged(int remainingSeconds) {
        Platform.runLater(() -> timerLabel.setText(
                String.format("Time: %02d:%02d", remainingSeconds / 60, remainingSeconds % 60)));
    }

    @Override
    public void onTimeFinished() {
        // the facade has already finished and saved the attempt
        Platform.runLater(() -> {
            if (running) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Time is up! The quiz was finished automatically.");
                alert.show();
                showResult();
            }
        });
    }
}
