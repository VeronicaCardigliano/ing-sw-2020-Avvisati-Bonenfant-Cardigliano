package it.polimi.ingsw.client;

public interface ConnectionObserver {
    void onConnection(String ip, int port);
    void onConnection();
}
