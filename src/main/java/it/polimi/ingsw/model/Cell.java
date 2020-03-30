package it.polimi.ingsw.model;

/**
 * @author giulio
 *
 */
public class Cell{

    int i;
    int j;
    int height;
    boolean domePresent;
    Builder occupant;
    //Le
    public Cell(int x, int y) throws IllegalArgumentException{
        if (!((x<6)&&(x>-1)&&(y<6)&&(y>-1))) throw new IllegalArgumentException("Invalid coordinates");
        else {
            this.i = x;
            this.j = y;
            height = 0;
            this.domePresent = false;
            this.occupant = null;
        }
    }

    private Cell(int i, int j, int height, Builder occupant, boolean domePresent) throws MaxHeightReachedException{

        this.i=i;
        this.j=j;
        if (height > 4 || height < 0) throw new MaxHeightReachedException("Out of range height, invalid build");
        else this.height = height;
        this.occupant = occupant;
        this.domePresent = domePresent;

    }

    public boolean isDomePresent(){
        return this.domePresent;
    }

    public int getIcoordinate(){
        return this.i;
    }

    public int getJcoordinate(){
        return this.j;
    }

    public int getHeight(){
        return this.height;
    }

    public Builder getBuilder() {return  this.occupant;}

    public boolean isOccupied() {if (this.occupant!= null) return true; return false;}


    public Cell addBlock(boolean buildDome) throws MaxHeightReachedException {
        if (this.height==4 || this.isDomePresent()) throw new MaxHeightReachedException("Out of height Exception");
        return new Cell(this.getIcoordinate(), this.getJcoordinate(), this.getHeight()+1, this.getBuilder(), this.height==3 || buildDome);
    }

    public Cell setOccupant(Builder occupant)throws AlreadyOccupiedException, InvalidOccupationException{

        //We are sure that MaxHeightReachedException will never be raised
        Cell tmp = new Cell(0,0);
        if (this.isOccupied() && occupant != null) throw new AlreadyOccupiedException();
        else if (this.isDomePresent() && occupant != null) throw new InvalidOccupationException();
        else {
            try {
                tmp = new Cell(this.getIcoordinate(), this.getJcoordinate(), this.getHeight(), occupant, false);
            } catch (MaxHeightReachedException e) {
                e.printStackTrace();
            }
        }
        return tmp;
    }




}
