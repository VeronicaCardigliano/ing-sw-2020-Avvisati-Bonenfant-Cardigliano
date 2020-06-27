package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.GameMap;
import it.polimi.ingsw.server.model.gameMap.Coordinates;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;

import java.util.*;

public class GuiMap extends GameMap {

    private static final int buildingIndexInStack = 0;
    private static final int builderIndexInStack = 1;
    private static final double cellDimension = 78.0;
    private static final int cellGap = 1;
    private Scene scene;
    private TilePane tile;

    GuiMap(TilePane tilePane, Scene s) {

        super();
        this.tile = tilePane;
        this.scene = s;

        //Hgap and Vgap fixed, so set to the minimum
        tile.setHgap(cellGap);
        tile.setVgap(cellGap);
        tile.setPrefColumns(Gui.mapDimension);
        tile.setPrefRows(Gui.mapDimension);
        for (int i = 0; i < Gui.mapDimension * Gui.mapDimension; i++)
            tile.getChildren().add(newCell());
    }

    public int getHeight(Coordinates coord) {
        return getHeights().get(coord);
    }

    /**
     * This override of setOccupiedCells allows to add one builder at a time
     * @param nickname player who owns the builders
     * @param builder1 first builder
     * @param builder2 second builder
     */
    @Override
    public void setOccupiedCells (String nickname, Coordinates builder1, Coordinates builder2) {
        ArrayList<Coordinates> positions = new ArrayList<>();

        if (builder2 == null) {
            positions.add(builder1);
        }

        else if (builder1 == null) {
            positions.add(getOccupiedCells().get(nickname).get(firstBuilderIndex));
            positions.add(builder2);
        }
        else{
            positions.add(builder1);
            positions.add(builder2);
        }

        occupiedCells.put(nickname, positions);
    }

    /**
     * Creates each of the cells of the map (represented by the tilePane)
     * @return the cell element (represented by a StackPane)
     */
    private StackPane newCell() {

        StackPane cell = new StackPane();
        cell.prefWidthProperty().bind(scene.widthProperty().multiply(cellDimension/ Gui.sceneWidth));
        cell.prefHeightProperty().bind(scene.heightProperty().multiply(cellDimension/ Gui.sceneHeight));

        //To see cells replace TRANSPARENT with a visible Color
        cell.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        return cell;
    }

    /**
     * @param cellStackCoord stackPane of the cell
     * @return the position of the current index in the cell StackPane
     */
    protected int getCurrentBuilderIndexInStack(Coordinates cellStackCoord) {
        int result = 0;
        if (getHeight(cellStackCoord) != 0)
            result = builderIndexInStack;

        return result;
    }

    /**
     * This method creates a new builder and positions it in the right cell
     * @param color color of the builder owner
     * @param cellIndex where the new builder has to be put
     */
    protected void createBuilder(String color, int cellIndex) {

        StackPane dstCell = (StackPane) tile.getChildren().get(cellIndex);

        ImageView builder = null;
        switch (color) {
            case "LIGHT_BLUE":
                builder = new ImageView(new Image(getClass().getResourceAsStream("/BuilderLightBlue.png")));
                break;
            case "MAGENTA":
                builder = new ImageView(new Image(getClass().getResourceAsStream("/BuilderMagenta.png")));
                break;
            case "WHITE":
                builder = new ImageView(new Image(getClass().getResourceAsStream("/BuilderWhite.png")));
                break;
        }

        if (builder == null)
            return;

        //builder.setFitWidth(dstCell.getPrefWidth());
        builder.setPreserveRatio(true);

        builder.fitHeightProperty().bind(scene.heightProperty().multiply(Gui.ratioCellHeight));

        ImageView finalBuilder = builder;
        Platform.runLater(() -> dstCell.getChildren().add(finalBuilder));
    }

    /**
     * Resets old turn builder selection
     * @param builderCell the cell occupied by the last turn builder
     */
    protected void resetBuilder (Coordinates builderCell) {

        StackPane oldBuilderCell = (StackPane) tile.getChildren().get(coordinatesToIndex(builderCell));

        int currBuilderIndexStack = getCurrentBuilderIndexInStack(builderCell);
        ImageView builder = (ImageView) oldBuilderCell.getChildren().get(currBuilderIndexStack);
        builder.setOpacity(1);
    }

