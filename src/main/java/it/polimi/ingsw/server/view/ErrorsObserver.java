package it.polimi.ingsw.server.view;

/**
 * This interface is implemented to be informed of generic errors of wrong insertion or wrong number of players insertion
 */
public interface ErrorsObserver {
    void onWrongInsertionUpdate(String error);
    void onWrongNumberInsertion();

}