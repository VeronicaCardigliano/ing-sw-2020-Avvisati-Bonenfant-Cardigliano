package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.gameMap.Coordinates;

public interface BuilderBuildObserver {
    void onBuilderBuild(String player, Coordinates src, Coordinates dst, boolean buildDome);
}
