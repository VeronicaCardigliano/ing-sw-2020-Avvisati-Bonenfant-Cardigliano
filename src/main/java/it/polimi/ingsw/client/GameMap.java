package it.polimi.ingsw.client;

import it.polimi.ingsw.client.gui.Gui;
import it.polimi.ingsw.server.model.gameMap.Coordinates;
import it.polimi.ingsw.server.model.gameMap.IslandBoard;

import java.util.*;

/**
 * Abstract class of the game map, extended by CliGameMap and GuiMap, that uses these common methods and attributes
 */
public abstract class GameMap {

    private static final int DOME = -1; //cells with dome are identified with height -1
    private final Map<Coordinates, Integer> heights = new HashMap<>();
    protected Map<String, ArrayList<Coordinates>> occupiedCells = new HashMap<>(); //every nickname String is associated to an array of 2 Coordinates (its builders positions)

    public static final int firstBuilderIndex = 0;
    public static final int secondBuilderIndex = 1;
    public static final int buildersNum = 2;
    protected Coordinates currentTurnBuilderPos; //position of the chosen builder during turn

    private Set<Coordinates> possibleDstBuilder1 = new HashSet<>(); //destinations for builder 1
    private Set<Coordinates> possibleDstBuilder2 = new HashSet<>(); //destinations for builder 2
    private int chosenBuilderNumber = 0;

    /**
     * Creates an empty map
     */
    public GameMap() {
        for (int i = 0; i < IslandBoard.dimension; i++)
            for (int j=0; j < IslandBoard.dimension; j++)
                heights.put(new Coordinates(i,j), 0);
    }

    /**
     * Sets a possible destination set for each of the owned builders.
     *
     * Arguments can be null.
     *
     * @param possibleDstBuilder1 Set of possible destinations for builder 1
     * @param possibleDstBuilder2 Set of possible destinations for builder 2
     */
    public void setPossibleDst(Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2) {
        this.possibleDstBuilder1 = possibleDstBuilder1;
        this.possibleDstBuilder2 = possibleDstBuilder2;
    }

    /**
     * Selects a builder.
     * @param chosenBuilderNumber number indicating which builder we want to select:
     *                            0: no builder
     *                            1: builder number 1
     *                            2: builder number 2
     */
    public void setChosenBuilderNum(int chosenBuilderNumber) {
        this.chosenBuilderNumber = chosenBuilderNumber;
    }

    /**
     * @return builder selection number.
     */
    public int getChosenBuilderNum() {
        return chosenBuilderNumber;
    }

    /**
     * Sets the position of the builder chosen for current turn
     * @param currentBuilderPos position of chosen Builder
     */
    public void setCurrentTurnBuilderPos(Coordinates currentBuilderPos) {
        currentTurnBuilderPos = currentBuilderPos;
    }

    /**
     * @return current turn builder's position
     */
    public Coordinates getCurrentTurnBuilderPos() {
        return currentTurnBuilderPos;
    }

    /**
     * @return all possible destinations
     */
    public ArrayList<Set<Coordinates>> getPossibleDst() {
        ArrayList<Set<Coordinates>> possDst = new ArrayList<>();
        possDst.add(possibleDstBuilder1);
        possDst.add(possibleDstBuilder2);
        return possDst;
    }

    /**
     * Updates gameMap with the 2 builders owned by a player
     * @param nickname player owning the 2 builders
     */
    public void setOccupiedCells (String nickname, Coordinates builder1, Coordinates builder2) {
        ArrayList<Coordinates> positions = new ArrayList<>();
        positions.add(builder1);
        positions.add(builder2);

        occupiedCells.put(nickname, positions);
    }

    /**
     * Removes players's builders from the map
     * @param nickname nickname whose builders are
     */
    public void removePlayer(String nickname) {
        occupiedCells.remove(nickname);
    }

    /**
     * Moves a player's builder from a source position to a destination position
     * @param nickname nickname whose builder is
     * @param src source position
     * @param dst destination position
     */
    public void updateOccupiedCells(String nickname, Coordinates src, Coordinates dst) {
        ArrayList<Coordinates> selectedCells = occupiedCells.get(nickname);

        if(selectedCells.get(0).equals(src)) {
            selectedCells.remove(0);
            selectedCells.add(0, dst);
        }
        else if(selectedCells.get(1).equals(src)) {
            selectedCells.remove(1);
            selectedCells.add(1, dst);
        }

    }

    /**
     *
     * @return a copy of the occupied cells map
     */
    public Map<String, ArrayList<Coordinates>> getOccupiedCells() {
        return Map.copyOf(occupiedCells);
    }

    /**
     * increment height of the specified cell. If dome is true it sets the height to DOME
     * @param coord position whose height will be modified
     * @param dome indicates if a dome will be placed at position coord
     */
    public void modifyHeight(Coordinates coord, boolean dome) {
        if (dome)
            heights.put(coord, DOME);
        else
            heights.put(coord, heights.get(coord) + 1);
    }

    /**
     * returns a copy of heights map
     * @return copy of heights map
     */
    public Map<Coordinates, Integer> getHeights() {
        return Map.copyOf(heights);
    }

    /**
     * Converts coordinates to the one-dimensional index with which children in tilePane are sorted
     * @param coord coordinates to convert
     * @return relative index
     */
    public static int coordinatesToIndex (Coordinates coord) {
        return coord.getI()* Gui.mapDimension + coord.getJ();
    }

    /**
     * Converts one-dimensional index to coordinates i and j
     * @param index index to convert
     * @return relative coordinates
     */
    public static Coordinates indexToCoord(int index) {
        int j = index % Gui.mapDimension;
        int i = index/Gui.mapDimension;
        return new Coordinates(i,j);
    }

}
