package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.view.VirtualView;

public interface DisconnectionObserver {
    void onDisconnection(String nickname);
    void onEarlyDisconnection(VirtualView view);
}
