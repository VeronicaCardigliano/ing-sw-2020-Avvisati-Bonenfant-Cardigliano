package it.polimi.ingsw.server.model.godCards;


import java.util.ArrayList;

import it.polimi.ingsw.server.model.gameMap.Builder;
import it.polimi.ingsw.server.model.gameMap.Cell;
import it.polimi.ingsw.server.model.Event;
import it.polimi.ingsw.server.model.gameMap.IslandBoard;
import it.polimi.ingsw.server.model.Player;


/**
 * @author thomas
 *
 * Generic Card associated to a god. Every Card has a corresponging json file with all its properties.
 * GodCard manages move and build logic interacting with the IslandBoard gameMap.
 */
public class GodCard {
    public static int maxHeightDifference = 1;
    private final String name;
    private final String description;
    protected final Player player;
    protected IslandBoard gameMap;

    protected String currState;
    protected int step;
    protected Builder currBuilder;

    protected ArrayList<ArrayList<String>> states;
    protected ArrayList<ArrayList<String>> statesCopy;
    protected ArrayList<String> currStateList;

    protected Event event;


    /**
     * GodCard constructor.
     * @param player whose card is
     */
    public GodCard(Player player, String name, String description, ArrayList<ArrayList<String>> states) {
        if(player == null)
            throw new RuntimeException("player can't be null");

        this.player = player;
        this.name = name;
        this.description = description;
        this.states = states;

        statesCopy = new ArrayList<>();
        currStateList = new ArrayList<>();

    }

    /**
     * @author thomas
     * Has to be called when the player associated to this card starts his turn.
     * It creates a copy of the states array to consume during the turn.
     */
    public void startTurn() {

        step = 0;
        event = null;

        statesCopy = new ArrayList<>(states);
        currState = statesCopy.get(0).get(0);
        currStateList.clear();
        currStateList.add(currState);

        for(ArrayList<String> list : statesCopy)
            if (!currState.equals(list.get(0))) {
                currState = "REQUIRED";
                if(!currStateList.contains(list.get(0)))
                currStateList.add(list.get(0));
            }

        for (Builder  b : player.getBuilders()){
            currBuilder = b;
                filterNextState();
        }

        currBuilder = null;

        //activate pending constraint
        if(gameMap == null)
            throw new RuntimeException("GameMap has to be set before starting turn.");
        gameMap.loadConstraint();
    }

