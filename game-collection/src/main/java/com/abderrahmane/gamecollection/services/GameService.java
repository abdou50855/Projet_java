package com.abderrahmane.gamecollection.services;

import com.abderrahmane.gamecollection.Session;
import com.abderrahmane.gamecollection.config.Database;
import com.abderrahmane.gamecollection.dao.GameDAO;
import com.abderrahmane.gamecollection.models.Game;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameService {

    public static List<Game> getUserGames() {
        return GameDAO.getGamesForUser(Session.getUser().getId());
    }

    public static void addGame(String title, int year, String platformName, String imagePath) {
        int gameId = GameDAO.addGame(title, year, platformName, imagePath);
        if (gameId > 0) {
            GameDAO.addGameForUser(Session.getUser().getId(), gameId);
        }
    }

    // Supprimer un jeu de la collection de l'utilisateur (wrapper vers DAO)
    public static boolean deleteGameForUser(int userId, int gameId) {
        return GameDAO.deleteGameForUser(userId, gameId);
    }

    // Dupliquer + réassocier uniquement pour l'utilisateur courant
    public static boolean updateUserGame(int userId, int oldGameId, String newTitle, Integer newYear, String newPlatformName, String newImagePath) {
        return GameDAO.duplicateGameForUser(userId, oldGameId, newTitle, newYear, newPlatformName, newImagePath);
    }

    public static List<Game> getGamesByPlatform(String platformName) {
        return GameDAO.getGamesByPlatformForUser(Session.getUser().getId(), platformName);
    }

    public static List<Game> getAllGames() {
        return GameDAO.getAllGames();
    }

    public static boolean updateGame(Game game) {
        return GameDAO.updateGame(game);
    }

    public static boolean deleteGameAdmin(int id) {
        return GameDAO.deleteGame(id);
    }

    public static List<String> getAllCollections() {
        List<String> collections = new ArrayList<>();
        List<Game> allGames = getAllGames();
        // Simple lecture des collections via DAO query existante si besoin
        String sql = "SELECT u.username, g.title FROM user_games ug JOIN users u ON ug.user_id = u.id JOIN games g ON ug.game_id = g.id";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                collections.add(rs.getString("username") + " → " + rs.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return collections;
    }
}
