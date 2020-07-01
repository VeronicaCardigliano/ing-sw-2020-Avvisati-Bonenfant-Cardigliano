package it.polimi.ingsw.server.controller;

import java.util.Set;

/**
 * This interface is implemented to be informed about a choice of single player godCard or match godCards.
 */
public interface GodCardChoiceObserver {

    void onGodCardChoice (String nickname, String godCardName);
    void onMatchGodCardsChoice(String nickname, Set<String> godNames);
}
