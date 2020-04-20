package it.polimi.ingsw.model;

/**
 * @author giulio
 *
 * Mutable Cell class. Each cell has an height and can have an Occupant or a Dome. It's not possible to
 * build an additional block if height = 3 or a dome has been set.
 */

public class Cell{

    private final int i;
    private final int j;
    private int height;
    private boolean domePresent;
    private Builder occupant;


    public Cell(int i, int j) throws IllegalArgumentException{
        if (!(i >= 0 && i < IslandBoard.dimension && j >= 0 && j < IslandBoard.dimension)) throw new IllegalArgumentException("Invalid coordinates");
        else {
            this.i = i;
            this.j = j;
            height = 0;
            this.domePresent = false;
            this.occupant = null;
        }
    }

    public boolean isDomePresent(){
        return this.domePresent;
    }

    public int getI(){
        return this.i;
    }

    public int getJ(){
        return this.j;
    }

    public int getHeight(){
        return this.height;
    }

    public Builder getBuilder() {return  this.occupant;}

    public boolean isOccupied() {
        return occupant != null;
    }


    public boolean addBlock() {
        boolean added = false;

        if(!domePresent && height < 3) {
            height++;
            added = true;
        }

        return added;
    }

    public boolean addDome() {
        boolean added = false;

        if(!domePresent) {
            domePresent = true;
            added = true;
        }

        return added;
    }

    /**
     * Method that sets the Cell occupant.
     *
     * @param occupant builder willing to occupy the Cell. Cannot be null
     * @return true if occupant has been correctly set.
     *
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
