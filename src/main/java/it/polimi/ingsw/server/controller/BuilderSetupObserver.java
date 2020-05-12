package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.gameMap.Coordinates;

public interface BuilderSetupObserver {
    void onBuilderSetup(String nickname, Coordinates pos1, Coordinates pos2);
}
