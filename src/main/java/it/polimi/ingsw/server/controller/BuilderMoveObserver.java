package it.polimi.ingsw.server.controller;


import it.polimi.ingsw.server.model.gameMap.Coordinates;

/**
 * This interface is implemented to be informed about a new move from src to dst.
 */
public interface BuilderMoveObserver {
    void onBuilderMove (String nickname, Coordinates src, Coordinates dst);
}
