package it.polimi.ingsw.model;

import org.json.JSONObject;

/*
 * GODS WITH WINCONDITION PROPERTY:
 * Pan
 * Chronos (advanced)
 *
 * */


public class WinConditionGodCard extends GodCard {

    private final int minimumDownStepsToWin;
    private final int completeTowersToWin;


    /**
     * GodCard constructor. Parses JSON
     *
     * @param player     whose card is
     * @param jsonObject
     */
    public WinConditionGodCard(Player player, JSONObject jsonObject) {
        super(player, jsonObject);

        if(jsonObject.opt("minimumDownStepsToWin") != null)
            minimumDownStepsToWin = jsonObject.getInt("minimumDownStepsToWin");
        else
            minimumDownStepsToWin = 4; //it is impossible to go down 4 steps

        if(jsonObject.opt("completeTowersToWin") != null)
            completeTowersToWin = jsonObject.getInt("completeTowersToWin");
        else
            completeTowersToWin = IslandBoard.dimension * IslandBoard.dimension + 1; //too many towers to complete
    }

    @Override
    protected boolean winCondition() {
        int completeTowers = 0;

        for(int i = 0; i < IslandBoard.dimension && completeTowers < completeTowersToWin; i++)
            for(int j = 0; j < IslandBoard.dimension && completeTowers < completeTowersToWin; j++)
                if(gameMap.getCell(i, j).isDomePresent() && gameMap.getCell(i, j).isDomePresent())
                    completeTowers++;

        return  super.winCondition() ||
                completeTowers >= completeTowersToWin ||
                (event.getType() == Event.EventType.MOVE && event.heightDifference() <= - minimumDownStepsToWin);

    }
}
