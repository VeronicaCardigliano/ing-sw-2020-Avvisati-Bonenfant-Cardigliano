package it.polimi.ingsw.interfaces.view;

/**
 * This interface is implemented to be informed of the actual insertion of a new player. If result is false,
 * the class is informed of a wrong attempt of player insertion, due for example to a wrong nickname or date.
 */
public interface PlayerAddedObserver {
    void onPlayerAdded(String nickname, boolean result);
}
