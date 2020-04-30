package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.gameMap.Cell;

public interface BuilderBuildObserver {
    void onBuilderBuild (Cell src, Cell dst, boolean buildDome);
}
