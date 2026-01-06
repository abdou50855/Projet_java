package com.abderrahmane.gamecollection.dao;

import com.abderrahmane.gamecollection.config.Database;
import com.abderrahmane.gamecollection.models.Game;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameDAO {

    public static List<Game> getAllGames() {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT g.id, g.title, g.release_year, p.name AS platform, g.image_path " +
                "FROM games g JOIN platforms p ON g.platform_id = p.id";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                games.add(new Game(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getInt("release_year"),
                        rs.getString("platform"),
                        rs.getString("image_path")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return games;
    }

    public static int addGame(String title, int year, String platformName, String imagePath) {
        String sql = "INSERT INTO games (title, release_year, platform_id, image_path) " +
                "VALUES (?, ?, (SELECT id FROM platforms WHERE name = ?), ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, title);
            stmt.setInt(2, year);
            stmt.setString(3, platformName);
            stmt.setString(4, imagePath);
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean updateGame(Game game) {
        String sql = "UPDATE games SET title = ?, release_year = ?, " +
                "platform_id = (SELECT id FROM platforms WHERE name = ?), image_path = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, game.getTitle());
            stmt.setInt(2, game.getYear());
            stmt.setString(3, game.getPlatformName());
            stmt.setString(4, game.getImagePath());
            stmt.setInt(5, game.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteGame(int gameId) {
        String sql = "DELETE FROM games WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<Game> getGamesForUser(int userId) {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT g.id, g.title, g.release_year, p.name AS platform, g.image_path " +
                "FROM user_games ug JOIN games g ON ug.game_id = g.id " +
                "JOIN platforms p ON g.platform_id = p.id WHERE ug.user_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    games.add(new Game(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getInt("release_year"),
                            rs.getString("platform"),
                            rs.getString("image_path")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return games;
    }

    public static List<Game> getGamesByPlatformForUser(int userId, String platformName) {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT g.id, g.title, g.release_year, p.name AS platform, g.image_path " +
                "FROM user_games ug JOIN games g ON ug.game_id = g.id " +
                "JOIN platforms p ON g.platform_id = p.id WHERE ug.user_id = ? AND p.name = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, platformName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    games.add(new Game(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getInt("release_year"),
                            rs.getString("platform"),
                            rs.getString("image_path")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return games;
    }

    public static boolean addGameForUser(int userId, int gameId) {
        String sql = "INSERT INTO user_games (user_id, game_id) VALUES (?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, gameId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteGameForUser(int userId, int gameId) {
        String sql = "DELETE FROM user_games WHERE user_id = ? AND game_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, gameId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Duplique un jeu dans la table games et met à jour user_games pour l'utilisateur donné.
     * Retourne true si succès.
     */
    public static boolean duplicateGameForUser(int userId, int oldGameId, String newTitle, Integer newYear, String newPlatformName, String newImagePath) {
        String insertGameSql = "INSERT INTO games(title, release_year, platform_id, image_path) " +
                "VALUES(?, ?, (SELECT id FROM platforms WHERE name = ?), ?)";
        String updateUserGamesSql = "UPDATE user_games SET game_id = ? WHERE user_id = ? AND game_id = ?";

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement insertStmt = conn.prepareStatement(insertGameSql, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, newTitle);
                if (newYear == null) insertStmt.setNull(2, Types.INTEGER); else insertStmt.setInt(2, newYear);
                insertStmt.setString(3, newPlatformName);
                insertStmt.setString(4, newImagePath);
                int affected = insertStmt.executeUpdate();
                if (affected == 0) { conn.rollback(); return false; }

                try (ResultSet rs = insertStmt.getGeneratedKeys()) {
                    if (!rs.next()) { conn.rollback(); return false; }
                    int newGameId = rs.getInt(1);

                    try (PreparedStatement updateStmt = conn.prepareStatement(updateUserGamesSql)) {
                        updateStmt.setInt(1, newGameId);
                        updateStmt.setInt(2, userId);
                        updateStmt.setInt(3, oldGameId);
                        int updated = updateStmt.executeUpdate();
                        if (updated == 0) { conn.rollback(); return false; }
                        conn.commit();
                        return true;
                    }
                }
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
