package it.polimi.ingsw.model;

import org.json.JSONObject;

public class OpponentTurnGodCard extends GodCard {

    private final boolean activeOnMoveUp;

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
    }




}
