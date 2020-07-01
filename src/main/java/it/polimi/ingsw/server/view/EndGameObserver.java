package it.polimi.ingsw.server.view;

/**
 * This interface is implemented to be informed of the end of the game and the winner.
 */
public interface EndGameObserver {
    void onEndGameUpdate (String winnerNickname);
}
