package it.polimi.ingsw.view;

import java.util.List;
import java.util.Set;

public interface BuilderPossibleBuildObserver {
    void updatePossibleBuildDst (Set possibleDstBuilder1, Set possibleDstBuilder2, Set possibleDstBuilder1forDome,
                                 Set possibleDstBuilder2forDome);
}
