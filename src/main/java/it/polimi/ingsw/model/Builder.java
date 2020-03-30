package it.polimi.ingsw.model;

/**
 * @author veronica
 * The builder is one of the two player's pawn that can be positioned on the map
 * and moved in each turn
 */

public class Builder {
    final private Player player;
    private Cell cell;

    /**
     * @param player : owner of the pawn
     * @throws IllegalArgumentException if player null
     */

    public Builder(Player player) throws IllegalArgumentException {
        if (player == null)
            throw new IllegalArgumentException("Player can't be null");
        this.player = player;
    }

    public void setCell (Cell cell) throws IllegalArgumentException {
        if (cell == null)
            throw new IllegalArgumentException("Cell can't be null");
        this.cell = cell;
    }
    
    public Player getPlayer () {
        return player;
    }

    public Cell getCell () {
        return cell;
    }
}
