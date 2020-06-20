package it.polimi.ingsw.client;

public interface SocketErrorObserver {
    void onConnectionRefused(String message);
    void onConnectionTimedOut(String message);
    void onUnknownHostError(String message);
}
