package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.model.Model;

public interface StateObserver {

    void onStateUpdate (Model.State currState);

}