    /**
     * This method creates a new building based on reached height and puts it in the right destination.
     * @param dstIndex index of the destination of the build
     * @param buildDome this flag is true when a dome has to be built
     * @param nickname currPlayer who has built
     */
    public void createBuilding(int dstIndex, boolean buildDome, String nickname) {
        ImageView newBuilding = null;
        int oldHeight;

        StackPane clickedCell = (StackPane) tile.getChildren().get(dstIndex);

        oldHeight = getHeight(indexToCoord(dstIndex));
        if (oldHeight != 0)
                Platform.runLater(() -> clickedCell.getChildren().remove(buildingIndexInStack));

        modifyHeight(indexToCoord(dstIndex), buildDome);

        //adds the right image in function to height
        if (buildDome) {
            switch (oldHeight) {
                case 0:
                    newBuilding = new ImageView (new Image(getClass().getResourceAsStream("/groundDome.png")));
                    break;
                case 1:
                    newBuilding = new ImageView (new Image(getClass().getResourceAsStream("/domeFirstLevel.png")));
                    break;
                case 2:
                    newBuilding = new ImageView (new Image(getClass().getResourceAsStream("/domeSecondLevel.png")));
                    break;
                case 3:
                    newBuilding = new ImageView (new Image(getClass().getResourceAsStream("/domeThirdLevel.png")));
                    break;
            }
        }
        else {
            switch (getHeight(indexToCoord(dstIndex))) {
                case 1:
                    newBuilding = new ImageView (new Image(getClass().getResourceAsStream("/firstLevel.png")));
                    break;
                case 2:
                    newBuilding = new ImageView (new Image(getClass().getResourceAsStream("/secondLevel.png")));
                    break;
                case 3:
                    newBuilding = new ImageView (new Image(getClass().getResourceAsStream("/thirdLevel.png")));
                    break;
            }
        }

        if (newBuilding != null) {
            newBuilding.setPreserveRatio(true);
            newBuilding.fitHeightProperty().bind(scene.heightProperty().multiply(Gui.ratioCellHeight));

            ImageView finalNewBuilding = newBuilding;

            if (getOccupiedCells().get(nickname).contains(indexToCoord(dstIndex)))
                Platform.runLater(() -> clickedCell.getChildren().add(buildingIndexInStack, finalNewBuilding));
            else
                Platform.runLater(() -> clickedCell.getChildren().add(finalNewBuilding));
        }
    }

    /**
     * MoveBuilder moves the builder in the chosen cell and removes any already present builder, which'll be saved and
     * re-inserted after onBuilderPushed update, in the method pushBuilder
     * @param src index of source of move step
     * @param dst index of the destination cell
     */
    protected void moveBuilder (int src, int dst) {

        int builderIndex;
        builderIndex= getCurrentBuilderIndexInStack(indexToCoord(src));
        StackPane sourceCell = (StackPane) tile.getChildren().get(src);
        ImageView builderToMove = (ImageView) sourceCell.getChildren().get(builderIndex);
        Platform.runLater(() -> sourceCell.getChildren().remove(builderToMove));

        StackPane clickedCell = (StackPane) tile.getChildren().get(dst);
        Platform.runLater(() -> clickedCell.getChildren().add(builderToMove));

    }

    /**
     * Makes possible destinations light up and makes them opaque when mouse enters
     * @param cellIndex index of the possible destination cell
     */
    private void setPossibleDstCell(int cellIndex) {

        StackPane cell = (StackPane) tile.getChildren().get(cellIndex);
        cell.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 75%, LIGHTGOLDENRODYELLOW , TRANSPARENT)");

