package it.polimi.ingsw.client;

/**
 * Handles View connection and disconnection requests
 */
public interface ConnectionObserver {
    /**
     * Connection request handler
     */
    void onConnection(String server, int port);

    /**
     * Disconnection request handler
     */
    void onDisconnection();
}
