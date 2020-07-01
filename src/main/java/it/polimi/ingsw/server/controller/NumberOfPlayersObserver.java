package it.polimi.ingsw.server.controller;

/**
 * This interface is implemented to be informed about number of players insertion.
 */
public interface NumberOfPlayersObserver {
    void onNumberInsertion (int num);
}
