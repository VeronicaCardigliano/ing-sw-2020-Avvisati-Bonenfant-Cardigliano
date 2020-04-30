package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.gameMap.Cell;

public interface BuilderMoveObserver {
    void onBuilderMove (Cell src, Cell dst);
}
