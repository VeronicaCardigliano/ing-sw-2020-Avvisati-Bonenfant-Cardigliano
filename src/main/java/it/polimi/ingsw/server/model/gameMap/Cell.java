package it.polimi.ingsw.server.model.gameMap;

/**
 * Mutable Cell class. Each cell has an height and can have an Occupant or a Dome. It's not possible to
 * build an additional block if height = IslandBoard.maxHeight or a dome has been set.
 */

public class Cell extends Coordinates{

    private int height;
    private boolean domePresent;
    private Builder occupant;


    public Cell(int i, int j) throws IllegalArgumentException{
        super(i, j);
        height = 0;
        this.domePresent = false;
        this.occupant = null;
    }

    public boolean isDomePresent(){
        return this.domePresent;
    }

    public int getHeight(){
        return this.height;
    }

    public Builder getBuilder() {return  this.occupant;}

    public boolean isOccupied() {
        return occupant != null;
    }


    /**
     * Add a block on this cell
     * @return True if height is less than max height and there isn't a dome
     */
    public boolean addBlock() {
        boolean added = false;

        if(!domePresent && height < IslandBoard.maxHeight) {
            height++;
            added = true;
        }

        return added;
    }


    /**
     * Put a dome on this cell
     * @return True if there wasn't a dome before
     */
    public boolean addDome() {
        boolean added = false;

        if(!domePresent) {
            domePresent = true;
            added = true;
        }

        return added;
    }


    /**
     * Method that puts a builder into a cell.
     * @param occupant Builder willing to occupy the Cell. Cannot be null
     * @return True if occupant has been correctly set.
     */
    public boolean setOccupant(Builder occupant) {
        boolean set = false;

        if(occupant != null && this.occupant == null && !this.domePresent) {
            this.occupant = occupant;
            occupant.setCell(this);
            set = true;
        }
        return set;
    }


    public boolean removeOccupant() {
        boolean removed = false;
        if(occupant != null) {
            occupant = null;
            removed = true;
        }
        return removed;
    }
}
