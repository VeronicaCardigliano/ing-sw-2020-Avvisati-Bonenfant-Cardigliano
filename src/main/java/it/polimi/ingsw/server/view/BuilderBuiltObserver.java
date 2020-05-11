package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.model.gameMap.Coordinates;

public interface BuilderBuiltObserver {
    void onBuilderBuild(String nickname, Coordinates src, Coordinates dst, boolean dome, boolean result);
}
