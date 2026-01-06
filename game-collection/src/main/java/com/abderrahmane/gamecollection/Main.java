package com.abderrahmane.gamecollection;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Enregistrer le stage dans le SceneManager
        SceneManager.setStage(stage);

        // Charger la première scène via SceneManager
        SceneManager.switchTo("home.fxml");

        stage.setTitle("Game Collection");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
