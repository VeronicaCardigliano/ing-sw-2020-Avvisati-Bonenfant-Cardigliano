package it.polimi.ingsw.server.controller;

/**
 * This interface is implemented to be informed about a new player insertion.
 */
public interface NewPlayerObserver {
    void onNicknameAndDateInsertion (String nickname, String birthday);
}
