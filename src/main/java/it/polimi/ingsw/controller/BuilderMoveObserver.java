package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Builder;
import it.polimi.ingsw.model.Cell;

public interface BuilderMoveObserver {
    void onBuilderMove (Cell src, Cell dst);
}
