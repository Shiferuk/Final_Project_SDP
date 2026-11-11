
package com.quizmaster.service;

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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SampleQuizData {

    private SampleQuizData() {
    }

    public static void load(QuizManagementService service) {

        if (!service.getAllQuizzes().isEmpty()) {
            return;
        }

        createProgrammingQuiz(service);
        createMathematicsQuiz(service);
        createEnglishQuiz(service);
    }

    private static void createProgrammingQuiz(QuizManagementService service) {

        List<Question> questions = new ArrayList<>();

        questions.add(createQuestion(
                QuestionType.MULTIPLE_CHOICE,
                "Which keyword is used to create a class in Java?",
                Difficulty.EASY,
                answer("function", false),
                answer("class", true),
                answer("create", false),
                answer("object", false)
        ));

        questions.add(createQuestion(
                QuestionType.MULTIPLE_SELECT,
                "Which of these are programming languages?",
                Difficulty.MEDIUM,
                answer("Python", true),
                answer("Java", true),
                answer("HTML", false),
                answer("CSS", false)
        ));

        Quiz quiz = createQuiz(
                "Programming Basics",
                "Basic questions about programming and Java.",
                "Programming",
                Difficulty.EASY,
                questions
        );

        service.createQuiz(quiz);
    }

    private static void createMathematicsQuiz(QuizManagementService service) {

        List<Question> questions = new ArrayList<>();

        questions.add(createQuestion(
                QuestionType.MULTIPLE_CHOICE,
                "What is 12 multiplied by 8?",
                Difficulty.EASY,
                answer("86", false),
                answer("96", true),
                answer("108", false),
                answer("92", false)
        ));

        questions.add(createQuestion(
                QuestionType.TRUE_FALSE,
                "The square root of 81 is 9.",
                Difficulty.EASY,
                answer("True", true),
                answer("False", false)
        ));

        Quiz quiz = createQuiz(
                "Mathematics Basics",
                "Simple mathematics questions.",
                "Mathematics",
                Difficulty.EASY,
                questions
        );

        service.createQuiz(quiz);
    }

    private static void createEnglishQuiz(QuizManagementService service) {

        List<Question> questions = new ArrayList<>();

        questions.add(createQuestion(
                QuestionType.TRUE_FALSE,
                "The word 'beautiful' is an adjective.",
                Difficulty.EASY,
                answer("True", true),
                answer("False", false)
        ));

        questions.add(createQuestion(
                QuestionType.MULTIPLE_CHOICE,
                "What is the past tense of 'go'?",
                Difficulty.MEDIUM,
                answer("Goed", false),
                answer("Gone", false),
                answer("Went", true),
                answer("Going", false)
        ));

        Quiz quiz = createQuiz(
                "English Grammar",
                "Basic English grammar questions.",
                "English",
                Difficulty.EASY,
                questions
        );

        service.createQuiz(quiz);
    }

    private static Quiz createQuiz(
            String title,
            String description,
            String category,
            Difficulty difficulty,
            List<Question> questions) {

        return new QuizBuilder()
                .setId(UUID.randomUUID().toString())
                .setTitle(title)
                .setDescription(description)
                .setCategory(category)
                .setDifficulty(difficulty)
                .setTimeLimitSeconds(600)
                .setQuestionLimit(5)
                .addQuestions(questions)
                .build();
    }

    private static Question createQuestion(
            QuestionType type,
            String text,
            Difficulty difficulty,
            Answer... answers) {

        QuestionFactory factory = QuestionFactory.forType(type);

        return factory.createQuestion(
                UUID.randomUUID().toString(),
                text,
                difficulty,
                1,
                hintFor(answers),
                List.of(answers)
        );
    }

    private static String hintFor(Answer... answers) {
        for (Answer answer : answers) {
            if (answer.isCorrect()) {
                return "A correct answer starts with '"
                        + answer.getText().charAt(0) + "'.";
            }
        }
        return "";
    }

    private static Answer answer(String text, boolean correct) {
        return new Answer(
                UUID.randomUUID().toString(),
                text,
                correct
        );
    }
}
