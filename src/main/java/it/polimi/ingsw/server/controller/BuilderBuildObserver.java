package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.gameMap.Coordinates;

/**
 * This interface is implemented to be informed about a new build from src to dst, of a dome or not.
 */
public interface BuilderBuildObserver {
    void onBuilderBuild(String nickname, Coordinates src, Coordinates dst, boolean buildDome);
}
