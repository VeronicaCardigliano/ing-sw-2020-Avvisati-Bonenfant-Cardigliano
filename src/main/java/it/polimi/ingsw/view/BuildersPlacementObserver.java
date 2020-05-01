package it.polimi.ingsw.view;

import it.polimi.ingsw.model.gameMap.Coordinates;

public interface BuildersPlacementObserver {
    void onBuildersPlacementUpdate (Coordinates positionBuilder1, Coordinates positionBuilder2);
}
