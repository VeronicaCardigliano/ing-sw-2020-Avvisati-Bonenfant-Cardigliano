package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.model.Model;

/**
 * This interface is implemented to be informed of a Model state change.
 */
public interface StateObserver {

    void onStateUpdate (Model.State currState);
}
