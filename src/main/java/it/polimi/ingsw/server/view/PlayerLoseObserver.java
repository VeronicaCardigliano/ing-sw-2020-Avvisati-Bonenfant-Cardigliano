package it.polimi.ingsw.server.view;

public interface PlayerLoseObserver {
    void onLossUpdate (String nickname, String currPlayer);
}
