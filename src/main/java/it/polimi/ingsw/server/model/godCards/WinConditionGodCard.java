package it.polimi.ingsw.server.model.godCards;

import it.polimi.ingsw.server.model.Event;
import it.polimi.ingsw.server.model.gameMap.IslandBoard;
import it.polimi.ingsw.server.model.Player;

import java.util.ArrayList;
import java.util.Map;

/**
 * God card with different conditions to win
 */
public class WinConditionGodCard extends GodCard {

    private final int minimumDownStepsToWin;
    private final int completeTowersToWin;

    public WinConditionGodCard(Player player, String name, String description, ArrayList<ArrayList<String>> states,
                               Map<String, Integer> intParameters) {
        super(player, name, description, states);

        this.minimumDownStepsToWin = intParameters.get("minimumDownStepsToWin");
        this.completeTowersToWin = intParameters.get("completeTowersToWin");
    }

    @Override
    public boolean winCondition() {
        int completeTowers = 0;

        for(int i = 0; i < IslandBoard.dimension && completeTowers < completeTowersToWin; i++)
            for(int j = 0; j < IslandBoard.dimension && completeTowers < completeTowersToWin; j++)
                if(gameMap.getCell(i, j).isDomePresent() && gameMap.getCell(i, j).getHeight() == 3)
                    completeTowers++;

        return  super.winCondition() ||
                completeTowers >= completeTowersToWin ||
                (event != null && event.getType() == Event.EventType.MOVE && event.heightDifference() <= - minimumDownStepsToWin);

    }
}
