package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.model.gameMap.Coordinates;

public interface BuilderMovementObserver {
    void onBuilderMovement(String nickname, Coordinates src, Coordinates dst, boolean result);
    void onBuilderPushed(String nickname, Coordinates src, Coordinates dst);
}
