package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.gameMap.Coordinates;

/**
 * This interface is implemented to be informed about a new builders placement.
 */
public interface BuilderSetupObserver {
    void onBuilderSetup(String nickname, Coordinates pos1, Coordinates pos2);
}
