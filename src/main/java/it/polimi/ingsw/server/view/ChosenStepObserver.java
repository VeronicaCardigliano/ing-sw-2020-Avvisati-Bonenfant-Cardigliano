package it.polimi.ingsw.server.view;

public interface ChosenStepObserver {
    void onChosenStep(String nickname, String step, boolean result);
}
