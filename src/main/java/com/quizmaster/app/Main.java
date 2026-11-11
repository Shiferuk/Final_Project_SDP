package com.quizmaster.app;

import com.quizmaster.controller.QuizCreationController;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        QuizCreationController controller =
                new QuizCreationController();

        VBox content = controller.createView();

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 800, 900);

        stage.setTitle("QuizMaster - Create Your Own Quiz");
        stage.setScene(scene);
        stage.setMinWidth(650);
        stage.setMinHeight(700);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}