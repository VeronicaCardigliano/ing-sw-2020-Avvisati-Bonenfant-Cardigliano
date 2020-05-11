package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.model.gameMap.Coordinates;

import java.util.Set;

public interface BuilderPossibleMoveObserver {
    void updatePossibleMoveDst (Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2);
}
