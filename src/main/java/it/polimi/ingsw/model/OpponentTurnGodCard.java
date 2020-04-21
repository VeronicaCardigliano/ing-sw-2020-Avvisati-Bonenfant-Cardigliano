package it.polimi.ingsw.model;

import org.json.JSONObject;

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
     * @param jsonObject
     */
    public OpponentTurnGodCard(Player player, JSONObject jsonObject) {
        super(player, jsonObject);

        if(jsonObject.opt("activeOnMoveUp") != null)
            activeOnMoveUp = jsonObject.getBoolean("activeOnMoveUp");
        else
            activeOnMoveUp = false;

        /*
        if(jsonObject.opt("alwaysActive") != null)
            alwaysActive = jsonObject.getBoolean("alwaysActive");
        else
            alwaysActive = false;*/

        if(jsonObject.opt("blockMoveUp") != null)
            blockMoveUp = jsonObject.getBoolean("blockMoveUp");
        else
            blockMoveUp = false;

        /*if(jsonObject.opt("limusPower") != null)
            limusPower = jsonObject.getBoolean("limusPower");
        else
            limusPower = false;*/
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
        boolean allowed = false;

        //move events
        if(futureEvent.getType() == Event.EventType.MOVE) {
            //athena blocks move
            if(blockMoveUp && futureEvent.heightDifference() > 0)
                allowed = false;
        }

        return allowed;
    }
}

