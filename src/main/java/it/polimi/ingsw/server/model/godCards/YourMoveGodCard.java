package it.polimi.ingsw.server.model.godCards;

import it.polimi.ingsw.server.model.Event;
import it.polimi.ingsw.server.model.gameMap.Builder;
import it.polimi.ingsw.server.model.gameMap.Cell;
import it.polimi.ingsw.server.model.gameMap.IslandBoard;
import it.polimi.ingsw.server.model.Player;

import java.lang.annotation.Native;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author giulio
 *
 * Specific Card associated to a god whom effect activates during his turn. This kind of god implements its own
 *  move depending on three different parameters read from the json file.
 */
public class YourMoveGodCard extends GodCard {
    private int pushForce;
    private boolean secondMoveDiffDst;
    private boolean extraMovePerimeter;
    private Cell firstSrcCell;


    public YourMoveGodCard(Player player, String name, String description, ArrayList<ArrayList<String>> states,
                           Map<String, Boolean> flagParameters, Map<String, Integer> intParameters) {

        super(player, name, description, states);

        this.pushForce = intParameters.get("pushForce");
        this.secondMoveDiffDst = flagParameters.get("secondMoveDiffDst");
        this.extraMovePerimeter = flagParameters.get("extraMovePerimeter");

    }

    @Override
    public boolean move(int i_src, int j_src, int i_dst, int j_dst) {

        boolean result = false;
        if(askMove(i_src, j_src, i_dst, j_dst)) {
            if (super.step == 0)
                firstSrcCell = gameMap.getCell(i_src, j_src);

            Builder builderToPush = null;

            if(pushForce != 0) {
                builderToPush = gameMap.getCell(i_dst, j_dst).getBuilder();
                gameMap.getCell(i_dst, j_dst).removeOccupant();
            }


            //this should clone the current list of step, add a move option and then add this new list to statesCopy
            if (super.askMove(i_src, j_src, i_dst, j_dst) && extraMovePerimeter){
                if (i_dst == 0 || j_dst == 0 || i_dst == IslandBoard.dimension - 1 || j_dst == IslandBoard.dimension - 1){
                    ArrayList<String> list = new ArrayList<>(statesCopy.get(0));
                    list.add(step, "MOVE");
                    statesCopy.add(0, list);
                }
            }

            result = super.move(i_src, j_src, i_dst, j_dst);


            if(builderToPush != null)
                push(builderToPush, i_src, j_src, i_dst, j_dst);

            if (this.currState.equals("BUILD"))
                firstSrcCell = null;
        }
        return result;
    }


    //function to support overrided move function. src and dst are the same as move, this way i can get the direction
    // and calculate the landing Cell for the occupant
    private void push(Builder builderToPush, int i_src, int j_src, int i_dst, int j_dst){

        //calculate the new cell of the opponent builder based on pushForce value
        int i_enemy_dst = (i_dst - i_src) * pushForce + i_dst;
        int j_enemy_dst = (j_dst - j_src) * pushForce + j_dst;

        //moving the opponent builder
        gameMap.getCell(i_enemy_dst,j_enemy_dst).setOccupant(builderToPush);

    }


    //function to support overrided move function. this functions just checks if enemy can be pushed. src and dst are
    // the same as move, this way i can get the direction and calculate the landing Cell for the occupant
    private boolean askPush(int i_src, int j_src, int i_dst, int j_dst) {

        //calculate the new cell of the opponent builder based on pushForce value
        int i_enemy_dst = (i_dst - i_src) * pushForce + i_dst;
        int j_enemy_dst = (j_dst - j_src) * pushForce + j_dst;

        boolean allowSwitch = pushForce == -1;

        Cell dst = gameMap.getCell(i_dst, j_dst);

        // Checks that destination isn't out of board (this could happen due to the pushForce),
        // destination Cell is free from builders or dome (don't care about height)
        return  dst.isOccupied() && !dst.getBuilder().getPlayer().equals(player) &&
                i_enemy_dst < IslandBoard.dimension && i_enemy_dst >= 0 &&
                j_enemy_dst < IslandBoard.dimension && j_enemy_dst >= 0 &&
                !gameMap.getCell(i_enemy_dst, j_enemy_dst).isDomePresent() &&
                (allowSwitch || !gameMap.getCell(i_enemy_dst, j_enemy_dst).isOccupied());
    }



    /* askMove checks that height difference is less than 1, not getting out of board [this should be
     * provided by the model?] and
     * does't care about enemy
     * occupant (but cares about dome)*/

    @Override
    public boolean askMove(int i_src, int j_src, int i_dst, int j_dst){

        boolean extraConditions = true;

        Cell src = gameMap.getCell(i_src, j_src);
        Cell dst = gameMap.getCell(i_dst, j_dst);

        boolean moveHeightCondition = IslandBoard.heightDifference(src, dst) <= maxHeightDifference;
        boolean correctSrc = src.getBuilder() != null && src.getBuilder().getPlayer().equals(player);

        /*if (extraMovePerimeter && step > 1)
            extraConditions = (i_dst == 0 || j_dst == 0 || i_dst == IslandBoard.dimension - 1 || j_dst == IslandBoard.dimension - 1);
        */

        if (secondMoveDiffDst && step != 0)
            extraConditions = !firstSrcCell.equals(dst);

        return  (super.askMove(i_src, j_src, i_dst,j_dst) ||
                (pushForce != 0 && moveHeightCondition &&
                !dst.isDomePresent() &&
                correctSrc &&
                gameMap.check(new Event(Event.EventType.MOVE, src, dst)) &&
                (dst.isOccupied() &&
                askPush(i_src, j_src, i_dst, j_dst)))) &&
                extraConditions;

    }

}