        if (getChosenBuilderNum() != 0) {
            cell.setOnMouseEntered(mouseEvent -> {
                StackPane cellToHandle = (StackPane) mouseEvent.getSource();
                cellToHandle.setOpacity(Gui.selectionOpacity);
            });

            cell.setOnMouseExited(mouseEvent -> {
                StackPane cellToHandle = (StackPane) mouseEvent.getSource();
                cellToHandle.setOpacity(1);
            });
        }
    }

    /**
     * Small method to set the onMouseClicked handler for the effective possible destinations of move/build after the turn
     * builder choice.
     * @param possibleDst set of possible destinations
     * @param event event handler to manage the choice
     */
    private void chooseDst(Set<Coordinates> possibleDst, EventHandler<MouseEvent> event) {
        for (Coordinates coord: possibleDst) {
            StackPane cell = (StackPane) tile.getChildren().get(coordinatesToIndex(coord));
            cell.setOnMouseClicked(event);
        }
    }

    /**
     * Shows the possible destinations for a move, can be called before the builder choice or after.
     * If the builder has been chosen
     * @param event handles the actions when a possibleDst is clicked
     */
    protected void showPossibleMoveDst(Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2,
                                       EventHandler<MouseEvent> event){

        if (getChosenBuilderNum() == 0 || getChosenBuilderNum() == 1) {
            for (Coordinates coord: possibleDstBuilder1)
                setPossibleDstCell(coordinatesToIndex(coord));
        }

        if (getChosenBuilderNum() == 0 || getChosenBuilderNum() == 2) {
            for (Coordinates coord: possibleDstBuilder2)
                setPossibleDstCell(coordinatesToIndex(coord));
        }

        if (getChosenBuilderNum() == 1)
            chooseDst(possibleDstBuilder1, event);
        else if (getChosenBuilderNum() == 2)
            chooseDst(possibleDstBuilder2, event);
    }

    /**
     * Shows the possible destinations for a build, can be called before the builder/dome choice or after.
     * PossibleDst sets are set to null if they're not to be considered, same for event in case the builder hasn't been chosen.
     */
    protected void showPossibleBuildDst(Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2,
                                     Set<Coordinates> possibleDstBuilder1forDome, Set<Coordinates> possibleDstBuilder2forDome,
                                        boolean buildDome, EventHandler<MouseEvent> event) {

        if (getChosenBuilderNum() == 0 || getChosenBuilderNum() == 1) {
            if (buildDome) {
                for (Coordinates coord: possibleDstBuilder1forDome)
                    setPossibleDstCell(coordinatesToIndex(coord));
            }
            else {
                for (Coordinates coord : possibleDstBuilder1)
                    setPossibleDstCell(coordinatesToIndex(coord));
            }
        }

        if (getChosenBuilderNum() == 0 || getChosenBuilderNum() == 2) {
            if (buildDome) {
                for (Coordinates coord: possibleDstBuilder2forDome)
                    setPossibleDstCell(coordinatesToIndex(coord));
            }
            else {
                for (Coordinates coord: possibleDstBuilder2)
                    setPossibleDstCell(coordinatesToIndex(coord));
            }
        }

        if (getChosenBuilderNum() == 1) {
            if (buildDome)
                chooseDst(possibleDstBuilder1forDome, event);
            else
                chooseDst(possibleDstBuilder1, event);
        }
        else if (getChosenBuilderNum() == 2) {
            if (buildDome)
                chooseDst(possibleDstBuilder2forDome, event);
            else
                chooseDst(possibleDstBuilder2, event);
        }
    }


    /**
     * This method has to be called after a push update. Inserts a new builder in the new position and updates the occupiedCells map.
     * @param nickname of the player whose builder have been pushed
     * @param src source coordinates
     * @param dst where the builder has to be added
     */
    protected void pushBuilder(String nickname, Coordinates src, Coordinates dst) {

        moveBuilder(coordinatesToIndex(src), coordinatesToIndex(dst));

        if (Coordinates.equals(getOccupiedCells().get(nickname).get(GameMap.firstBuilderIndex), src))
            setOccupiedCells(nickname, dst, getOccupiedCells().get(nickname).get(GameMap.secondBuilderIndex));
        else
            setOccupiedCells(nickname, getOccupiedCells().get(nickname).get(GameMap.firstBuilderIndex), dst);
    }

    /**
     * Resets the possible destinations cells after builder/destination choice
     */
    protected void resetPossibleDestinations() {
        for (Node cell : tile.getChildren()) {

            cell.setOnMouseClicked(null);
            cell.setOnMouseEntered(null);
            cell.setStyle ("-fx-background-color: TRANSPARENT");
        }
    }

    /**
     * Removes the builders of a player who has lost from the cell, taking their coordinates from occupiedCells
     * @param nickname of the losing player
     */
    protected void removeBuilders (String nickname) {

        Coordinates builder1Coord, builder2Coord;
        ImageView builder1, builder2;
        builder1Coord = getOccupiedCells().get(nickname).get(0);
        builder2Coord = getOccupiedCells().get(nickname).get(1);

        StackPane cellBuilder1 = (StackPane) tile.getChildren().get(coordinatesToIndex(builder1Coord));
        builder1 = (ImageView) cellBuilder1.getChildren().get(getCurrentBuilderIndexInStack(builder1Coord));
        Platform.runLater(() -> cellBuilder1.getChildren().remove(builder1));

        StackPane cellBuilder2 = (StackPane) tile.getChildren().get(coordinatesToIndex(builder2Coord));
        builder2 = (ImageView) cellBuilder2.getChildren().get(getCurrentBuilderIndexInStack(builder1Coord));
        Platform.runLater(() -> cellBuilder2.getChildren().remove(builder2));
        occupiedCells.remove(nickname);
    }

    /**
     * Resets the map for a new match
     */
    protected void resetMap () {

        for (int i = 0; i < tile.getChildren().size(); i++) {

            StackPane cell = (StackPane) tile.getChildren().get(i);
            cell.getChildren().clear();
            cell.setOnMouseClicked(null);
            cell.setOnMouseEntered(null);
            cell.setStyle ("-fx-background-color: TRANSPARENT");

        }
    }

    /**
     * Converts coordinates to the one-dimensional index with which children in tilePane are sorted
     * @param coord coordinates to convert
     * @return relative index
     */
    public int coordinatesToIndex(Coordinates coord) {
        return coord.getI()* Gui.mapDimension + coord.getJ();
    }

    /**
     * Converts one-dimentional index to coordinates
     */
    public Coordinates indexToCoord(int index) {
        int j = index % Gui.mapDimension;
        int i = index/Gui.mapDimension;
        return new Coordinates(i,j);
    }
}
