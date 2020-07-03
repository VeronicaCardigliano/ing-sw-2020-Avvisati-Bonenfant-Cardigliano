package it.polimi.ingsw.interfaces.view;

import it.polimi.ingsw.server.model.gameMap.Coordinates;

/**
 * This interface is implemented to be informed about the effective movement of a builder, after a move or
 * after a push event (the builder may have been moved by another player).
 * After a move, the class which implements the interface can be also notified about a failure if result is false.
 */
public interface BuilderMovementObserver {
    void onBuilderMovement(String nickname, Coordinates src, Coordinates dst, boolean result);
    void onBuilderPushed(String nickname, Coordinates src, Coordinates dst);
}
