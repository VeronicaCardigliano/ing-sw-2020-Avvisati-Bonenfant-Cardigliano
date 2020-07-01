package it.polimi.ingsw.server.view;

/**
 * This interface is implemented to be informed of the actual choice of the start player. If false, the start player
 * was not chosen correctly.
 */
public interface StartPlayerSetObserver {
    void onStartPlayerSet(String nickname, boolean result);
}
