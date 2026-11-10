
package com.quizmaster.controller;

import com.quizmaster.builder.QuizBuilder;
import com.quizmaster.factory.MultipleChoiceFactory;
import com.quizmaster.factory.MultipleSelectFactory;
import com.quizmaster.factory.QuestionFactory;
import com.quizmaster.factory.TrueFalseFactory;
import com.quizmaster.model.Answer;
import com.quizmaster.model.Difficulty;
import com.quizmaster.model.Question;
import com.quizmaster.model.QuestionType;
import com.quizmaster.model.Quiz;
import com.quizmaster.service.QuizManagementService;
import com.quizmaster.service.SampleQuizData;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QuizCreationController {

    private final QuizManagementService quizService =
            new QuizManagementService();

    private final ObservableList<Question> questions =
            FXCollections.observableArrayList();

    private final ObservableList<Quiz> quizzes =
            FXCollections.observableArrayList();

    private final TextField quizTitleField = new TextField();
    private final TextField categoryField = new TextField();
    private final TextArea descriptionArea = new TextArea();

    private final ComboBox<Difficulty> quizDifficulty = new ComboBox<>();

    private final TextField timeLimitField = new TextField("10");
    private final TextField questionLimitField = new TextField("10");

    private final ComboBox<QuestionType> questionType = new ComboBox<>();

    private final TextArea questionTextArea = new TextArea();
    private final ComboBox<Difficulty> questionDifficulty = new ComboBox<>();

    private final TextField answer1Field = new TextField();
    private final TextField answer2Field = new TextField();
    private final TextField answer3Field = new TextField();
    private final TextField answer4Field = new TextField();

    private final CheckBox correct1 = new CheckBox("Correct");
    private final CheckBox correct2 = new CheckBox("Correct");
    private final CheckBox correct3 = new CheckBox("Correct");
    private final CheckBox correct4 = new CheckBox("Correct");

    private final ListView<Question> questionList =
            new ListView<>(questions);

    private final ListView<Quiz> quizList =
            new ListView<>(quizzes);

    private final Label statusLabel = new Label();

    public QuizCreationController() {
        SampleQuizData.load(quizService);
        refreshQuizList();
    }

    public VBox createView() {

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label heading = new Label("QuizMaster - Quiz Creation");
        heading.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        quizDifficulty.getItems().setAll(Difficulty.values());
        quizDifficulty.setValue(Difficulty.EASY);

        questionDifficulty.getItems().setAll(Difficulty.values());
        questionDifficulty.setValue(Difficulty.EASY);

        questionType.getItems().setAll(QuestionType.values());
        questionType.setValue(QuestionType.MULTIPLE_CHOICE);

        descriptionArea.setPromptText("Enter quiz description");
        descriptionArea.setPrefRowCount(2);

        questionTextArea.setPromptText("Enter your question");
        questionTextArea.setPrefRowCount(2);

        quizTitleField.setPromptText("Quiz title");
        categoryField.setPromptText("Category, for example Programming");

        answer1Field.setPromptText("Answer 1");
        answer2Field.setPromptText("Answer 2");
        answer3Field.setPromptText("Answer 3");
        answer4Field.setPromptText("Answer 4");

        Button addQuestionButton = new Button("Add Question");
        Button editQuestionButton = new Button("Edit Selected Question");
        Button deleteQuestionButton = new Button("Delete Selected Question");

        Button saveQuizButton = new Button("Save Quiz");
        Button updateQuizButton = new Button("Update Selected Quiz");
        Button deleteQuizButton = new Button("Delete Selected Quiz");
        Button clearButton = new Button("New Quiz");

        addQuestionButton.setOnAction(event -> addQuestion());
        editQuestionButton.setOnAction(event -> editSelectedQuestion());
        deleteQuestionButton.setOnAction(event -> deleteSelectedQuestion());

        saveQuizButton.setOnAction(event -> saveQuiz());
        updateQuizButton.setOnAction(event -> updateQuiz());
        deleteQuizButton.setOnAction(event -> deleteQuiz());
        clearButton.setOnAction(event -> clearForm());

        questionList.setPrefHeight(150);
        quizList.setPrefHeight(150);

        quizList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Quiz quiz, boolean empty) {
                super.updateItem(quiz, empty);

                if (empty || quiz == null) {
                    setText(null);
                } else {
                    setText(quiz.getTitle() + " - " + quiz.getCategory());
                }
            }
        });

        questionList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Question question, boolean empty) {
                super.updateItem(question, empty);

                if (empty || question == null) {
                    setText(null);
                } else {
                    setText(question.getText() + " [" + question.getType() + "]");
                }
            }
        });

        quizList.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, selectedQuiz) -> {
                    if (selectedQuiz != null) {
                        loadQuiz(selectedQuiz);
                    }
                });

        questionList.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldQuestion, selectedQuestion) -> {
                    if (selectedQuestion != null) {
                        loadQuestion(selectedQuestion);
                    }
                });

        questionType.setOnAction(event -> updateAnswerFields());

        root.getChildren().addAll(
                heading,

                new Label("QUIZ INFORMATION"),
                new Label("Title"),
                quizTitleField,
                new Label("Description"),
                descriptionArea,
                new Label("Category"),
                categoryField,
                new Label("Quiz difficulty"),
                quizDifficulty,
                new Label("Time limit (minutes)"),
                timeLimitField,
                new Label("Maximum number of questions"),
                questionLimitField,

                new Separator(),

                new Label("QUESTION CREATION"),
                new Label("Question type"),
                questionType,
                new Label("Question text"),
                questionTextArea,
                new Label("Question difficulty"),
                questionDifficulty,

                new Label("Answer 1"),
                answer1Field,
                correct1,

                new Label("Answer 2"),
                answer2Field,
                correct2,

                new Label("Answer 3"),
                answer3Field,
                correct3,

                new Label("Answer 4"),
                answer4Field,
                correct4,

                addQuestionButton,
                new Label("Questions in current quiz"),
                questionList,
                editQuestionButton,
                deleteQuestionButton,

                saveQuizButton,
                clearButton,

                new Separator(),

                new Label("SAVED QUIZZES"),
                quizList,
                updateQuizButton,
                deleteQuizButton,

                statusLabel
        );

        updateAnswerFields();

        return root;
    }

    private void addQuestion() {

        try {
            String text = questionTextArea.getText().trim();

            if (text.isEmpty()) {
                throw new IllegalArgumentException("Enter the question text.");
            }

            QuestionType type = questionType.getValue();

            if (type == null || questionDifficulty.getValue() == null) {
                throw new IllegalArgumentException(
                        "Select question type and difficulty."
                );
            }

            int limit = parsePositiveInteger(
                    questionLimitField.getText(), "Question limit"
            );

            if (questions.size() >= limit) {
                throw new IllegalArgumentException(
                        "You have reached the question limit."
                );
            }

            QuestionFactory factory = getFactory(type);

            Question question = factory.createQuestion(
                    UUID.randomUUID().toString(),
                    text,
                    questionDifficulty.getValue(),
                    1,
                    createAnswers(type)
            );

            questions.add(question);
            clearQuestionEditor();

            statusLabel.setText("Question added successfully.");

        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private void loadQuestion(Question question) {

        questionType.setValue(question.getType());
        questionTextArea.setText(question.getText());
        questionDifficulty.setValue(question.getDifficulty());

        clearAnswerFields();

        List<Answer> answers = question.getAnswers();

        for (int i = 0; i < answers.size() && i < 4; i++) {

            Answer answer = answers.get(i);

            switch (i) {
                case 0:
                    answer1Field.setText(answer.getText());
                    correct1.setSelected(answer.isCorrect());
                    break;
                case 1:
                    answer2Field.setText(answer.getText());
                    correct2.setSelected(answer.isCorrect());
                    break;
                case 2:
                    answer3Field.setText(answer.getText());
                    correct3.setSelected(answer.isCorrect());
                    break;
                case 3:
                    answer4Field.setText(answer.getText());
                    correct4.setSelected(answer.isCorrect());
                    break;
                default:
                    break;
            }
        }

        statusLabel.setText("Question loaded for editing.");
    }

    private void editSelectedQuestion() {

        Question selected = questionList.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Select a question to edit.");
            return;
        }

        try {
            String text = questionTextArea.getText().trim();

            if (text.isEmpty()) {
                throw new IllegalArgumentException("Enter the question text.");
            }

            QuestionType type = questionType.getValue();

            if (type == null || questionDifficulty.getValue() == null) {
                throw new IllegalArgumentException(
                        "Select question type and difficulty."
                );
            }

            QuestionFactory factory = getFactory(type);

            Question updatedQuestion = factory.createQuestion(
                    selected.getId(),
                    text,
                    questionDifficulty.getValue(),
                    selected.getPoints(),
                    createAnswers(type)
            );

            int index = questions.indexOf(selected);

            if (index < 0) {
                throw new IllegalStateException("Question was not found.");
            }

            questions.set(index, updatedQuestion);

            clearQuestionEditor();

            statusLabel.setText("Question updated successfully.");

        } catch (IllegalArgumentException | IllegalStateException exception) {
            showError(exception.getMessage());
        }
    }

    private void deleteSelectedQuestion() {

        Question selected = questionList.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Select a question to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Question");
        confirmation.setHeaderText("Delete this question?");
        confirmation.setContentText(selected.getText());

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL)
                == ButtonType.OK) {

            questions.remove(selected);
            clearQuestionEditor();

            statusLabel.setText("Question deleted successfully.");
        }
    }

    private List<Answer> createAnswers(QuestionType type) {

        List<Answer> answers = new ArrayList<>();

        if (type == QuestionType.TRUE_FALSE) {

            answers.add(new Answer(
                    UUID.randomUUID().toString(),
                    "True",
                    correct1.isSelected()
            ));

            answers.add(new Answer(
                    UUID.randomUUID().toString(),
                    "False",
                    correct2.isSelected()
            ));

            return answers;
        }

        String[] texts = {
                answer1Field.getText(),
                answer2Field.getText(),
                answer3Field.getText(),
                answer4Field.getText()
        };

        boolean[] correct = {
                correct1.isSelected(),
                correct2.isSelected(),
                correct3.isSelected(),
                correct4.isSelected()
        };

        for (int i = 0; i < texts.length; i++) {

            if (texts[i] != null && !texts[i].isBlank()) {
                answers.add(new Answer(
                        UUID.randomUUID().toString(),
                        texts[i].trim(),
                        correct[i]
                ));
            }
        }

        return answers;
    }

    private QuestionFactory getFactory(QuestionType type) {

        switch (type) {
            case MULTIPLE_CHOICE:
                return new MultipleChoiceFactory();
            case TRUE_FALSE:
                return new TrueFalseFactory();
            case MULTIPLE_SELECT:
                return new MultipleSelectFactory();
            default:
                throw new IllegalArgumentException(
                        "Unsupported question type."
                );
        }
    }

    private void updateAnswerFields() {

        boolean trueFalse =
                questionType.getValue() == QuestionType.TRUE_FALSE;

        if (trueFalse) {
            answer1Field.setText("True");
            answer2Field.setText("False");

            answer1Field.setDisable(true);
            answer2Field.setDisable(true);
            answer3Field.setDisable(true);
            answer4Field.setDisable(true);

            answer3Field.clear();
            answer4Field.clear();

        } else {
            answer1Field.setDisable(false);
            answer2Field.setDisable(false);
            answer3Field.setDisable(false);
            answer4Field.setDisable(false);

            answer1Field.clear();
            answer2Field.clear();
            answer3Field.clear();
            answer4Field.clear();
        }

        correct1.setSelected(false);
        correct2.setSelected(false);
        correct3.setSelected(false);
        correct4.setSelected(false);
    }

    private void saveQuiz() {

        try {
            if (questions.isEmpty()) {
                throw new IllegalArgumentException("Add at least one question.");
            }

            int timeLimit = parsePositiveInteger(
                    timeLimitField.getText(), "Time limit"
            );

            int questionLimit = parsePositiveInteger(
                    questionLimitField.getText(), "Question limit"
            );

            if (questions.size() > questionLimit) {
                throw new IllegalArgumentException(
                        "Question count exceeds the selected limit."
                );
            }

            Quiz quiz = new QuizBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setTitle(quizTitleField.getText().trim())
                    .setDescription(descriptionArea.getText().trim())
                    .setCategory(categoryField.getText().trim())
                    .setDifficulty(quizDifficulty.getValue())
                    .setTimeLimitSeconds(timeLimit * 60)
                    .setQuestionLimit(questionLimit)
                    .addQuestions(new ArrayList<>(questions))
                    .build();

            quizService.createQuiz(quiz);

            refreshQuizList();
            clearForm();

            statusLabel.setText("Quiz saved successfully.");

        } catch (IllegalArgumentException | IllegalStateException exception) {
            showError(exception.getMessage());
        }
    }

    private void updateQuiz() {

        Quiz selected = quizList.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Select a quiz to update.");
            return;
        }

        try {
            int timeLimit = parsePositiveInteger(
                    timeLimitField.getText(), "Time limit"
            );

            int questionLimit = parsePositiveInteger(
                    questionLimitField.getText(), "Question limit"
            );

            Quiz updatedQuiz = new QuizBuilder()
                    .setId(selected.getId())
                    .setTitle(quizTitleField.getText().trim())
                    .setDescription(descriptionArea.getText().trim())
                    .setCategory(categoryField.getText().trim())
                    .setDifficulty(quizDifficulty.getValue())
                    .setTimeLimitSeconds(timeLimit * 60)
                    .setQuestionLimit(questionLimit)
                    .addQuestions(new ArrayList<>(questions))
                    .build();

            quizService.updateQuiz(selected.getId(), updatedQuiz);

            refreshQuizList();

            statusLabel.setText("Quiz updated successfully.");

        } catch (IllegalArgumentException | IllegalStateException exception) {
            showError(exception.getMessage());
        }
    }

    private void deleteQuiz() {

        Quiz selected = quizList.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Select a quiz to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Quiz");
        confirmation.setHeaderText("Delete " + selected.getTitle() + "?");
        confirmation.setContentText("This action cannot be undone.");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL)
                == ButtonType.OK) {

            quizService.deleteQuiz(selected.getId());

            refreshQuizList();
            clearForm();

            statusLabel.setText("Quiz deleted successfully.");
        }
    }

    private void loadQuiz(Quiz quiz) {

        quizTitleField.setText(quiz.getTitle());
        descriptionArea.setText(quiz.getDescription());
        categoryField.setText(quiz.getCategory());

        quizDifficulty.setValue(quiz.getDifficulty());

        timeLimitField.setText(
                String.valueOf(quiz.getTimeLimitSeconds() / 60)
        );

        questionLimitField.setText(
                String.valueOf(quiz.getQuestionLimit())
        );

        questions.setAll(quiz.getQuestions());

        clearQuestionEditor();

        statusLabel.setText("Quiz selected for editing.");
    }

    private void refreshQuizList() {
        quizzes.setAll(quizService.getAllQuizzes());
    }

    private void clearForm() {

        quizTitleField.clear();
        descriptionArea.clear();
        categoryField.clear();

        quizDifficulty.setValue(Difficulty.EASY);

        timeLimitField.setText("10");
        questionLimitField.setText("10");

        questions.clear();

        clearQuestionEditor();

        quizList.getSelectionModel().clearSelection();

        statusLabel.setText("");
    }

    private void clearQuestionEditor() {

        questionTextArea.clear();
        questionDifficulty.setValue(Difficulty.EASY);

        clearAnswerFields();

        questionList.getSelectionModel().clearSelection();
    }

    private void clearAnswerFields() {

        answer1Field.clear();
        answer2Field.clear();
        answer3Field.clear();
        answer4Field.clear();

        correct1.setSelected(false);
        correct2.setSelected(false);
        correct3.setSelected(false);
        correct4.setSelected(false);

        updateAnswerFields();
    }

    private int parsePositiveInteger(String value, String fieldName) {

        try {
            int number = Integer.parseInt(value.trim());

            if (number <= 0) {
                throw new NumberFormatException();
            }

            return number;

        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    fieldName + " must be a positive whole number."
            );
        }
    }

    private void showError(String message) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("QuizMaster Error");
        alert.setHeaderText("Cannot complete the operation");
        alert.setContentText(message);
        alert.showAndWait();

        statusLabel.setText("Error: " + message);
    }
}
