package it.polimi.ingsw.server.view;

import java.util.Set;

public interface BuilderPossibleMoveObserver {
    void updatePossibleMoveDst (String nickname, Set possibleDstBuilder1, Set possibleDstBuilder2);
}
