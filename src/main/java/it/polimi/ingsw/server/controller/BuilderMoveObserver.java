package it.polimi.ingsw.server.controller;


import it.polimi.ingsw.server.model.gameMap.Coordinates;

public interface BuilderMoveObserver {
    void onBuilderMove (String nickname, Coordinates src, Coordinates dst);
}
