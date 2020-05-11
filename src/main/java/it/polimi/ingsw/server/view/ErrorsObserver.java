package it.polimi.ingsw.server.view;

public interface ErrorsObserver {
    void onWrongInsertionUpdate(String error);
    void onWrongNumberInsertion();

}