package it.polimi.ingsw.server.model.gameMap;

import it.polimi.ingsw.server.model.Player;

/**
 * The builder is one of the two player's pawn that can be positioned on the map
 * and moved in each turn. It can also build in an adjacent cell.
 */

public class Builder {
    final private Player player;
    private Cell cell;
    private final BuilderColor color;
    public enum BuilderColor { MAGENTA, LIGHT_BLUE, WHITE }

    /**
     * @param player : owner of the pawn
     * @throws IllegalArgumentException if player null
     */

    public Builder(Player player, BuilderColor color) throws IllegalArgumentException {
        if (player == null)
            throw new IllegalArgumentException("Player can't be null");
        this.player = player;
        this.color = color;
    }

    /**
     * Overload used just for test purpose
     */
    public Builder(Player player) throws IllegalArgumentException {
        this(player, BuilderColor.WHITE);
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

    public BuilderColor getColor () {
        return color;
    }
}
