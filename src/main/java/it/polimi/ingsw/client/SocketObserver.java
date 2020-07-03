package it.polimi.ingsw.client;

/**
 * Handles message relative to a connection error/disconnection
 */
public interface SocketObserver {
    /**
     * Handles a socket connection error (e.g. timeout)
     * @param message message describing the error
     */
    void onConnectionError(String message);

    /**
     * Handles socket disconnection event
     */
    void onDisconnection();
}
