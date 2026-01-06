package com.abderrahmane.gamecollection;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
    private static Stage primaryStage;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchTo(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/views/" + fxmlFile));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // ✅ Appliquer style.css globalement
            scene.getStylesheets().add(SceneManager.class.getResource("/style.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
