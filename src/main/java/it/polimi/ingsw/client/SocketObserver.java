package it.polimi.ingsw.client;

public interface SocketObserver {
    void onConnectionError(String message);
    void onDisconnection();
}
