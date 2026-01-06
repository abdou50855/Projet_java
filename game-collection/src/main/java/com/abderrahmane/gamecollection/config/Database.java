package com.abderrahmane.gamecollection.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {

    private static String url;
    private static String user;
    private static String password;
    private static String driver;

    static {
        try (InputStream input = Database.class.getResourceAsStream("/database.properties")) {
            Properties props = new Properties();
            if (input == null) {
                throw new RuntimeException("❌ Fichier database.properties introuvable !");
            }
            props.load(input);

            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");
            driver = props.getProperty("db.driver");

            if (driver != null && !driver.isBlank()) {
                Class.forName(driver);
                System.out.println("✅ Driver MySQL chargé avec succès");
            }
        } catch (Exception e) {
            System.out.println("❌ Erreur lors du chargement de la configuration DB");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("❌ Erreur de connexion à MySQL");
            e.printStackTrace();
            return null;
        }
    }
}
