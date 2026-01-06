package com.abderrahmane.gamecollection.controllers;

import com.abderrahmane.gamecollection.models.User;
import com.abderrahmane.gamecollection.services.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class UserController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> roleColumn;

    private ObservableList<User> userList;

    @FXML
    public void initialize() {
        // 🔹 Utilisation des getters classiques
        idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        usernameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getUsername()));
        roleColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getRole()));

        loadUsers();
    }

    private void loadUsers() {
        userList = FXCollections.observableArrayList(UserService.getAllUsers());
        userTable.setItems(userList);
    }
}
