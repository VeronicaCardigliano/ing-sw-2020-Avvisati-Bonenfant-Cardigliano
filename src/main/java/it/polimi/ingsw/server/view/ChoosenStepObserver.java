package it.polimi.ingsw.server.view;

public interface ChoosenStepObserver {
    void onChoosenStep(String nickname, String step, boolean result);
}
