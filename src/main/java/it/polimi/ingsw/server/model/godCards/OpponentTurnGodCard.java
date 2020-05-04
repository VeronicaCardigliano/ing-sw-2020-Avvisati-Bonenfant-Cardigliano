package it.polimi.ingsw.server.model.godCards;

import it.polimi.ingsw.server.model.Event;
import it.polimi.ingsw.server.model.Player;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author thomas
 *
 * GodCard with powers that are applied during enemies turns.
 */
public class OpponentTurnGodCard extends GodCard {

    private final boolean activeOnMoveUp;
    //private final boolean alwaysActive;

    private final boolean blockMoveUp;
    //private final boolean limusPower;

    /**
     * GodCard constructor. Parses JSON
     *
     * @param player     whose card is
     *
     */
    public OpponentTurnGodCard(Player player, String name, String description, ArrayList<ArrayList<String>> states,
                               Map<String, Boolean> flagParameters, Map<String, Integer> intParameters) {
        super(player, name, description, states);

        this.activeOnMoveUp = flagParameters.get("activeOnMoveUp");
        this.blockMoveUp = flagParameters.get("blockMoveUp");

    }

    @Override
    public void startTurn() {
        super.startTurn();

        //when the turn starts previous contraint from this card has to be removed
        gameMap.removeConstraint(this);
    }

    public void check() {
        boolean addConstraint = false;

        /*if(alwaysActive)
            addConstraint = true;*/
        //athena power activation
        /*else*/ if(activeOnMoveUp && event.getType() == Event.EventType.MOVE && event.heightDifference() > 0)
            gameMap.addConstraint(this);


    }

    @Override
    public boolean move(int i_src, int j_src, int i_dst, int j_dst) {
        boolean result =  super.move(i_src, j_src, i_dst, j_dst);

        check();

        return result;
    }

    @Override
    public boolean build(int i_src, int j_src, int i_dst, int j_dst) {
        boolean result = super.build(i_src, j_src, i_dst, j_dst);

        check();

        return result;
    }

    public boolean checkFutureEvent(Event futureEvent) {
        boolean allowed = true;

        //move events
        if(futureEvent.getType() == Event.EventType.MOVE) {
            //athena blocks move
            if(blockMoveUp && futureEvent.heightDifference() > 0)
                allowed = false;
        }

        return allowed;
    }
}

