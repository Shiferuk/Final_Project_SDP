
package com.quizmaster.service;

import com.quizmaster.model.Question;
import com.quizmaster.model.Quiz;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QuizManagementService {

    private final Map<String, Quiz> quizzes = new LinkedHashMap<>();

    // Create a new quiz
    public void createQuiz(Quiz quiz) {
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz cannot be null");
        }

        if (quizzes.containsKey(quiz.getId())) {
            throw new IllegalArgumentException(
                    "Quiz with this ID already exists"
            );
        }

        quizzes.put(quiz.getId(), quiz);
    }

    // Get one quiz by ID
    public Quiz getQuiz(String quizId) {
        if (quizId == null || quizId.isBlank()) {
            throw new IllegalArgumentException("Quiz ID cannot be empty");
        }

        Quiz quiz = quizzes.get(quizId);

        if (quiz == null) {
            throw new IllegalArgumentException("Quiz not found: " + quizId);
        }

        return quiz;
    }

    // Get all quizzes
    public List<Quiz> getAllQuizzes() {
        return new ArrayList<>(quizzes.values());
    }

    // Update an existing quiz
    public void updateQuiz(String quizId, Quiz updatedQuiz) {
        if (quizId == null || quizId.isBlank()) {
            throw new IllegalArgumentException("Quiz ID cannot be empty");
        }

        if (updatedQuiz == null) {
            throw new IllegalArgumentException("Updated quiz cannot be null");
        }

        if (!quizzes.containsKey(quizId)) {
            throw new IllegalArgumentException("Quiz not found: " + quizId);
        }

        if (!quizId.equals(updatedQuiz.getId())) {
            throw new IllegalArgumentException(
                    "Quiz ID cannot be changed during update"
            );
        }

        quizzes.put(quizId, updatedQuiz);
    }

    // Delete a quiz
    public void deleteQuiz(String quizId) {
        if (quizId == null || quizId.isBlank()) {
            throw new IllegalArgumentException("Quiz ID cannot be empty");
        }

        if (quizzes.remove(quizId) == null) {
            throw new IllegalArgumentException("Quiz not found: " + quizId);
        }
    }

    // Add a question to a quiz
    public void addQuestion(String quizId, Question question) {
        Quiz quiz = getQuiz(quizId);
        quiz.addQuestion(question);
    }

    // Remove a question from a quiz
    public void removeQuestion(String quizId, String questionId) {
        Quiz quiz = getQuiz(quizId);
        quiz.removeQuestion(questionId);
    }

    // Replace an existing question
    public void updateQuestion(
            String quizId,
            String questionId,
            Question updatedQuestion
    ) {
        if (updatedQuestion == null) {
            throw new IllegalArgumentException(
                    "Updated question cannot be null"
            );
        }

        if (!questionId.equals(updatedQuestion.getId())) {
            throw new IllegalArgumentException(
                    "Question ID cannot be changed during update"
            );
        }

        Quiz quiz = getQuiz(quizId);
        quiz.replaceQuestion(questionId, updatedQuestion);
    }

    // Find quizzes by category
    public List<Quiz> findByCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category cannot be empty");
        }

        List<Quiz> result = new ArrayList<>();

        for (Quiz quiz : quizzes.values()) {
            if (quiz.getCategory().equalsIgnoreCase(category)) {
                result.add(quiz);
            }
        }

        return result;
    }

    // Find quizzes by title
    public List<Quiz> searchByTitle(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("Search keyword cannot be empty");
        }

        List<Quiz> result = new ArrayList<>();

        for (Quiz quiz : quizzes.values()) {
            if (quiz.getTitle().toLowerCase()
                    .contains(keyword.toLowerCase())) {
                result.add(quiz);
            }
        }

        return result;
    }

    public int getQuizCount() {
        return quizzes.size();
    }
}
