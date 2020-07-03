package it.polimi.ingsw.interfaces.view;

/**
 * This interface is implemented to be informed of a turn change.
 */
public interface PlayerTurnObserver {
    void onPlayerTurn(String nickname);
}
