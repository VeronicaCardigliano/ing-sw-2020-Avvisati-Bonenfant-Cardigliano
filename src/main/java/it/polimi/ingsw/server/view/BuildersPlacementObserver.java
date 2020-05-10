package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.model.gameMap.Coordinates;

public interface BuildersPlacementObserver {
    void onBuildersPlacementUpdate (String nickname, Coordinates positionBuilder1, Coordinates positionBuilder2);
}
