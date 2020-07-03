package it.polimi.ingsw.client;

/**
 * Class that handles a player disconnection event notification
 */
interface OpponentDisconnectionObserver {
    /**
     * opponent player disconnection handler
     * @param nickname player who disconnected
     */
    void onOpponentDisconnection(String nickname);
}