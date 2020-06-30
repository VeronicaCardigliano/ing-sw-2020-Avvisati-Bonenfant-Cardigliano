package it.polimi.ingsw.client;

import it.polimi.ingsw.server.model.gameMap.Coordinates;
import it.polimi.ingsw.server.model.gameMap.IslandBoard;
import java.util.*;

/**
 * Abstract class of the game map, extended by CliGameMap and GuiMap, that uses these common methods and attributes
 */
public abstract class GameMap {

    private static final int DOME = -1;
    private Map<Coordinates, Integer> heights = new HashMap<>();
    protected Map<String, ArrayList<Coordinates>> occupiedCells = new HashMap<>();

    public static final int firstBuilderIndex = 0;
    public static final int secondBuilderIndex = 1;
    public static final int buildersNum = 2;

    private Set<Coordinates> possibleDstBuilder1 = new HashSet<>();
    private Set<Coordinates> possibleDstBuilder2 = new HashSet<>();
    private int chosenBuilderNumber = 0;

    public GameMap() {
        for (int i = 0; i < IslandBoard.dimension; i++)
            for (int j=0; j < IslandBoard.dimension; j++)
                heights.put(new Coordinates(i,j), 0);
    }

    public void setPossibleDst(Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2) {
        this.possibleDstBuilder1 = possibleDstBuilder1;
        this.possibleDstBuilder2 = possibleDstBuilder2;
    }

    public void setChosenBuilderNum(int chosenBuilderNumber) {
        this.chosenBuilderNumber = chosenBuilderNumber;
    }

    public int getChosenBuilderNum() {
        return chosenBuilderNumber;
    }

    public ArrayList<Set<Coordinates>> getPossibleDst() {
        ArrayList<Set<Coordinates>> possDst = new ArrayList<>();
        possDst.add(possibleDstBuilder1);
        possDst.add(possibleDstBuilder2);
        return possDst;
    }

    public void setOccupiedCells (String nickname, Coordinates builder1, Coordinates builder2) {
        ArrayList<Coordinates> positions = new ArrayList<>();
        positions.add(builder1);
        positions.add(builder2);

        occupiedCells.put(nickname, positions);
    }

    public void removePlayer(String nickname) {
        occupiedCells.remove(nickname);
    }

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

    public Map<String, ArrayList<Coordinates>> getOccupiedCells() {
        return this.occupiedCells;
    }

    public void modifyHeight(Coordinates coord, boolean dome) {
        if (dome)
            heights.put(coord, DOME);
        else
            heights.put(coord, heights.get(coord) + 1);
    }

    public Map<Coordinates, Integer> getHeights() {
        return this.heights;
    }

}
