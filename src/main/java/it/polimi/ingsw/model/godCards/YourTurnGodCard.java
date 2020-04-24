package it.polimi.ingsw.model.godCards;

import it.polimi.ingsw.model.Player;
import org.json.JSONObject;

import java.util.ArrayList;

public class YourTurnGodCard extends GodCard {
    /**
     * GodCard constructor. Parses JSON
     *
     * @param player     whose card is
     */
    public YourTurnGodCard(Player player, String name, String description, ArrayList<ArrayList<String>> states) {
        super(player, name, description, states);
    }


}
