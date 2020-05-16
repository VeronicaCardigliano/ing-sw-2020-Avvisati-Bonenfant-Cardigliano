package it.polimi.ingsw.server.model.godCards;

import it.polimi.ingsw.server.model.gameMap.Cell;
import it.polimi.ingsw.server.model.gameMap.IslandBoard;
import it.polimi.ingsw.server.model.Player;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author giulio
 *
 * Specific Card associated to a god whom effect activates during his turn. This kind of god implements its own
 *  move depending on three different parameters read from the json file.
 */
public class YourMoveGodCard extends GodCard {

    public static int maxHeightDifference = 1;
    private int pushForce;
    private boolean secondMoveDiffDst;
    private Cell firstSrcCell;


    public YourMoveGodCard(Player player, String name, String description, ArrayList<ArrayList<String>> states,
                           Map<String, Boolean> flagParameters, Map<String, Integer> intParameters) {

        super(player, name, description, states);

        this.pushForce = intParameters.get("pushForce");
        this.secondMoveDiffDst = flagParameters.get("secondMoveDiffDst");

    }

    /*
    private boolean canMoveAgain(){

        boolean canMoveAgain = false;

        for (ArrayList<String> state : this.states) {

            for (int j = step; j < state.size(); j++) {
                if (state.get(j).equals("MOVE")) {
                    canMoveAgain = true;
                    break;
                }
            }
        }
        return canMoveAgain;
    }
    */

    @Override
    public boolean move(int i_src, int j_src, int i_dst, int j_dst) {

        boolean result = false;

        if (this.step == 0)
            firstSrcCell = gameMap.getCell(i_src, j_src);

        //checks if cannot move for basic rules
        if (!super.askMove(i_src, j_src, i_dst, j_dst)) {

            //checks if can move using pushPower
            if (pushForce != 0 && askMove(i_src, j_src, i_dst, j_dst) && askPush(i_src, j_src, i_dst, j_dst)) {

                    if (secondMoveDiffDst && firstSrcCell.getI() != i_dst && firstSrcCell.getJ() != j_dst)
                        result = false;
                    else {
                        push(i_src, j_src, i_dst, j_dst);
                        //here i can call super.move sure that all previous control are enough to say that this
                        // is a valid move. Test should assert that this function will return true
                        super.move(i_src, j_src, i_dst, j_dst);
                        result = true;
                    }
            }
        }
        else {
            //getting here means i can move as default
            if (secondMoveDiffDst && firstSrcCell.getJ() == j_dst && firstSrcCell.getI() == i_dst)
                result = false;
            else
                result = super.move(i_src, j_src, i_dst, j_dst);
        }
        if (this.currState.equals("BUILD"))
            firstSrcCell = null;

        return result;
    }



    //function to support overrided move function. src and dst are the same as move, this way i can get the direction
    // and calculate the landing Cell for the occupant
    private void push(int i_src, int j_src, int i_dst, int j_dst){

        int i_enemy_dst = (i_dst - i_src) * pushForce + i_dst;
        int j_enemy_dst = (j_dst - j_src) * pushForce + j_dst;

        //put builder on new cell
        gameMap.getCell(i_enemy_dst,j_enemy_dst).setOccupant(gameMap.getCell(i_dst, j_dst).getBuilder());
        //remove enemy builder from dst cell
        gameMap.getCell(i_dst,j_dst).removeOccupant();

    }



    //function to support overrided move function. this functions just checks if enemy can be pushed. src and dst are
    // the same as move, this way i can get the direction and calculate the landing Cell for the occupant
    private boolean askPush(int i_src, int j_src, int i_dst, int j_dst) {
        int i_enemy_dst = (i_dst - i_src) * pushForce + i_dst;
        int j_enemy_dst = (j_dst - j_src) * pushForce + j_dst;

        // Checks that destination isn't out of board (this could happen due to the pushForce),
        // destination Cell is free from builders or dome (don't care about height)
        return  i_enemy_dst < IslandBoard.dimension && i_enemy_dst > 0 &&
                j_enemy_dst < IslandBoard.dimension && j_enemy_dst > 0 &&
                !gameMap.getCell(i_enemy_dst, j_enemy_dst).isDomePresent() &&
                !gameMap.getCell(i_enemy_dst, j_enemy_dst).isOccupied();
    }



    /* askMove checks that height difference is less than 1, not getting out of board [this should be
     * provided by the model?] and
     * does't care about enemy
     * occupant (but cares about dome)*/

    public boolean askMove(int i_src, int j_src, int i_dst, int j_dst){


        return  gameMap.heightDifference(i_src, j_src, i_dst, j_dst) <= maxHeightDifference &&
                gameMap.heightDifference(i_src, j_src, i_dst, j_dst) >= 0 &&
                IslandBoard.distanceOne(i_src, j_src, i_dst, j_dst) && !gameMap.getCell(i_src, i_dst).isDomePresent() &&
                !((i_src == i_dst) && (j_src == j_dst));

    }

}