package it.polimi.ingsw.interfaces.view;

/**
 * This interface is implemented to be informed of the actual assignment of builders color for a player,
 * if the result is false the class is notified about a wrong attempt of color choice.
 */
public interface ColorAssignmentObserver {
    void onColorAssigned (String nickname, String color, boolean result);
}
