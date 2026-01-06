package com.abderrahmane.gamecollection.controllers;

import com.abderrahmane.gamecollection.SceneManager;
import com.abderrahmane.gamecollection.Session;
import com.abderrahmane.gamecollection.models.Game;
import com.abderrahmane.gamecollection.models.Platform;
import com.abderrahmane.gamecollection.services.GameService;
import com.abderrahmane.gamecollection.services.PlatformService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class GameController {

    @FXML private TableView<Game> gameTable;
    @FXML private TableColumn<Game, String> titleColumn;
    @FXML private TableColumn<Game, Integer> yearColumn;
    @FXML private TableColumn<Game, String> platformColumn;
    @FXML private TableColumn<Game, String> imageColumn;

    @FXML private ComboBox<Platform> platformFilter;
    @FXML private ComboBox<Platform> platformField;

    @FXML private TextField titleField;
    @FXML private TextField yearField;

    @FXML private Button addBtn;
    @FXML private Button editBtn;
    @FXML private Button deleteBtn;
    @FXML private Button resetFilterBtn;
    @FXML private Button homeBtn;
    @FXML private Button chooseImageBtn;

    @FXML private ImageView previewImage;

    @FXML private Button logoutBtn;
    @FXML private Label welcomeLabel;

    private File selectedImageFile;
    private ObservableList<Game> gameList;

    // état d'édition inline
    private boolean editMode = false;
    private int editingGameId = -1;
    private String originalImagePath = null;

    @FXML
    public void initialize() {
        if (Session.getUser() == null) {
            SceneManager.switchTo("login.fxml");
            return;
        }

        if (welcomeLabel != null) welcomeLabel.setText("Bienvenue, " + Session.getUser().getUsername());

        if (titleColumn != null) titleColumn.setCellValueFactory(data -> data.getValue().titleProperty());
        if (yearColumn != null) yearColumn.setCellValueFactory(data -> data.getValue().yearProperty().asObject());
        if (platformColumn != null) platformColumn.setCellValueFactory(data -> data.getValue().platformNameProperty());
        if (imageColumn != null) imageColumn.setCellValueFactory(data -> data.getValue().imagePathProperty());

        if (imageColumn != null) {
            imageColumn.setCellFactory(col -> new TableCell<>() {
                private final ImageView imageView = new ImageView();
                {
                    imageView.setFitWidth(60);
                    imageView.setFitHeight(60);
                    imageView.setPreserveRatio(true);
                }
                @Override
                protected void updateItem(String path, boolean empty) {
                    super.updateItem(path, empty);
                    if (empty || path == null || path.isEmpty()) {
                        setGraphic(null);
                    } else {
                        File imgFile = new File(path);
                        if (imgFile.exists()) {
                            imageView.setImage(new Image(imgFile.toURI().toString()));
                            setGraphic(imageView);
                        } else {
                            setGraphic(null);
                        }
                    }
                }
            });
        }

        loadPlatforms();
        loadGames();

        if (addBtn != null) addBtn.setOnAction(e -> onAddOrSave());
        if (editBtn != null) editBtn.setOnAction(e -> enterEditMode());
        if (deleteBtn != null) deleteBtn.setOnAction(e -> deleteSelectedGame());
        if (resetFilterBtn != null) resetFilterBtn.setOnAction(e -> loadGames());
        if (platformFilter != null) platformFilter.setOnAction(e -> filterByPlatform());
        if (chooseImageBtn != null) chooseImageBtn.setOnAction(e -> chooseImage());
        if (homeBtn != null) homeBtn.setOnAction(e -> SceneManager.switchTo("home.fxml"));
        if (logoutBtn != null) {
            logoutBtn.setOnAction(e -> {
                Session.setUser(null);
                SceneManager.switchTo("home.fxml");
            });
        }

        if (gameTable != null && previewImage != null) {
            gameTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
                if (newV != null && newV.getImagePath() != null) {
                    File imgFile = new File(newV.getImagePath());
                    previewImage.setImage(imgFile.exists() ? new Image(imgFile.toURI().toString()) : null);
                } else {
                    previewImage.setImage(null);
                }
            });
        }

        if (addBtn != null) addBtn.setText("Ajouter");
    }

    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));

        File file = null;
        try {
            file = fileChooser.showOpenDialog(gameTable != null && gameTable.getScene() != null ? gameTable.getScene().getWindow() : null);
        } catch (Exception ignored) {}
        if (file != null) {
            selectedImageFile = file;
            if (previewImage != null) previewImage.setImage(new Image(file.toURI().toString()));
        }
    }

    private void loadPlatforms() {
        ObservableList<Platform> platforms = FXCollections.observableArrayList(PlatformService.getAllPlatforms());
        if (platformFilter != null) platformFilter.setItems(platforms);
        if (platformField != null) platformField.setItems(platforms);
    }

    private void loadGames() {
        gameList = FXCollections.observableArrayList(GameService.getUserGames());
        if (gameTable != null) gameTable.setItems(gameList);
    }

    private void filterByPlatform() {
        Platform selected = platformFilter != null ? platformFilter.getValue() : null;
        if (selected != null) {
            gameList = FXCollections.observableArrayList(GameService.getGamesByPlatform(selected.getName()));
            if (gameTable != null) gameTable.setItems(gameList);
        }
    }

    private void onAddOrSave() {
        if (!editMode) {
            addGame();
        } else {
            saveEditedGame();
        }
    }

    private void addGame() {
        String title = titleField != null ? titleField.getText() : "";
        String yearText = yearField != null ? yearField.getText() : "";
        Platform platform = platformField != null ? platformField.getValue() : null;

        if (title.isEmpty() || yearText.isEmpty() || platform == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        int year;
        try {
            year = Integer.parseInt(yearText.trim());
        } catch (NumberFormatException e) {
            showAlert("Erreur", "L'année doit être un nombre.");
            return;
        }

        String imagePath = null;
        if (selectedImageFile != null) {
            try {
                File destDir = new File("game_images/");
                if (!destDir.exists()) destDir.mkdirs();
                File destFile = new File(destDir, System.currentTimeMillis() + "_" + selectedImageFile.getName());
                Files.copy(selectedImageFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                imagePath = destFile.getAbsolutePath();
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Erreur", "Impossible d'enregistrer l'image.");
                return;
            }
        }

        GameService.addGame(title, year, platform.getName(), imagePath);
        loadGames();
        clearForm();
    }

    private void deleteSelectedGame() {
        Game selected = gameTable != null ? gameTable.getSelectionModel().getSelectedItem() : null;
        if (selected == null) {
            showAlert("Erreur", "Aucun jeu sélectionné.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation de suppression");
        confirmAlert.setHeaderText("Supprimer le jeu ?");
        confirmAlert.setContentText("Voulez-vous vraiment supprimer le jeu \"" + selected.getTitle() + "\" de votre collection ?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean ok = GameService.deleteGameForUser(Session.getUser().getId(), selected.getId());
                if (ok) {
                    loadGames();
                    showAlert("Succès", "Le jeu a été supprimé de votre collection.");
                    if (editMode && editingGameId == selected.getId()) exitEditMode();
                } else {
                    showAlert("Erreur", "Impossible de supprimer le jeu.");
                }
            }
        });
    }

    private void enterEditMode() {
        Game selected = gameTable != null ? gameTable.getSelectionModel().getSelectedItem() : null;
        if (selected == null) {
            showAlert("Erreur", "Aucun jeu sélectionné.");
            return;
        }

        // sécurité : vérifier que le jeu est bien dans la collection de l'utilisateur
        boolean owned = GameService.getUserGames().stream().anyMatch(g -> g.getId() == selected.getId());
        if (!owned) {
            showAlert("Erreur", "Vous ne pouvez modifier que les jeux de votre collection.");
            return;
        }

        editMode = true;
        editingGameId = selected.getId();
        originalImagePath = selected.getImagePath();
        selectedImageFile = null;

        if (titleField != null) titleField.setText(selected.getTitle());
        if (yearField != null) yearField.setText(String.valueOf(selected.getYear()));

        if (platformField != null && platformField.getItems() != null) {
            for (Platform p : platformField.getItems()) {
                if (p != null && p.getName().equals(selected.getPlatformName())) {
                    platformField.setValue(p);
                    break;
                }
            }
        }

        if (selected.getImagePath() != null && previewImage != null) {
            File imgFile = new File(selected.getImagePath());
            previewImage.setImage(imgFile.exists() ? new Image(imgFile.toURI().toString()) : null);
        } else if (previewImage != null) {
            previewImage.setImage(null);
        }

        if (addBtn != null) addBtn.setText("Enregistrer");
    }

    private void saveEditedGame() {
        String newTitle = titleField != null ? titleField.getText() : "";
        String yearText = yearField != null ? yearField.getText() : "";
        Platform platform = platformField != null ? platformField.getValue() : null;

        if (newTitle == null || newTitle.trim().isEmpty() || yearText == null || yearText.trim().isEmpty() || platform == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        int newYear;
        try {
            newYear = Integer.parseInt(yearText.trim());
        } catch (NumberFormatException e) {
            showAlert("Erreur", "L'année doit être un nombre.");
            return;
        }

        String imagePath = originalImagePath;
        if (selectedImageFile != null) {
            try {
                File destDir = new File("game_images/");
                if (!destDir.exists()) destDir.mkdirs();
                File destFile = new File(destDir, System.currentTimeMillis() + "_" + selectedImageFile.getName());
                Files.copy(selectedImageFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                imagePath = destFile.getAbsolutePath();
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Erreur", "Impossible d'enregistrer l'image.");
                return;
            }
        }

        boolean ok = GameService.updateUserGame(
                Session.getUser().getId(),
                editingGameId,
                newTitle,
                newYear,
                platform.getName(),
                imagePath
        );

        if (ok) {
            loadGames();
            exitEditMode();
            showAlert("Succès", "Jeu modifié dans votre collection.");
        } else {
            showAlert("Erreur", "Impossible de modifier le jeu.");
        }
    }

    private void exitEditMode() {
        editMode = false;
        editingGameId = -1;
        originalImagePath = null;
        selectedImageFile = null;
        clearForm();
        if (addBtn != null) addBtn.setText("Ajouter");
    }

    private void clearForm() {
        if (titleField != null) titleField.clear();
        if (yearField != null) yearField.clear();
        if (platformField != null) platformField.setValue(null);
        if (previewImage != null) previewImage.setImage(null);
        selectedImageFile = null;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
