package it.polimi.ingsw.server.view;

import java.util.Set;

public interface BuilderPossibleBuildObserver {
    void updatePossibleBuildDst (String nickname, Set possibleDstBuilder1, Set possibleDstBuilder2, Set possibleDstBuilder1forDome,
                                 Set possibleDstBuilder2forDome);
}
