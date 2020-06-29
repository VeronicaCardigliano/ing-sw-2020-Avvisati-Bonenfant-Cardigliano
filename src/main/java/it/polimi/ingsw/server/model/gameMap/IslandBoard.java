package it.polimi.ingsw.server.model.gameMap;

import it.polimi.ingsw.server.model.Event;
import it.polimi.ingsw.server.model.godCards.OpponentTurnGodCard;

import java.util.HashSet;
import java.util.Set;

/**
 * IslandBoard class contains all the cells of the board and the list of the exposed constraints in the game
 */

public class IslandBoard {

    public final static int dimension = 5;
    public final static int maxHeight = 3;

    private Cell[][] matrix = new Cell[dimension][dimension];
    private Set<OpponentTurnGodCard> constraintsList;
    private OpponentTurnGodCard constraintToActivate;

    /**
     * Initializes a matrix[dimension][dimension] of Cell
     */
    public IslandBoard() {
        for (int i = 0; i < dimension; i++){
            for (int j = 0; j < dimension; j++){
                matrix[i][j] = new Cell(i,j);
            }
        }
        constraintsList = new HashSet<>();
    }

    public Cell getCell(int i, int j) throws RuntimeException {
        if (i >= dimension || j >= dimension || i < 0 || j < 0) throw new RuntimeException("Invalid coordinates");
        return matrix[i][j];
    }

    public Cell getCell(Coordinates coord) {
        return getCell(coord.getI(), coord.getJ());
    }


    /**
     * @return Returns the height difference between SRC and DST cells
     */
    public int heightDifference(int i_src, int j_src, int i_dst, int j_dst){
        return (getCell(i_dst,j_dst).getHeight() - getCell(i_src,j_src).getHeight());
    }


    /**
     * @return Returns the height difference between SRC and DST cells
     */
    public static int heightDifference(Cell src, Cell dst) {
        return dst.getHeight() - src.getHeight();
    }


    /**
     * @return true if the distance between SRC and DST cell is one
     */
    public static boolean distanceOne(int i_src, int j_src, int i_dst, int j_dst){
        int modI = Math.abs(i_src - i_dst);
        int modJ = Math.abs(j_src - j_dst);

        return !(modI == 0 && modJ == 0) && modI < 2 && modJ < 2;
    }

    /**
     * parameters must not be null
     * @param src Cell from which the builder will move
     * @param dst Cell to which the builder will move
     * @return true if distance between SRC and DST cell is one
     */
    public static boolean distanceOne(Cell src, Cell dst) {
        int modI = Math.abs(src.getI() - dst.getI());
        int modJ = Math.abs(src.getJ() - dst.getJ());

        return !(modI == 0 && modJ == 0) && modI < 2 && modJ < 2;
    }

    /**
     * Add a new Constraint for next players.
     * @param card with constraint
     */
    public void addConstraint(OpponentTurnGodCard card) {
        constraintToActivate = card;
    }

    /**
     * Remove Constraint for next players
     * @param card with constraint
     */
    public void removeConstraint(OpponentTurnGodCard card) {
        constraintsList.remove(card);
    }

    public boolean check(Event event) {
        boolean allowed = true;
        for(OpponentTurnGodCard constraintCard : constraintsList)
            if(!constraintCard.checkFutureEvent(event)) {
                allowed = false;
                break;
            }
        return allowed;
    }

    public void loadConstraint() {
        if(constraintToActivate != null) {
            constraintsList.add(constraintToActivate);
            constraintToActivate = null;
        }
    }
}
