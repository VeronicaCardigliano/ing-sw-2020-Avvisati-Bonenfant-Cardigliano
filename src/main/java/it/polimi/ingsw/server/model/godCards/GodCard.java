package it.polimi.ingsw.server.model.godCards;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import it.polimi.ingsw.server.model.gameMap.Builder;
import it.polimi.ingsw.server.model.gameMap.Cell;
import it.polimi.ingsw.server.model.Event;
import it.polimi.ingsw.server.model.gameMap.Coordinates;
import it.polimi.ingsw.server.model.gameMap.IslandBoard;
import it.polimi.ingsw.server.model.Player;


/**
 * Generic Card associated to a god. Every Card has a corresponging json file with all its properties.
 * GodCard manages move and build logic interacting with the IslandBoard gameMap.
 */
public class GodCard {
    public static final int maxHeightDifference = 1;
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
     * @param player Whose card is
     * @throws RuntimeException If player is null
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
            ArrayList<String> tmpArray = new ArrayList<>(currStateList);
            filterNextState();
            if (currStateList.size() < tmpArray.size())
                currStateList = tmpArray;
        }

        currBuilder = null;

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

    public Player getPlayer() {
        return player;
    }

    /**
     * Update the list of available steps for a player given the step chosen previously.
     * @param previousStep Step chosen before
     */
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
                        if (!currStateList.contains(list.get(step)))
                            currStateList.add(list.get(step));
                    }

                } else {
                    statesCopy.remove(list);
                    i--;
                }
            } else if(step == list.size() && list.get(step-1).equals(previousStep) && !currStateList.contains("END")) {
                currStateList.add("END");
            } else{
                statesCopy.remove(list);
                i--;
            }
        }
        filterNextState();
    }


    /**
     * Find all possible cells where a builder can move or build
     * @param builder Builder we want to use
     * @param buildDome True if wants to try to build a dome
     * @return A set of available coordinates for moving/building
     */
    public Set<Coordinates> findBuilderPossibleDest(Builder builder, boolean buildDome){
        Set<Coordinates> possibleDstBuilder = new HashSet<>();
        Coordinates src = builder.getCell();
        int x, y;
        int i_src = src.getI();
        int j_src = src.getJ();
        for (x = 0; x < IslandBoard.dimension; x++)
            for (y = 0; y < IslandBoard.dimension; y++){

                switch (currState) {
                    case "MOVE":
                        if (askMove(i_src,
                                j_src, x, y)) {
                            possibleDstBuilder.add(new Coordinates(gameMap.getCell(x, y)));
                        }
                        break;

                    case "BUILD":
                        if (askBuild(i_src, j_src, x, y, buildDome)) {
                            possibleDstBuilder.add(new Coordinates(gameMap.getCell(x, y)));
                        }
                        break;
                }
            }
        return possibleDstBuilder;
    }


    /**
     * This method removes steps that will bring the player into a lose condition. If for example a builder has both
     * option of move and build, but there is a constraint which doesn't allow him to build, the build option will be
     * removed. These control is performer using {@link #askMove(int, int, int, int)} and
     * {@link #askBuild(int, int, int, int, boolean)}
     */
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
                                    if (askMove(i_src, j_src, x, y)) {
                                        canMove = true;
                                    }
                                    break;

                                case "BUILD":
                                    if ((askBuild(i_src, j_src, x, y, false) || askBuild(i_src, j_src, x, y, true))) {
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


    /**
     * @param gameMap every GodCard will be interacting with.
     */
    public void setGameMap(IslandBoard gameMap) throws IllegalArgumentException{
        if(gameMap == null)
            throw new IllegalArgumentException("Can't set a null map");
        this.gameMap = gameMap;
    }

    
    /**
     * Method used to actually do a build if askBuild returned a true value, adds a dome or a block based on buildDome parameter
     * @param buildDome specifies whether you want to build a dome or not
     * @return True if it was successful (used for test purpose)
     */
    public boolean build (int i_src, int j_src, int i_dst, int j_dst, boolean buildDome) {
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
     * Moves a builder owned by godCard's player.
     * It first calls {@link #askMove(int, int, int, int)} to validate the move and returns if the moved happened
     * @return True if the move did occur.
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
     * @param i_src every coordinate must be in range 0 - IslandBoard.dimension (usually 5)
     * @return True if the required move has distance of one, height difference between destination cell and
     * source is less than one and there isn't a dome or an occupant on destination Cell.
     */
    public boolean askMove(int i_src, int j_src, int i_dst, int j_dst) {
        Cell src;
        Cell dst;

        src = gameMap.getCell(i_src, j_src);
        dst = gameMap.getCell(i_dst, j_dst);

        return src.getBuilder() != null && src.getBuilder().getPlayer().equals(player) &&
                IslandBoard.distanceOne(src, dst) &&
                IslandBoard.heightDifference(src, dst) <= maxHeightDifference &&
                !dst.isDomePresent() && !dst.isOccupied() && gameMap.check(new Event(Event.EventType.MOVE, src, dst)) &&
                dst != src;

    }

    /**
     * Tries to build from (i_src, j_src) to (i_dst, j_dst). If flag buildDome is set to true
     * it tries to build a dome instead of a block.
     * @param buildDome true if the builder wants to build a dome
     * @return True if the builder can build at (i_dst, j_dst)
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
                IslandBoard.distanceOne (src, dst) &&
                !dst.isDomePresent() && !dst.isOccupied() && buildHeightCondition &&
                gameMap.check(new Event(type, src, dst));
    }

    /**
     * Has to be called after a {@link #move(int, int, int, int)} or a {@link #build(int, int, int, int, boolean)}.
     * @return True if that event causes the player associated to the godCard to win.
     */
    public boolean winCondition() {
        return event != null && event.getType() == Event.EventType.MOVE && event.heightDifference() == 1 &&
                event.getDstBlockHeight() == IslandBoard.maxHeight;
    }

    public String getCurrState() {
        return currState;
    }

    public int getStepNumber () {return step;}


    /**
     * @param o GodCard Object
     * @return True if god names are equals
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GodCard godCard = (GodCard) o;

        if(godCard.name.equals("default")) return false;
        return godCard.name.equals(this.name);
    }

}
