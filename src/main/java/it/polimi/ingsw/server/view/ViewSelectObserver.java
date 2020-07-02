package it.polimi.ingsw.server.view;

/**
 * Interface implemented to be informed of a new current Client, identified by his nickname.
 * It allows to have a selected View which represents the current player one.
 */
public interface ViewSelectObserver {
    void onViewSelect(String nickname);
}
