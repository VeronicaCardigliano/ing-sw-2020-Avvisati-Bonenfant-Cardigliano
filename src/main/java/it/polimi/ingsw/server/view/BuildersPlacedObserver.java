package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.model.gameMap.Coordinates;

/**
 * This interface is implemented to be informed about the effective placement of a player builders if the result is true,
 * the failure of a placement attempt if the result is false
 */
public interface BuildersPlacedObserver {
    void onBuildersPlacedUpdate(String nickname, Coordinates positionBuilder1, Coordinates positionBuilder2, boolean result);
}
