package com.abderrahmane.gamecollection.services;

import com.abderrahmane.gamecollection.config.Database;
import com.abderrahmane.gamecollection.models.Platform;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlatformService {

    //  Récupérer toutes les plateformes
    public static List<Platform> getAllPlatforms() {
        List<Platform> platforms = new ArrayList<>();
        String sql = "SELECT * FROM platforms";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                platforms.add(new Platform(
                        rs.getInt("id"),
                        rs.getString("name")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return platforms;
    }

    //  Ajouter une plateforme
    public static boolean addPlatform(String name) {
        String sql = "INSERT INTO platforms(name) VALUES(?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //  Mettre à jour une plateforme
    public static boolean updatePlatform(int id, String name) {
        String sql = "UPDATE platforms SET name=? WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //  Supprimer une plateforme
    public static boolean deletePlatform(int id) {
        String sql = "DELETE FROM platforms WHERE id=?";
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
