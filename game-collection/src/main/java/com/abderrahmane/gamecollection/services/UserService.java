package com.abderrahmane.gamecollection.services;

import com.abderrahmane.gamecollection.dao.UserDAO;
import com.abderrahmane.gamecollection.models.User;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    // Authentification (login)
    public static User login(String username, String password) {
        return UserDAO.login(username, password);
    }

    // Inscription (signup) avec rôle
    public static boolean signup(String username, String password, String role) {
        // Si le rôle est "user", on peut utiliser UserDAO.signup
        if ("user".equalsIgnoreCase(role)) {
            return UserDAO.signup(username, password);
        }
        // Sinon, on utilise createUser pour permettre à l’admin de créer un compte avec rôle spécifique
        return UserDAO.createUser(username, password, role);
    }

    // Création d’un utilisateur (admin)
    public static boolean createUser(String username, String password, String role) {
        return UserDAO.createUser(username, password, role);
    }

    // Récupérer un utilisateur par ID
    public static User getUserById(int id) {
        return UserDAO.getUserById(id);
    }

    // Récupérer tous les utilisateurs (admin)
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            ResultSet rs = UserDAO.getAllUsers();
            while (rs != null && rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    // Mettre à jour un utilisateur
    public static boolean updateUser(User user) {
        return UserDAO.updateUser(user);
    }

    // Supprimer un utilisateur
    public static boolean deleteUser(int id) {
        return UserDAO.deleteUser(id);
    }
}
