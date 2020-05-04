package it.polimi.ingsw.server.controller;

public interface NewPlayerObserver {
    void onNicknameAndDateInsertion (String nickname, String birthday);
}
