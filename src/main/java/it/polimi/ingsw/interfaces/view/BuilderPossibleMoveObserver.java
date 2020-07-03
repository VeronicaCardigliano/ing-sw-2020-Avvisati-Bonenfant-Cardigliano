package it.polimi.ingsw.interfaces.view;

import it.polimi.ingsw.server.model.gameMap.Coordinates;
import java.util.Set;

/**
 * This interface is implemented to be informed about new possible destinations for a move, when it's the moment to move.
 */
public interface BuilderPossibleMoveObserver {
    void updatePossibleMoveDst (Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2);
}
