package it.polimi.ingsw.model;


import it.polimi.ingsw.model.gameMap.Cell;
import it.polimi.ingsw.model.gameMap.IslandBoard;

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
        if(type == EventType.MOVE)
            return IslandBoard.heightDifference(srcCell, dstCell);
        else
            return 0;
    }

    public int getDstBlockHeight() {
        return dstCell.getHeight();
    }

    public boolean builtDome() {
        return type == EventType.BUILD_DOME;
    }

    public int getBuiltBlockHeight() {
        if(type == EventType.BUILD)
            return dstCell.getHeight();
        return -1;
    }


}
