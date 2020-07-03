package it.polimi.ingsw.interfaces.view;

/**
 * This interface is implemented to be informed about the result of a stepChoice, if result is true the step has been
 * correctly chosen, otherwise the class is notified about a failure in step choice.
 */
public interface ChosenStepObserver {
    void onChosenStep(String nickname, String step, boolean result);
}
