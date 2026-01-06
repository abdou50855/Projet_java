package com.abderrahmane.gamecollection.dao;

import com.abderrahmane.gamecollection.config.Database;
import com.abderrahmane.gamecollection.models.User;

import java.sql.*;

public class UserDAO {

    // Signup (utilisé par SignupController)
    public static boolean signup(String username, String password) {
        try (Connection conn = Database.getConnection()) {

            // Vérifier si l'utilisateur existe déjà
            String checkQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();

            if (rs.getInt(1) > 0) {
                return false; // utilisateur déjà existant
            }

            // Insérer un utilisateur normal (role = user)
            String insertQuery = "INSERT INTO users (username, password, role) VALUES (?, ?, 'user')";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.executeUpdate();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Création d’un utilisateur (utilisé par l’admin)
    public static boolean createUser(String username, String password, String role) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            return false; // username déjà utilisé
        }
    }

    // Login (retourne un User COMPLET avec role)
    public static User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // login incorrect
    }

    // Récupérer un utilisateur par ID
    public static User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Récupérer tous les utilisateurs (pour l’admin)
    public static ResultSet getAllUsers() {
        String sql = "SELECT * FROM users";

        try {
            Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            return stmt.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Modifier un utilisateur
    public static boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, role = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setInt(4, user.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Supprimer un utilisateur
    public static boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
