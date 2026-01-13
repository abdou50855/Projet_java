package com.abderrahmane.gamecollection.services;

import com.abderrahmane.gamecollection.dao.UserGamesDAO;

import java.util.List;

public class UserGamesService {

    public static List<String> getAllUserGames() {
        return UserGamesDAO.getAllUserGames();
    }
}
