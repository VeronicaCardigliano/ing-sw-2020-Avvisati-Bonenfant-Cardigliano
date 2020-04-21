package it.polimi.ingsw.model.godCards;

import it.polimi.ingsw.model.Player;
import org.json.JSONObject;

public class YourTurnGodCard extends GodCard {
    /**
     * GodCard constructor. Parses JSON
     *
     * @param player     whose card is
     * @param jsonObject
     */
    public YourTurnGodCard(Player player, JSONObject jsonObject) {
        super(player, jsonObject);
    }


}
