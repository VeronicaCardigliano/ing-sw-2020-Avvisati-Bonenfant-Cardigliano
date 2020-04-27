package it.polimi.ingsw.view;

import java.util.List;

public interface BuilderPossibleBuildObserver {
    void updatePossibleBuildDst (List possibleDstBuilder1, List possibleDstBuilder2, List possibleDstBuilder1forDome,
                                 List possibleDstBuilder2forDome);
}
