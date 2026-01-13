package com.abderrahmane.gamecollection.services;

import com.abderrahmane.gamecollection.dao.PlatformDAO;
import com.abderrahmane.gamecollection.models.Platform;

import java.util.List;

public class PlatformService {

    // Récupérer toutes les plateformes
    public static List<Platform> getAllPlatforms() {
        return PlatformDAO.getAllPlatforms();
    }

    // Ajouter une plateforme
    public static boolean addPlatform(String name) {
        return PlatformDAO.addPlatform(name);
    }

    // Mettre à jour une plateforme
    public static boolean updatePlatform(int id, String name) {
        return PlatformDAO.updatePlatform(id, name);
    }

    // Supprimer une plateforme
    public static boolean deletePlatform(int id) {
        return PlatformDAO.deletePlatform(id);
    }
}
