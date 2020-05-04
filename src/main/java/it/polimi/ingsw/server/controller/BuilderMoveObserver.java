package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.gameMap.Cell;

public interface BuilderMoveObserver {
    void onBuilderMove (Cell src, Cell dst);
}
