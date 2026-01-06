package com.abderrahmane.gamecollection.controllers;

import com.abderrahmane.gamecollection.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class HomeController {

    @FXML private Button loginBtn;
    @FXML private Button signupBtn;

    @FXML
    public void initialize() {

        // Aller vers la page Login
        loginBtn.setOnAction(e ->
                SceneManager.switchTo("login.fxml")
        );

        // Aller vers la page Signup
        signupBtn.setOnAction(e ->
                SceneManager.switchTo("signup.fxml")
        );
    }
}
