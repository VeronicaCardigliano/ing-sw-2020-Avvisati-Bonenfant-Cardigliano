package it.polimi.ingsw.server.view;

import java.util.Set;

public interface GodChoiceObserver {
    void onGodCardAssigned(String nickname, String card, boolean result);
    void onMatchGodCardsAssigned(Set<String> godCardsToUse, boolean result);
}
