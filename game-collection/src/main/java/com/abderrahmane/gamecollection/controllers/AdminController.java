package com.abderrahmane.gamecollection.controllers;

import com.abderrahmane.gamecollection.SceneManager;
import com.abderrahmane.gamecollection.Session;
import com.abderrahmane.gamecollection.models.Game;
import com.abderrahmane.gamecollection.models.Platform;
import com.abderrahmane.gamecollection.models.User;
import com.abderrahmane.gamecollection.services.GameService;
import com.abderrahmane.gamecollection.services.PlatformService;
import com.abderrahmane.gamecollection.services.UserService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.util.List;

public class AdminController {

    // UTILISATEURS
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Integer> colUserId;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colPassword;
    @FXML private TableColumn<User, String> colRole;
    @FXML private Button btnDeleteUser;
    private final ObservableList<User> usersList = FXCollections.observableArrayList();

    // JEUX (avec image)
    @FXML private TableView<Game> gamesTable;
    @FXML private TableColumn<Game, Integer> colGameId;
    @FXML private TableColumn<Game, String> colGameName;
    @FXML private TableColumn<Game, Number> colGameYear;
    @FXML private TableColumn<Game, String> colGamePlatform;
    @FXML private TableColumn<Game, String> colGameImage;
    @FXML private Button btnAddGame;
    @FXML private Button btnEditGame;
    @FXML private Button btnDeleteGame;
    private final ObservableList<Game> gamesList = FXCollections.observableArrayList();

    // PLATEFORMES
    @FXML private TableView<Platform> platformsTable;
    @FXML private TableColumn<Platform, Integer> colPlatformId;
    @FXML private TableColumn<Platform, String> colPlatformName;
    @FXML private Button btnAddPlatform;
    @FXML private Button btnEditPlatform;
    @FXML private Button btnDeletePlatform;
    private final ObservableList<Platform> platformsList = FXCollections.observableArrayList();

    // COLLECTIONS (user -> game)
    @FXML private TableView<CollectionRow> collectionsTable;
    @FXML private TableColumn<CollectionRow, String> colCollectionUser;
    @FXML private TableColumn<CollectionRow, String> colCollectionGame;
    private final ObservableList<CollectionRow> collectionsList = FXCollections.observableArrayList();

    // Bouton déconnexion
    @FXML private Button btnLogout;

    // Sections
    @FXML private VBox usersSection;
    @FXML private VBox gamesSection;
    @FXML private VBox platformsSection;
    @FXML private VBox collectionsSection;

    @FXML
    public void initialize() {
        setupUserTable();
        setupGameTable();
        setupPlatformTable();
        setupCollectionTable();

        loadUsers();
        loadGames();
        loadPlatforms();
        loadCollections();

        if (btnDeleteUser != null) btnDeleteUser.setOnAction(e -> deleteUser());
        if (btnAddGame != null) btnAddGame.setOnAction(e -> addGame());
        if (btnEditGame != null) btnEditGame.setOnAction(e -> editGame());
        if (btnDeleteGame != null) btnDeleteGame.setOnAction(e -> deleteGame());
        if (btnAddPlatform != null) btnAddPlatform.setOnAction(e -> addPlatform());
        if (btnEditPlatform != null) btnEditPlatform.setOnAction(e -> editPlatform());
        if (btnDeletePlatform != null) btnDeletePlatform.setOnAction(e -> deletePlatform());

        // logout
        if (btnLogout != null) btnLogout.setOnAction(e -> logout());

        // Forcer les tables à s'étirer et adapter les colonnes
        if (gamesTable != null) {
            gamesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            gamesTable.setMaxHeight(Double.MAX_VALUE);
            gamesTable.setFixedCellSize(56); // ajuste la hauteur des lignes si besoin
        }
        if (usersTable != null) {
            usersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            usersTable.setMaxHeight(Double.MAX_VALUE);
            usersTable.setFixedCellSize(48);
        }
        if (platformsTable != null) {
            platformsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            platformsTable.setMaxHeight(Double.MAX_VALUE);
            platformsTable.setFixedCellSize(48);
        }
        if (collectionsTable != null) {
            collectionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            collectionsTable.setMaxHeight(Double.MAX_VALUE);
            collectionsTable.setFixedCellSize(48);
        }

        showUsersSection();
    }

