package it.polimi.ingsw.server.view;

/**
 * This interface is implemented to be informed whether a player loses but the match continues (when no one has won yet).
 */
public interface PlayerLoseObserver {
    void onLossUpdate (String nickname);
}
