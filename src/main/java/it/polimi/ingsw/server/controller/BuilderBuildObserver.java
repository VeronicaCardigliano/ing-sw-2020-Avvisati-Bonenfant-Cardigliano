package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.gameMap.Cell;

public interface BuilderBuildObserver {
    void onBuilderBuild (Cell src, Cell dst, boolean buildDome);
}
