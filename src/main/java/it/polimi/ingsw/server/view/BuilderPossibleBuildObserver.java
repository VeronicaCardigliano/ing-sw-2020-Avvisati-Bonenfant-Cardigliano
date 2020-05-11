package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.model.gameMap.Coordinates;

import java.util.Set;

public interface BuilderPossibleBuildObserver {
    void updatePossibleBuildDst (String nickname, Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2,
                                 Set<Coordinates> possibleDstBuilder1forDome, Set<Coordinates> possibleDstBuilder2forDome);
}
