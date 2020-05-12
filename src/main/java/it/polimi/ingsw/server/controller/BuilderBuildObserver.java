package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.gameMap.Coordinates;

public interface BuilderBuildObserver {
    void onBuilderBuild(String nickname, Coordinates src, Coordinates dst, boolean buildDome);
}