    public void forceState(String state) {
        if(!state.equals("MOVE") && !state.equals("BUILD"))
            throw new IllegalArgumentException("State must be either MOVE or BUILD");

        this.currState = state;

    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public ArrayList<String> getCurrStateList(){ return currStateList; }


    private void setNextState(String previousStep) {
        step++;

        currStateList.clear();
        currState = "END";

        //search for possible paths removing the ones with a different previous step (from the one passed by argument)
        //and if there are multiple choices for the next step add them to a List
        boolean tmp = false;
        ArrayList<String> list;

        for(int i = 0; i < statesCopy.size(); i++) {
            list = statesCopy.get(i);

            if(step < list.size()) {
                if (list.get(step - 1).equals(previousStep)) {

                    if (!tmp) {
                        currState = list.get(step);
                        currStateList.add(list.get(step));
                        tmp = true;
                    } else if (!currState.equals(list.get(step))) {
                        currState = "REQUIRED";
                        //This should prevent from saving clones, TODO verify
                        if (!currStateList.contains(list.get(i)))
                            currStateList.add(list.get(i));
                    }

                } else {
                    statesCopy.remove(list);
                    i--;
                }
            } else if(step == list.size() && !currStateList.contains("END")) {
                currStateList.add("END");
            } else{
                statesCopy.remove(list);
                i--;
            }
        }

        filterNextState();

    }


    private void filterNextState(){
        if (currStateList.size()>1){

            currState = "REQUIRED";
            //here test that the step can be really performed and is not locking the player
            int i_src = currBuilder.getCell().getI();
            int j_src = currBuilder.getCell().getJ();
            boolean canMove = false;
            boolean canBuild = false;

            for (String step : currStateList) {
                if (!step.equals("END"))
                    for (int x = 0; x < IslandBoard.dimension; x++)
                        for (int y = 0; y < IslandBoard.dimension; y++){

                            switch (step) {
                                case "MOVE":
                                    if (IslandBoard.distanceOne(i_src, j_src, x, y) && askMove(i_src, j_src, x, y)) {
                                        canMove = true;
                                    }
                                    break;

                                case "BUILD":
                                    if ((x == i_src && y == j_src || IslandBoard.distanceOne(i_src, j_src, x, y)) &&
                                            (askBuild(i_src, j_src, x, y, false) || askBuild(i_src, j_src, x, y, true))) {
                                        canBuild = true;
                                    }
                                    break;
                            }
                        }
            }

            if (!canBuild)
                currStateList.remove("BUILD");
            if (!canMove)
                currStateList.remove("MOVE");
            if (currStateList.size() == 1)
                currState = currStateList.get(0);
        }
    }


    public Player getPlayer() {
        return player;
    }

    /**
     * @param gameMap every GodCard will be interacting with.
     */
    public void setGameMap(IslandBoard gameMap) throws IllegalArgumentException{
        if(gameMap == null)
            throw new IllegalArgumentException("Can't set a null map");
        this.gameMap = gameMap;
    }

    public boolean build(int i_src, int j_src, int i_dst, int j_dst, boolean buildDome) {
        boolean built = false;
        Cell dst;

        if(askBuild(i_src, j_src, i_dst, j_dst, buildDome)) {
            dst = gameMap.getCell(i_dst, j_dst);
            currBuilder = gameMap.getCell(i_src, j_src).getBuilder();
            built = true;

            if(buildDome) {
                dst.addDome();
                event = new Event(Event.EventType.BUILD_DOME, gameMap.getCell(i_src, j_src), dst);
            }
            else {
                dst.addBlock();
                event = new Event(Event.EventType.BUILD, gameMap.getCell(i_src, j_src), dst);

            }

            setNextState("BUILD");

        }
        return built;
    }


    /**
     * @author thomas
     *
     * moves a builder owned by godCard's player to an adiacent cell.
     * It first calls askMove to validate the move and returns if the moved happened
     *
     * @return if the move did occur.
     */
    public boolean move(int i_src, int j_src, int i_dst, int j_dst)
    {
        Cell src;
        Cell dst;
        boolean moved = false;

        if(this.askMove(i_src, j_src, i_dst, j_dst)) {
            moved = true;
            currBuilder = gameMap.getCell(i_src, j_src).getBuilder();
            src = gameMap.getCell(i_src, j_src);
            dst = gameMap.getCell(i_dst, j_dst);

            dst.setOccupant(src.getBuilder());
            src.removeOccupant();

            event = new Event(Event.EventType.MOVE, gameMap.getCell(i_src, j_src), dst);
            setNextState("MOVE");

        }

        return moved;
    }

    /**
     * Try to move a builder from src position to dst position.
     *
     * @param i_src every coordinate must be in range 0 - IslandBoard.dimension (usually 5)
     * @return Returns true if the required move has distance of one, height difference between destination cell and
     * source is less than one and there isn't a dome or an occupant on destination Cell.
     */
    public boolean askMove(int i_src, int j_src, int i_dst, int j_dst) {
        Cell src;
        Cell dst;

        src = gameMap.getCell(i_src, j_src);
        dst = gameMap.getCell(i_dst, j_dst);

        return src.getBuilder() != null && src.getBuilder().getPlayer().equals(player) &&
                //IslandBoard.distanceOne(src, dst) &&
                IslandBoard.heightDifference(src, dst) <= maxHeightDifference &&
                !dst.isDomePresent() && !dst.isOccupied() && gameMap.check(new Event(Event.EventType.MOVE, src, dst)) &&
                dst != src;

    }

    /**
     * Tries to build from (i_src, j_src) to (i_dst, j_dst). If flag buildDome is set to true
     * it tries to build a dome instead of a block.
     *
     *
     * @param buildDome true if the builder wants to build a dome
     * @return true if the builder can build at (i_dst, j_dst)
     */
    public boolean askBuild(int i_src, int j_src, int i_dst, int j_dst, boolean buildDome) {
        Cell src;
        Cell dst;

        src = gameMap.getCell(i_src, j_src);
        dst = gameMap.getCell(i_dst, j_dst);

        Event.EventType type = buildDome ? Event.EventType.BUILD_DOME : Event.EventType.BUILD;

        boolean buildHeightCondition = (dst.getHeight() < IslandBoard.maxHeight && !buildDome) ||
                (dst.getHeight() == IslandBoard.maxHeight && buildDome);

        return src.getBuilder() != null && src.getBuilder().getPlayer().equals(player) &&
                IslandBoard.distanceOne(src, dst) &&
                !dst.isDomePresent() && !dst.isOccupied() && buildHeightCondition &&
                gameMap.check(new Event(type, src, dst));
    }

    /**
     * Has to be called after a move or a build.
     * @return if that event causes the player associated to the godCard to win.
     */
    public boolean winCondition() {

        return event != null && event.getType() == Event.EventType.MOVE && event.heightDifference() == 1 &&
                event.getDstBlockHeight() == IslandBoard.maxHeight;

    }

    public String getCurrState() {
        return currState;
    }

    public int getStepNumber () {return step;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GodCard godCard = (GodCard) o;

        if(godCard.name.equals("default")) return false;
        return godCard.name.equals(this.name);
    }

}
