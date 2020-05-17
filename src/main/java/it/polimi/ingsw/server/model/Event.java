package it.polimi.ingsw.server.model;


import it.polimi.ingsw.server.model.gameMap.Cell;
import it.polimi.ingsw.server.model.gameMap.Coordinates;
import it.polimi.ingsw.server.model.gameMap.IslandBoard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author thomas
 *
 * Immutable class representing an event on the board.
 * It can be either a MOVE or BUILD type of event.
 * Events are used by IslandBoard to check if a move is possible according to other GodCards' rules
 * or to check if a Player has won.
 */
public class Event {
    public enum EventType {
        MOVE, BUILD, BUILD_DOME
    }

    private final EventType type;

    private final Cell srcCell;
    private final Cell dstCell;


    public Event(EventType type, Cell srcCell, Cell dstCell) throws NullPointerException {
        if(type != null && srcCell != null && dstCell != null) {
            this.type = type;
            this.srcCell = srcCell;
            this.dstCell = dstCell;
        } else
            throw new NullPointerException("Event is incomplete");

    }

    public EventType getType() {
        return type;
    }

    public int heightDifference() {
            return IslandBoard.heightDifference(srcCell, dstCell);
    }

    public int getDstBlockHeight() {
        return dstCell.getHeight();
    }

    public boolean builtDome() {
        return type == EventType.BUILD_DOME;
    }

    public Cell getSrcCell() { return srcCell; }

    public Cell getDstCell() { return dstCell; }


}
