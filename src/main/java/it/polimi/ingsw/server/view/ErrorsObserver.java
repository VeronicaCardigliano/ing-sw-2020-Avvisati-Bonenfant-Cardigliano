package it.polimi.ingsw.server.view;

public interface ErrorsObserver {
    void onWrongInsertionUpdate(String nickname, String error);
    void onWrongNumberInsertion();
    void onWrongPlayerInsertion(String nickname);
}