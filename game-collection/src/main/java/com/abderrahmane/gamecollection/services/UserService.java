package com.abderrahmane.gamecollection.services;

import com.abderrahmane.gamecollection.config.Database;
import com.abderrahmane.gamecollection.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    // 🔹 Authentification (login)
    public static User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username=? AND password=?";
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // si non trouvé
    }

    // 🔹 Inscription (signup)
    public static boolean signup(String username, String password, String role) {
        String sql = "INSERT INTO users(username, password, role) VALUES(?,?,?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);

            return stmt.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            // cas où username est déjà utilisé
            System.out.println("❌ Nom d'utilisateur déjà pris");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 🔹 Récupérer tous les utilisateurs (admin)
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
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

    // 🔹 Mettre à jour un utilisateur
    public static boolean updateUser(User user) {
        String sql = "UPDATE users SET username=?, password=?, role=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setInt(4, user.getId());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 🔹 Supprimer un utilisateur
    public static boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
