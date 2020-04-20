package it.polimi.ingsw.model;


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
        MOVE, BUILD
    }

    private final EventType type;
    private final Cell srcCell;
    private final Cell dstCell;
    private final Cell buildCell;
    private final boolean buildDome;

    public Event(EventType type, Cell srcCell, Cell dstCell, Cell buildCell, boolean buildDome) throws IllegalArgumentException {
        if(type == EventType.MOVE && (buildCell != null || buildDome))
            throw new IllegalArgumentException("MoveType Events don't have build information");

        else if(type == EventType.BUILD && (srcCell != null || dstCell != null))
            throw new IllegalArgumentException("BuildType Events don't have move information");

        this.type = type;
        this.srcCell = srcCell;
        this.dstCell = dstCell;
        this.buildCell = buildCell;
        this.buildDome = buildDome;
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
        return buildDome;
    }

    public int getBuiltBlockHeight() {
        if(type == EventType.BUILD)
            return buildCell.getHeight();
        return -1;
    }


}
