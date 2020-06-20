package it.polimi.ingsw.client;

public interface SocketErrorObserver {
    void onConnectionError(String message);
}
