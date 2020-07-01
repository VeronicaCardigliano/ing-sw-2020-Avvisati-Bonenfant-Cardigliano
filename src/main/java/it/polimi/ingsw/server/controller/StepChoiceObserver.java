package it.polimi.ingsw.server.controller;

/**
 * This interface is implemented to be informed about a step choice.
 */
public interface StepChoiceObserver {
    void onStepChoice(String player, String step);
}