    // ---------------- USERS ----------------
    private void setupUserTable() {
        colUserId.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getId()).asObject());
        colUsername.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getUsername()));
        colPassword.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getPassword()));
        colRole.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getRole()));
        usersTable.setItems(usersList);
    }

    private void loadUsers() {
        usersList.clear();
        List<User> all = UserService.getAllUsers();
        if (all != null) usersList.addAll(all);
    }

    private void deleteUser() {
        User u = usersTable.getSelectionModel().getSelectedItem();
        if (u == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer l'utilisateur \"" + u.getUsername() + "\" ?", ButtonType.OK, ButtonType.CANCEL);
        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                UserService.deleteUser(u.getId());
                loadUsers();
                loadCollections();
            }
        });
    }

    // ---------------- GAMES ----------------
    private void setupGameTable() {
        colGameId.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getId()).asObject());
        colGameName.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getTitle()));
        colGameYear.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getYear()));
        colGamePlatform.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getPlatformName()));
        colGameImage.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getImagePath()));

        // keep image column fixed width and show preview without expanding window
        colGameImage.setPrefWidth(90);
        colGameImage.setMinWidth(90);
        colGameImage.setMaxWidth(90);

        // optional: fix row height so images don't expand the whole window
        gamesTable.setFixedCellSize(70);

        colGameImage.setCellFactory(col -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitWidth(80);
                imageView.setFitHeight(60);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.getStyleClass().add("image-view");
            }
            @Override
            protected void updateItem(String path, boolean empty) {
                super.updateItem(path, empty);
                if (empty || path == null || path.isBlank()) {
                    setGraphic(null);
                } else {
                    File f = new File(path);
                    if (f.exists()) {
                        try {
                            imageView.setImage(new Image(f.toURI().toString(), 80, 60, true, true));
                            setGraphic(imageView);
                        } catch (Exception ex) {
                            setGraphic(null);
                        }
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        gamesTable.setItems(gamesList);
    }

    private void loadGames() {
        gamesList.clear();
        List<Game> all = GameService.getAllGames();
        if (all != null) gamesList.addAll(all);
    }

    private void addGame() {
        Stage dialog = new Stage();
        VBox form = new VBox(10);

        Label titleLabel = new Label("Ajouter un Jeu");
        titleLabel.getStyleClass().add("title");

        TextField titleField = new TextField();
        titleField.setPromptText("Nom du jeu");
        titleField.getStyleClass().add("text-field");

        TextField yearField = new TextField();
        yearField.setPromptText("Année");
        yearField.getStyleClass().add("text-field");

        List<Platform> allPlatforms = PlatformService.getAllPlatforms();
        ObservableList<Platform> platformOptions = FXCollections.observableArrayList();
        if (allPlatforms != null) platformOptions.addAll(allPlatforms);
        ComboBox<Platform> platformCombo = new ComboBox<>(platformOptions);
        platformCombo.setPromptText("Sélectionner une plateforme");
        platformCombo.getStyleClass().add("combo-box");
        platformCombo.setConverter(new StringConverter<>() {
            @Override public String toString(Platform p) { return p == null ? "" : p.getName(); }
            @Override public Platform fromString(String string) { return null; }
        });

        Label imageLabel = new Label("Aucune image sélectionnée");
        imageLabel.getStyleClass().add("info-label");
        Button chooseImageBtn = new Button("Choisir une image");
        chooseImageBtn.getStyleClass().add("button");
        final File[] selectedImage = new File[1];
        chooseImageBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
            File file = fc.showOpenDialog(dialog);
            if (file != null) {
                selectedImage[0] = file;
                imageLabel.setText("Image : " + file.getName());
            }
        });

        Button saveBtn = new Button("Enregistrer");
        saveBtn.getStyleClass().add("button");
        saveBtn.setOnAction(e -> {
            String title = titleField.getText();
            if (title == null || title.isBlank()) return;
            int year = 0;
            try { year = Integer.parseInt(yearField.getText().trim()); } catch (Exception ignored) {}
            Platform selectedPlatform = platformCombo.getValue();
            if (selectedPlatform == null) return;
            String imagePath = (selectedImage[0] != null) ? selectedImage[0].getAbsolutePath() : null;
            GameService.addGame(title, year, selectedPlatform.getName(), imagePath);
            loadGames();
            dialog.close();
        });

        form.getChildren().addAll(titleLabel, titleField, yearField, platformCombo, chooseImageBtn, imageLabel, saveBtn);

        Scene scene = new Scene(form, 480, 380);
        if (SceneManager.class.getResource("/style.css") != null) {
            scene.getStylesheets().add(SceneManager.class.getResource("/style.css").toExternalForm());
        }
        dialog.setScene(scene);
        dialog.show();
    }

    private void editGame() {
        Game g = gamesTable.getSelectionModel().getSelectedItem();
        if (g == null) {
            new Alert(Alert.AlertType.WARNING, "Aucun jeu sélectionné.", ButtonType.OK).showAndWait();
            return;
        }

        Stage dialog = new Stage();
        VBox form = new VBox(10);

        Label titleLabel = new Label("Modifier Jeu");
        titleLabel.getStyleClass().add("title");

        TextField titleField = new TextField(g.getTitle());
        titleField.getStyleClass().add("text-field");
        TextField yearField = new TextField(String.valueOf(g.getYear()));
        yearField.getStyleClass().add("text-field");

        List<Platform> allPlatforms = PlatformService.getAllPlatforms();
        ObservableList<Platform> platformOptions = FXCollections.observableArrayList();
        if (allPlatforms != null) platformOptions.addAll(allPlatforms);
        ComboBox<Platform> platformCombo = new ComboBox<>(platformOptions);
        platformCombo.getStyleClass().add("combo-box");
        platformCombo.setConverter(new StringConverter<>() {
            @Override public String toString(Platform p) { return p == null ? "" : p.getName(); }
            @Override public Platform fromString(String string) { return null; }
        });
        for (Platform p : platformOptions) {
            if (p.getName().equals(g.getPlatformName())) {
                platformCombo.setValue(p);
                break;
            }
        }

        Label imageLabel = new Label("Image actuelle : " + (g.getImagePath() == null ? "" : g.getImagePath()));
        imageLabel.getStyleClass().add("info-label");
        Button chooseImageBtn = new Button("Changer l’image");
        chooseImageBtn.getStyleClass().add("button");
        final File[] selectedImage = new File[1];
        chooseImageBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
            File file = fc.showOpenDialog(dialog);
            if (file != null) {
                selectedImage[0] = file;
                imageLabel.setText("Nouvelle image : " + file.getName());
            }
        });

        Button saveBtn = new Button("Enregistrer");
        saveBtn.getStyleClass().add("button");
        saveBtn.setOnAction(e -> {
            String newTitle = titleField.getText();
            if (newTitle == null || newTitle.trim().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Veuillez saisir le nom du jeu.", ButtonType.OK).showAndWait();
                return;
            }
            int newYear;
            try { newYear = Integer.parseInt(yearField.getText().trim()); } catch (Exception ex) {
                new Alert(Alert.AlertType.WARNING, "L'année doit être un nombre valide.", ButtonType.OK).showAndWait();
                return;
            }
            Platform selectedPlatform = platformCombo.getValue();
            if (selectedPlatform == null) {
                new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une plateforme.", ButtonType.OK).showAndWait();
                return;
            }
            g.setTitle(newTitle);
            g.setYear(newYear);
            g.setPlatformName(selectedPlatform.getName());
            if (selectedImage[0] != null) g.setImagePath(selectedImage[0].getAbsolutePath());
            GameService.updateGame(g);
            loadGames();
            dialog.close();
        });

        form.getChildren().addAll(titleLabel, titleField, yearField, platformCombo, chooseImageBtn, imageLabel, saveBtn);

        Scene scene = new Scene(form, 480, 380);
        if (SceneManager.class.getResource("/style.css") != null) {
            scene.getStylesheets().add(SceneManager.class.getResource("/style.css").toExternalForm());
        }
        dialog.setScene(scene);
        dialog.show();
    }

    private void deleteGame() {
        Game g = gamesTable.getSelectionModel().getSelectedItem();
        if (g == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer le jeu \"" + g.getTitle() + "\" ?", ButtonType.OK, ButtonType.CANCEL);
        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                GameService.deleteGameAdmin(g.getId());
                loadGames();
                loadCollections();
            }
        });
    }

    // ---------------- PLATFORMS ----------------
    private void setupPlatformTable() {
        colPlatformId.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getId()).asObject());
        colPlatformName.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getName()));
        platformsTable.setItems(platformsList);
    }

    private void loadPlatforms() {
        platformsList.clear();
        List<Platform> all = PlatformService.getAllPlatforms();
        if (all != null) platformsList.addAll(all);
    }

    private void addPlatform() {
        Stage dialog = new Stage();
        VBox form = new VBox(10);

        Label title = new Label("Ajouter une Plateforme");
        title.getStyleClass().add("title");

        TextField nameField = new TextField();
        nameField.setPromptText("Nom de la plateforme");
        nameField.getStyleClass().add("text-field");

        Button saveBtn = new Button("Enregistrer");
        saveBtn.getStyleClass().add("button");
        saveBtn.setOnAction(e -> {
            String name = nameField.getText();
            if (name == null || name.trim().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Veuillez saisir un nom de plateforme.", ButtonType.OK).showAndWait();
                return;
            }
            PlatformService.addPlatform(name);
            loadPlatforms();
            dialog.close();
        });

        form.getChildren().addAll(title, nameField, saveBtn);

        Scene scene = new Scene(form, 320, 160);
        if (SceneManager.class.getResource("/style.css") != null) {
            scene.getStylesheets().add(SceneManager.class.getResource("/style.css").toExternalForm());
        }
        dialog.setScene(scene);
        dialog.show();
    }

    private void editPlatform() {
        Platform p = platformsTable.getSelectionModel().getSelectedItem();
        if (p == null) return;
        Stage dialog = new Stage();
        VBox form = new VBox(10);

        Label title = new Label("Modifier Plateforme");
        title.getStyleClass().add("title");

        TextField nameField = new TextField(p.getName());
        nameField.getStyleClass().add("text-field");
        Button saveBtn = new Button("Enregistrer");
        saveBtn.getStyleClass().add("button");
        saveBtn.setOnAction(e -> {
            String newName = nameField.getText();
            if (newName == null || newName.trim().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Veuillez saisir un nom de plateforme.", ButtonType.OK).showAndWait();
                return;
            }
            PlatformService.updatePlatform(p.getId(), newName);
            loadPlatforms();
            dialog.close();
        });

        form.getChildren().addAll(title, nameField, saveBtn);
        Scene scene = new Scene(form, 320, 160);
        if (SceneManager.class.getResource("/style.css") != null) {
            scene.getStylesheets().add(SceneManager.class.getResource("/style.css").toExternalForm());
        }
        dialog.setScene(scene);
        dialog.show();
    }

    private void deletePlatform() {
        Platform p = platformsTable.getSelectionModel().getSelectedItem();
        if (p == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer la plateforme \"" + p.getName() + "\" ?", ButtonType.OK, ButtonType.CANCEL);
        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                PlatformService.deletePlatform(p.getId());
                loadPlatforms();
            }
        });
    }

    // ---------------- COLLECTIONS ----------------
    private void setupCollectionTable() {
        colCollectionUser.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getUsername()));
        colCollectionGame.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getGameTitle()));
        collectionsTable.setItems(collectionsList);
    }

    private void loadCollections() {
        collectionsList.clear();

        try {
            List<String> raw = GameService.getAllCollections();
            if (raw != null) {
                loadUsers();
                for (String s : raw) {
                    if (s == null || s.trim().isEmpty()) continue;
                    String user = s;
                    String game = "";
                    if (s.contains("->")) {
                        String[] parts = s.split("->", 2);
                        user = parts[0].trim();
                        game = parts[1].trim();
                    } else if (s.contains(":")) {
                        String[] parts = s.split(":", 2);
                        user = parts[0].trim();
                        game = parts[1].trim();
                    } else if (s.contains("-")) {
                        String[] parts = s.split("-", 2);
                        user = parts[0].trim();
                        game = parts[1].trim();
                    } else if (s.contains("|")) {
                        String[] parts = s.split("\\|", 2);
                        user = parts[0].trim();
                        game = parts[1].trim();
                    } else {
                        String[] parts = s.split("\\s+", 2);
                        if (parts.length == 2) {
                            user = parts[0].trim();
                            game = parts[1].trim();
                        } else {
                            game = "";
                        }
                    }
                    User found = findUserByUsername(user);
                    if (found != null && "user".equalsIgnoreCase(found.getRole())) {
                        collectionsList.add(new CollectionRow(found.getUsername(), game));
                    }
                }
            }
        } catch (NoSuchMethodError | Exception ignored) {
        }

        collectionsTable.setItems(collectionsList);
    }

    private User findUserByUsername(String username) {
        if (username == null) return null;
        for (User u : usersList) {
            if (username.equalsIgnoreCase(u.getUsername())) return u;
        }
        try {
            List<User> all = UserService.getAllUsers();
            if (all != null) {
                for (User u : all) {
                    if (username.equalsIgnoreCase(u.getUsername())) return u;
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    public static class CollectionRow {
        private final String username;
        private final String gameTitle;
        public CollectionRow(String username, String gameTitle) {
            this.username = username;
            this.gameTitle = gameTitle;
        }
        public String getUsername() { return username; }
        public String getGameTitle() { return gameTitle; }
    }

    // ---------------- SECTIONS ----------------
    @FXML private void showUsersSection() {
        usersSection.setVisible(true);
        usersSection.setManaged(true);

        gamesSection.setVisible(false);
        gamesSection.setManaged(false);
        platformsSection.setVisible(false);
        platformsSection.setManaged(false);
        collectionsSection.setVisible(false);
        collectionsSection.setManaged(false);
    }

    @FXML private void showGamesSection() {
        usersSection.setVisible(false);
        usersSection.setManaged(false);

        gamesSection.setVisible(true);
        gamesSection.setManaged(true);

        platformsSection.setVisible(false);
        platformsSection.setManaged(false);
        collectionsSection.setVisible(false);
        collectionsSection.setManaged(false);
    }

    @FXML private void showPlatformsSection() {
        usersSection.setVisible(false);
        usersSection.setManaged(false);

        gamesSection.setVisible(false);
        gamesSection.setManaged(false);

        platformsSection.setVisible(true);
        platformsSection.setManaged(true);

        collectionsSection.setVisible(false);
        collectionsSection.setManaged(false);
    }

    @FXML private void showCollectionsSection() {
        usersSection.setVisible(false);
        usersSection.setManaged(false);

        gamesSection.setVisible(false);
        gamesSection.setManaged(false);

        platformsSection.setVisible(false);
        platformsSection.setManaged(false);

        collectionsSection.setVisible(true);
        collectionsSection.setManaged(true);
    }

    // ---------------- LOGOUT ----------------
    private void logout() {
        try {
            Session.setUser(null);
        } catch (Exception ignored) {}
        SceneManager.switchTo("login.fxml");
    }
}
