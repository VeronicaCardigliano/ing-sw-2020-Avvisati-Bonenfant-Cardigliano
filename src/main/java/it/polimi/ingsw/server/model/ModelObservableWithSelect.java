package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.view.ViewSelectObserver;

public abstract class ModelObservableWithSelect extends ModelObservable{
    ViewSelectObserver viewSelectObserver;

    public void setViewSelectObserver(ViewSelectObserver observer) {
        viewSelectObserver = observer;
    }

    public void notifyViewSelection(String nickname) {
        if(viewSelectObserver != null)
            viewSelectObserver.onViewSelect(nickname);
        else
            System.out.println("view select observer is not set");
    }

}
