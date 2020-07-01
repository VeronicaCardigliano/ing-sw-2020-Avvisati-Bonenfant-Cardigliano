package it.polimi.ingsw.server.model.godCards;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.gameMap.IslandBoard;

import java.util.ArrayList;
import java.util.Map;


/**
 * Specific Card associated to a god whom effect activates during his turn.
 */
public class YourTurnGodCard extends GodCard {

    private final boolean blockMovingUpIfBuilt;
    private boolean built;

    public YourTurnGodCard(Player player, String name, String description, ArrayList<ArrayList<String>> states,
                           Map<String, Boolean> flagParameters) {
        super(player, name, description, states);
        this.blockMovingUpIfBuilt = flagParameters.get("blockMovingUpIfBuilt");
    }

    @Override
    public void startTurn(){
        super.startTurn();
        built = false;
    }

    /**
     * This override of build method, sets the built attribute to true if the flag blockMovingUpIfBuilt is true
     */
    @Override
    public boolean build(int i_src, int j_src, int i_dst, int j_dst, boolean buildDome) {
        if(blockMovingUpIfBuilt)
            built = true;
        return super.build(i_src, j_src, i_dst, j_dst, buildDome);
    }

    @Override
    public boolean askMove(int i_src, int j_src, int i_dst, int j_dst) {
        boolean prometheusCondition = blockMovingUpIfBuilt &&
                (!(gameMap.heightDifference(i_src, j_src, i_dst, j_dst) > 0) || !built);
        return super.askMove(i_src, j_src, i_dst, j_dst) && prometheusCondition && IslandBoard.distanceOne(i_src, j_src, i_dst, j_dst);
    }
}
