package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.model.gameMap.Coordinates;

import java.util.Set;

/**
 * This interface is implemented to be informed about new possible destinations for a build, when it's the moment to build.
 */
public interface BuilderPossibleBuildObserver {
    void updatePossibleBuildDst (Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2,
                                 Set<Coordinates> possibleDstBuilder1forDome, Set<Coordinates> possibleDstBuilder2forDome);
}
