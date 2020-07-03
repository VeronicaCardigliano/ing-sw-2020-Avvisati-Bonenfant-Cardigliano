package it.polimi.ingsw.interfaces.view;

import java.util.Set;
/**
 * This interface is implemented to be informed of the actual assignment if a player godCard or match godCards.
 * If the result is false the class is notified about a wrong attempt of godCard/godCards choice.
 */
public interface GodChoiceObserver {
    void onGodCardAssigned(String nickname, String card, boolean result);
    void onMatchGodCardsAssigned(Set<String> godCardsToUse, boolean result);
}
