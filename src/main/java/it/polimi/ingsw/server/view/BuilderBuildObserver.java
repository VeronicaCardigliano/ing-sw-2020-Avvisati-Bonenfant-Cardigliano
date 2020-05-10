package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.model.gameMap.Coordinates;

public interface BuilderBuildObserver {
    public void onBuilderBuild(String nickname, Coordinates src, Coordinates dst, Boolean dome);
}
