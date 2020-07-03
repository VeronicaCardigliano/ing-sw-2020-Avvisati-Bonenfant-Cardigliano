package it.polimi.ingsw.interfaces.controller;

/**
 * This interface is implemented to be informed of start player set.
 */
public interface StartPlayerObserver {
    void onSetStartPlayer(String nickname, String startPlayer);
}
