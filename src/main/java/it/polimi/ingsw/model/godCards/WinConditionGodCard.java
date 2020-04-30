package it.polimi.ingsw.model.godCards;

import it.polimi.ingsw.model.Event;
import it.polimi.ingsw.model.gameMap.IslandBoard;
import it.polimi.ingsw.model.Player;

import java.util.ArrayList;
import java.util.Map;

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
     *
     */
    public WinConditionGodCard(Player player, String name, String description, ArrayList<ArrayList<String>> states,
                               Map<String, Boolean> flagParameters, Map<String, Integer> intParameters) {
        super(player, name, description, states);

        this.minimumDownStepsToWin = intParameters.get("minimumDownStepsToWin");
        this.completeTowersToWin = intParameters.get("completeTowersToWin");
    }

    @Override
    public boolean winCondition() {
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
