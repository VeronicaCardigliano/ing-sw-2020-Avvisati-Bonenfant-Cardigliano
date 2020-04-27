package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Model;

public interface StateObserver {

    void onStateUpdate (Model.State currState);

}
