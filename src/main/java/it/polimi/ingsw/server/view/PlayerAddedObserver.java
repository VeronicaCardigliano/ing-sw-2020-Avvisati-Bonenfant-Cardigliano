package it.polimi.ingsw.server.view;

public interface PlayerAddedObserver {
    void onPlayerAdded(String nickname, boolean result);
}
