package com.abderrahmane.gamecollection.dao;

import com.abderrahmane.gamecollection.config.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserGamesDAO {

    public static List<String> getAllUserGames() {
        List<String> list = new ArrayList<>();

        String sql = """
            SELECT u.username, g.title AS game_name
            FROM user_games ug
            JOIN users u ON ug.user_id = u.id
            JOIN games g ON ug.game_id = g.id
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("username") + " → " + rs.getString("game_name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
