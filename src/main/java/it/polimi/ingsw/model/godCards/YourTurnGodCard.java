package it.polimi.ingsw.model.godCards;

import it.polimi.ingsw.model.Player;

import java.util.ArrayList;
import java.util.Map;

public class YourTurnGodCard extends GodCard {

    private final boolean blockMovingUpIfBuilt;
    private boolean built;

    /**
     * GodCard constructor. Parses JSON
     *
     * @param player     whose card is
     */
    public YourTurnGodCard(Player player, String name, String description, ArrayList<ArrayList<String>> states,
                           Map<String, Boolean> flagParameters, Map<String, Integer> intParameters) {
        super(player, name, description, states);

        this.blockMovingUpIfBuilt = flagParameters.get("blockMovingUpIfBuilt");

    }

    @Override
    public boolean build(int i_src, int j_src, int i_dst, int j_dst, boolean buildDome) {

        //Prometheus power
        if(blockMovingUpIfBuilt)
            built = true;

        return super.build(i_src, j_src, i_dst, j_dst, buildDome);
    }

    @Override
    public boolean askMove(int i_src, int j_src, int i_dst, int j_dst) {
        //prometheus condition
        boolean prometheusCondition = blockMovingUpIfBuilt &&
                (!(gameMap.heightDifference(i_src, j_src, i_dst, j_dst) > 0) || !built);


        return super.askMove(i_src, j_src, i_dst, j_dst) && prometheusCondition;
    }
}
