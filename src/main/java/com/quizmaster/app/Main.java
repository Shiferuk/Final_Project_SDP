package com.quizmaster.app;

import com.quizmaster.controller.QuizCreationController;
import com.quizmaster.controller.QuizPlayController;
import com.quizmaster.facade.QuizFacade;
import com.quizmaster.repository.QuizRepository;
import com.quizmaster.service.QuizManagementService;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class Main extends Application {

    private QuizFacade facade;

    @Override
    public void start(Stage stage) {

        QuizManagementService quizService =
                new QuizManagementService(new QuizRepository("data/quizzes.json"));
        facade = new QuizFacade();

        QuizCreationController creation = new QuizCreationController(quizService);
        QuizPlayController play = new QuizPlayController(quizService, facade);

        ScrollPane createPane = new ScrollPane(creation.createView());
        createPane.setFitToWidth(true);
        ScrollPane playPane = new ScrollPane(play.createView());
        playPane.setFitToWidth(true);

        Tab createTab = new Tab("Create / Edit Quiz", createPane);
        Tab playTab = new Tab("Take Quiz & History", playPane);
        createTab.setClosable(false);
        playTab.setClosable(false);
        playTab.setOnSelectionChanged(event -> {
            if (playTab.isSelected()) {
                play.refresh();
            }
        });

        Scene scene = new Scene(new TabPane(createTab, playTab), 850, 900);

        stage.setTitle("QuizMaster");
        stage.setScene(scene);
        stage.setMinWidth(650);
        stage.setMinHeight(700);
        stage.show();
    }

    @Override
    public void stop() {
        if (facade != null) {
            facade.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
