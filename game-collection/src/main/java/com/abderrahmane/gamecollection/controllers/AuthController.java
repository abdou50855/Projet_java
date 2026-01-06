package com.abderrahmane.gamecollection.controllers;

import com.abderrahmane.gamecollection.SceneManager;
import com.abderrahmane.gamecollection.Session;
import com.abderrahmane.gamecollection.models.User;
import com.abderrahmane.gamecollection.services.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AuthController {

    // Champs pour login.fxml
    @FXML private TextField usernameField;       // login.fxml
    @FXML private PasswordField passwordField;   // login.fxml
    @FXML private Button loginBtn;               // login.fxml
    @FXML private Button goToSignupBtn;          // login.fxml

    // Champs pour signup.fxml
    @FXML private TextField signupUsernameField; // signup.fxml
    @FXML private PasswordField signupPasswordField; // signup.fxml
    @FXML private Button signupBtn;                  // signup.fxml
    @FXML private Button goToLoginBtn;               // signup.fxml

    @FXML private Label messageLabel; // optionnel, pour afficher des messages

    @FXML
    public void initialize() {
        // Actions login
        if (loginBtn != null) loginBtn.setOnAction(e -> login());
        if (goToSignupBtn != null) goToSignupBtn.setOnAction(e -> SceneManager.switchTo("signup.fxml"));

        // Actions signup
        if (signupBtn != null) signupBtn.setOnAction(e -> signup());
        if (goToLoginBtn != null) goToLoginBtn.setOnAction(e -> SceneManager.switchTo("login.fxml"));
    }

    private void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.", Alert.AlertType.ERROR);
            return;
        }

        User user = UserService.login(username, password);

        if (user == null) {
            showAlert("Erreur", "Nom d'utilisateur ou mot de passe incorrect.", Alert.AlertType.WARNING);
            return;
        }

        // Stocker l’utilisateur dans la session
        Session.setUser(user);

        // Redirection selon le rôle
        if ("admin".equals(user.getRole())) {
            SceneManager.switchTo("admin/admin_view.fxml");
        } else {
            SceneManager.switchTo("game_view.fxml");
        }
    }

    private void signup() {
        String username = signupUsernameField.getText();
        String password = signupPasswordField.getText();

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.", Alert.AlertType.ERROR);
            return;
        }

        // rôle forcé à "user"
        String role = "user";

        boolean success = UserService.signup(username, password, role);

        if (!success) {
            showAlert("Erreur", "Nom d'utilisateur déjà utilisé.", Alert.AlertType.WARNING);
            return;
        }

        showAlert("Succès", "Compte créé avec succès !", Alert.AlertType.INFORMATION);
        SceneManager.switchTo("login.fxml");
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
