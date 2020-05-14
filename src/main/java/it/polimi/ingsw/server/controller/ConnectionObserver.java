package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.view.VirtualView;

import java.io.IOException;

public interface ConnectionObserver {
    void onConnection(VirtualView view) throws IOException;
}
