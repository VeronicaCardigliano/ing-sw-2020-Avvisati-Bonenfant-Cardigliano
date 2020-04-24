package it.polimi.ingsw.model.godCards;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.IslandBoard;
import it.polimi.ingsw.model.Player;
import org.json.JSONObject;

import java.util.ArrayList;

import static java.lang.Math.max;

/**
 * @author giulio
 *
 * Specific Card associated to a god whom effect activates during his turn. This kind of god implements its own build and move depending on three different parameters read from the json file.
 *
 *
 */
public class YourMoveGodCard extends GodCard {

    private int pushForce;
    private boolean secondMoveDiffDst;
    private Cell firstSrcCell;


    public YourMoveGodCard(Player player, String name, String description, ArrayList<ArrayList<String>> states,
                           int pushForce, boolean secondMoveDiffDest) {

        super(player, name, description, states);

        this.pushForce = pushForce;
        this.secondMoveDiffDst = secondMoveDiffDest;

    }

    private boolean canMoveAgain(){

        boolean canMoveAgain = false;

        for (int t = 0; t < this.states.size(); t++) {
            for (int j = step; j < states.get(t).size(); j++){
                if (states.get(t).get(j) == "MOVE") canMoveAgain = true;
            }
        }
        return canMoveAgain;
    }

    @Override
    public boolean move(int i_src, int j_src, int i_dst, int j_dst) {

        boolean result = false;

        //aggiustare il set di firstSrcCell e il retun via boolean

        if (firstSrcCell == null) {
            //here i should not care about secondMoveDiffDst because firstSrcCell is empty!

            //cannot move basically
            if (!super.askMove(i_src, j_src, i_dst, j_dst)) {

                //cannot move using pushPower
                if (!askMove(i_src, j_src, i_dst, j_dst) || pushForce == 0) {
                    result = false;
                } else //can move using pushing power (but there could be a dome anyway or push cannot be completed
                {
                    if(askMove(i_src, j_src, i_dst, j_dst) && askPush(i_src, j_src, i_dst, j_dst)) {
                        push(i_src, j_src, i_dst, j_dst);
                        //here i can call super.move sure that all previous control are enough to say that this
                        // is a valid move. Test should assert that this function will return true
                        super.move(i_src, j_src, i_dst, j_dst);
                        result = true;
                        firstSrcCell = gameMap.getCell(i_src, j_src);
                    }
                    else result = false;
                }
            } else {
                //getting here means i can move as default
                result = super.move(i_src, j_src, i_dst, j_dst);
                firstSrcCell = gameMap.getCell(i_src, j_src);
            }
        }
        //from this line up to the end i know that a move has already been made due to the fact that firstSrcCell
        // != null

        //here i suppose that secondCellDiffDist could be true
        else if (secondMoveDiffDst && firstSrcCell.getI() == i_dst && firstSrcCell.getJ() == j_dst) result = false;

            //this else ensures that !(secondMoveDiffDst and src = dst).
        else {
            result = this.move(i_src, j_src, i_dst, j_dst);
            //in this way i "reset" the cell and make a new move
        }

        setNextState("MOVE");
        if (currState == "END") firstSrcCell = null;

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


        return  gameMap.heightDifference(i_src, j_src, i_dst, j_dst) < 2 &&
                gameMap.heightDifference(i_src, j_src, i_dst, j_dst) >= 0 &&
                IslandBoard.distanceOne(i_src, j_src, i_dst, j_dst) &&

                !gameMap.getCell(i_src, i_dst).isDomePresent();

    }

}