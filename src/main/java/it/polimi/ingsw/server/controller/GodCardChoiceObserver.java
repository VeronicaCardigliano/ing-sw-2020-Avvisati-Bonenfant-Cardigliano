package it.polimi.ingsw.server.controller;

import java.util.Set;

public interface GodCardChoiceObserver {

    void onGodCardChoice (String nickname, String godCardName);
    void onMatchGodCardsChoice(String nickname, Set<String> godNames);
}
