package it.polimi.ingsw.model.godCards;


import java.util.ArrayList;
import java.util.Objects;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Event;
import it.polimi.ingsw.model.IslandBoard;
import it.polimi.ingsw.model.Player;
import org.json.*;


/**
 * @author thomas
 *
 * Generic Card associated to a god. Every Card has a corresponging json file with all its properties.
 * GodCard manages move and build logic interacting with the IslandBoard gameMap.
 */
public class GodCard {
    private final String name;
    private final String description;
    private final Player player;
    protected IslandBoard gameMap;

    protected String currState;
    protected int step;

    protected ArrayList<ArrayList<String>> states;
    protected ArrayList<ArrayList<String>> statesCopy;

    protected Event event;



    /**
     * GodCard constructor. Parses JSON
     * @param player whose card is
     */
    public GodCard(Player player, JSONObject jsonObject) {
        if(player == null)
            throw new RuntimeException("player can't be null");

        this.player = player;

        if (jsonObject.opt("name") != null && jsonObject.opt("description") != null) {
            this.name = jsonObject.getString("name");
            this.description = jsonObject.getString("description");
        }
        else {
            this.name = "default";
            this.description = "this is an empty godCard with no special powers";
        }

        states = new ArrayList<>();
        statesCopy = new ArrayList<>();

        //initialize states attribute which contains all possible states' configurations
        if(jsonObject.opt("states") != null) {

            JSONArray outerList = jsonObject.getJSONArray("states");
            ArrayList<String> innerList;

            //converts outerList into an ArrayList (states)
            for(int i = 0; i < outerList.length(); i++) {
                innerList = new ArrayList<>();

                for (int j = 0; j < outerList.getJSONArray(i).length(); j++)
                    innerList.add(outerList.getJSONArray(i).getString(j));

                states.add(innerList);

            }


        } else {
            ArrayList<String> list = new ArrayList<>();
            list.add("MOVE");
            list.add("BUILD");
            states.add(list);
        }

    }

    /**
     * @author thomas
     * Has to be called when the player associated to this card starts his turn.
     * It creates a copy of the states array to consume during the turn.
     */
    public void startTurn() {

        step = 0;

        statesCopy = new ArrayList<>(states);
        currState = statesCopy.get(0).get(0);

        for(ArrayList<String> list : statesCopy)
            if (!currState.equals(list.get(0))) {
                currState = "BOTH";
                break;
            }
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    /**
     * @author veronica
     *
     * Method that sets the next step according to the previous one.
     *
     */
    protected void setNextState(String previousStep) {
        step++;

        currState = "END";

        //search for possible paths removing the ones with a different previous step (from the one passed by argument)

        boolean tmp = false;
        ArrayList<String> list;

        for(int i = 0; i < statesCopy.size(); i++) {
            list = statesCopy.get(i);

            if(step < list.size()) {
                if (list.get(step - 1).equals(previousStep)) {

                    if (!tmp) {
                        currState = list.get(step);
                        tmp = true;
                    } else if (!currState.equals(list.get(step)))
                        currState = "BOTH";

                } else {
                    statesCopy.remove(list);
                    i--;
                }
            } else {
                statesCopy.remove(list);
                i--;
            }
        }

    }


    //TODO: move the factory method with the json in a special package
    /**
     * factory method that parses the json and creates a new GodCard from the right subclass.
     * If the JSON file is invalid it will automatically create a default GodCard.
     *
     * @param player picking the card
     * @return the GodCard created
     */
    public static GodCard createCard(Player player, JSONObject godObject) {
        GodCard cardCreated = null;

        if (godObject.opt("type") != null) {
            switch (godObject.getString("type").toUpperCase()) {
                case "MOVE":
                    cardCreated = new YourMoveGodCard(player, godObject);
                    break;

                case "BUILD":
                    cardCreated = new YourBuildGodCard(player, godObject);
                    break;

                case "OPPONENT":
                    cardCreated = new OpponentTurnGodCard(player, godObject);
                    break;

                case "TURN":
                    cardCreated = new YourTurnGodCard(player, godObject);
                    break;

                case "WIN":
                    cardCreated = new WinConditionGodCard(player, godObject);
                    break;
            }
        }
        else
            throw new RuntimeException("Invalid json");

        return cardCreated;
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

    public boolean build(int i_src, int j_src, int i_dst, int j_dst) {
        return build(i_src, j_src, i_dst, j_dst, false);
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
            src = gameMap.getCell(i_src, j_src);
            dst = gameMap.getCell(i_dst, j_dst);

            dst.setOccupant(src.getBuilder());
            src.removeOccupant();

            setNextState("MOVE");

            event = new Event(Event.EventType.MOVE, gameMap.getCell(i_src, j_src), dst);
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
    public boolean askMove(int i_src, int j_src, int i_dst, int j_dst)
    {
        Cell src;
        Cell dst;

        src = gameMap.getCell(i_src, j_src);
        dst = gameMap.getCell(i_dst, j_dst);

        return src.getBuilder() != null && src.getBuilder().getPlayer().equals(player) &&
                //IslandBoard.distanceOne(src, dst) &&
                IslandBoard.heightDifference(src, dst) <= 1 && !dst.isDomePresent() && !dst.isOccupied() &&
                gameMap.check(new Event(Event.EventType.MOVE, src, dst));

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


        boolean buildHeightCondition = (dst.getHeight() < 3 && !buildDome) || (dst.getHeight() == 3 && buildDome);

        return src.getBuilder() != null && src.getBuilder().getPlayer().equals(player) &&
                //&& IslandBoard.distanceOne(src, dst)
                !dst.isDomePresent() && !dst.isOccupied() && buildHeightCondition &&
                gameMap.check(new Event(type, src, dst));
    }

    /**
     * Has to be called after a move or a build.
     * @return if that event causes the player associated to the godCard to win.
     */
    public boolean winCondition() {

        return event != null && event.getType() == Event.EventType.MOVE && event.heightDifference() == 1 &&
                event.getDstBlockHeight() == 3;

    }

    public String getCurrState() {
        return currState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GodCard godCard = (GodCard) o;

        if (!Objects.equals(name, godCard.name)) return false;
        if (!Objects.equals(description, godCard.description)) return false;
        if (!Objects.equals(player, godCard.player)) return false;
        return Objects.equals(gameMap, godCard.gameMap);
    }

}
