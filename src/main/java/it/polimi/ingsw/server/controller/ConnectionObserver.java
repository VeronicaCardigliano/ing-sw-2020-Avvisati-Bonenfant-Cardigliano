package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.view.VirtualView;

/**
 * This interface is implemented to be informed about a new connection, disconnection or earlyDisconnection.
 * An earlyDisconnection means that a client disconnected before signing up with a nickname
 */
public interface ConnectionObserver {
    void onConnection(VirtualView view);
    void onDisconnection(String nickname);
    void onEarlyDisconnection(VirtualView view);
}
