package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.gameMap.Coordinates;

public interface BuilderSetupObserver {
    void onBuilderSetup(String player, Coordinates pos1, Coordinates pos2);
}
