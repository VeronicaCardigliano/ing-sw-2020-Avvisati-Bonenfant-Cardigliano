package it.polimi.ingsw.server.model.godCards;

import it.polimi.ingsw.server.model.Event;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.gameMap.Builder;
import it.polimi.ingsw.server.model.gameMap.IslandBoard;

import java.util.ArrayList;
import java.util.Map;

/**
 * God card with powers that are applied during enemies turns.
 */
public class OpponentTurnGodCard extends GodCard {

    private final boolean activeOnMoveUp;
    private final boolean alwaysActive;

    private final boolean blockMoveUp;
    private final boolean limusPower;

    /**
     * GodCard constructor. Parses JSON
     * @param player whose card is
     */
    public OpponentTurnGodCard(Player player, String name, String description, ArrayList<ArrayList<String>> states,
                               Map<String, Boolean> flagParameters, Map<String, Integer> intParameters) {
        super(player, name, description, states);

        this.activeOnMoveUp = flagParameters.get("activeOnMoveUp");
        this.blockMoveUp = flagParameters.get("blockMoveUp");
        this.limusPower = flagParameters.get("limusPower");
        this.alwaysActive = flagParameters.get("alwaysActive");
    }

    @Override
    public void startTurn() {
        super.startTurn();
        if(!alwaysActive)
            gameMap.removeConstraint(this);
    }

    /**
     * Check if constraint should be added
     */
    public void check() {
        if(activeOnMoveUp && event.getType() == Event.EventType.MOVE && event.heightDifference() > 0)
            gameMap.addConstraint(this);
    }


    /**
     * Like {@link GodCard#setGameMap(IslandBoard)} but adds god constraints if necessary
     * @param gameMap Every GodCard will be interacting with.
     */
    @Override
    public void setGameMap(IslandBoard gameMap){
        super.setGameMap(gameMap);
        if(alwaysActive)
            gameMap.addConstraint(this);
    }


    /**
     * Like a god card move but uses {@link #check()} to add constraints
     * @see GodCard#move(int, int, int, int)
     * @return True if move is performed
     */
    @Override
    public boolean move(int i_src, int j_src, int i_dst, int j_dst) {
        boolean result =  super.move(i_src, j_src, i_dst, j_dst);
        check();
        return result;
    }


    /**
     * Like a god card build but uses {@link #check()} to add constraints
     * @see GodCard#build(int, int, int, int, boolean)
     * @param buildDome Specifies whether you want to build a dome or not
     * @return True if build is performed.
     */
    @Override
    public boolean build(int i_src, int j_src, int i_dst, int j_dst, boolean buildDome) {
        boolean result = super.build(i_src, j_src, i_dst, j_dst, buildDome);
        check();
        return result;
    }


    /**
     * Check a requested event given card constraints.
     * @return True if no constrain conflict with the input Event
     */
    public boolean checkFutureEvent(Event futureEvent) {
        boolean allowed = true;

        if(futureEvent.getType() == Event.EventType.MOVE) {
            if(blockMoveUp && futureEvent.heightDifference() > 0)
                allowed = false;
        }

        if(futureEvent.getType() == Event.EventType.BUILD ||futureEvent.getType() == Event.EventType.BUILD_DOME) {

            if (limusPower && !futureEvent.getSrcCell().getBuilder().getPlayer().equals(player)){
                for (Builder b : player.getBuilders()){
                    if (Math.abs(b.getCell().getI() - futureEvent.getDstCell().getI()) < 2 &&
                            Math.abs(b.getCell().getJ() - futureEvent.getDstCell().getJ()) < 2 &&
                            !(futureEvent.getType() == Event.EventType.BUILD_DOME && futureEvent.getDstCell().getHeight() == IslandBoard.maxHeight)) {
                        allowed = false;
                        break;
                    }
                }
            }

        }
        return allowed;
    }
}

