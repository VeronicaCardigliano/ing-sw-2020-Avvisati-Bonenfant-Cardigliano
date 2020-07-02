package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.GameMap;
import it.polimi.ingsw.client.View;
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

/**
 * Subclass of the abstract class GameMap
 */
public class GuiMap extends GameMap {

    private static final String builderLightBlue = "/Images/BuilderLightBlue.png";
    private static final String builderMagenta = "/Images/BuilderMagenta.png";
    protected static final String builderWhite = "/Images/BuilderWhite.png";
    private static final String groundDome = "/Images/groundDome.png";
    private static final String domeFirstLevel = "/Images/domeFirstLevel.png";
    protected static final String domeSecondLevel = "/Images/domeSecondLevel.png";
    private static final String domeThirdLevel = "/Images/domeThirdLevel.png";
    private static final String firstLevel = "/Images/firstLevel.png";
    protected static final String secondLevel = "/Images/domeSecondLevel.png";
    protected static final String thirdLevel =  "/Images/thirdLevel.png";

    //index of the building in the StackPane which represents the cell
    private static final int buildingIndexInStack = 0;
    private static final int builderIndexInStack = 1;
    private static final double cellDimension = 78.0;
    private static final int cellGap = 1;
    private Scene scene;
    private TilePane tile;
    private EventHandler<MouseEvent> currClickCellHandler;

    /**
     * This constructor sets the features of the tilePane and adds to it mapDimension x mapDimension StackPanes (Cells)
     * @param tilePane root pane of the GuiClass
     * @param primaryScene scene in which the map is inserted
     */
    GuiMap(TilePane tilePane, Scene primaryScene) {

        super();
        this.tile = tilePane;
        this.scene = primaryScene;

        tile.setHgap(cellGap);
        tile.setVgap(cellGap);
        tile.setPrefColumns(Gui.mapDimension);
        tile.setPrefRows(Gui.mapDimension);
        for (int i = 0; i < Gui.mapDimension * Gui.mapDimension; i++)
            tile.getChildren().add(newCell());
    }

    /**
     * This override method sets the chosen number calling super() and resets the builder selection if the parameter is 0
     * @param chosenBuilderNumber number of the chosen builder
     */
    @Override
    public void setChosenBuilderNum(int chosenBuilderNumber) {

        super.setChosenBuilderNum(chosenBuilderNumber);
        if(chosenBuilderNumber == 0)
            resetBuilder(getCurrentTurnBuilderPos());
    }

    /**
     * This override of setOccupiedCells allows to add one builder at a time and creates, with "create builder" method,
     * a new builder or two new builders. This is done if the player's builders haven't been positioned yet.
     * @param nickname player who owns the builders
     * @param builder1 first builder
     * @param builder2 second builder
     */
    @Override
    public void setOccupiedCells (String nickname, Coordinates builder1, Coordinates builder2) {

        if (getOccupiedCells().get(nickname) == null || !(getOccupiedCells().get(nickname).size() == buildersNum)) {

            ArrayList<Coordinates> positions = new ArrayList<>();
            if (builder2 == null) {
                positions.add(builder1);
                createBuilder(View.getColor(nickname).toUpperCase(), builder1);
            }

            else if (builder1 == null) {
                positions.add(getOccupiedCells().get(nickname).get(firstBuilderIndex));
                createBuilder(View.getColor(nickname).toUpperCase(), builder2);
                positions.add(builder2);
            }
            else {
                positions.add(builder1);
                createBuilder(View.getColor(nickname).toUpperCase(), builder1);
                positions.add(builder2);
                createBuilder(View.getColor(nickname).toUpperCase(), builder2);
            }

            occupiedCells.put(nickname, positions);
        }
    }

    /**
     * Shows the possible destinations for a move or build, can be called before the builder choice or after.
     * If the builder has been chosen, uses the attribute event to handle when a possibleDst is clicked.
     */
    @Override
    public void setPossibleDst (Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2) {

        if (possibleDstBuilder1 == null && possibleDstBuilder2 == null)
            resetPossibleDestinations();
        else if (possibleDstBuilder1 != null && possibleDstBuilder2 != null)
        {
            if (getChosenBuilderNum() == 0 || getChosenBuilderNum() == 1) {
                for (Coordinates coord : possibleDstBuilder1)
                    setPossibleDstCell(coordinatesToIndex(coord));
            }

            if (getChosenBuilderNum() == 0 || getChosenBuilderNum() == 2) {
                for (Coordinates coord : possibleDstBuilder2)
                    setPossibleDstCell(coordinatesToIndex(coord));
            }

            if (getChosenBuilderNum() == 1)
                chooseDst(possibleDstBuilder1, currClickCellHandler);
            else if (getChosenBuilderNum() == 2)
                chooseDst(possibleDstBuilder2, currClickCellHandler);
        }
    }

    /**
     * This override method calls the super() to update the occupied cells after a move/push event, then moves the builder
     * calling the private method moveBuilder
     * @param nickname of the player who's moving/been moved
     * @param src old position of the builder
     * @param dst new position of the builder
     */
    @Override
    public void updateOccupiedCells(String nickname, Coordinates src, Coordinates dst) {

        super.updateOccupiedCells(nickname, src, dst);

        moveBuilder(coordinatesToIndex(src), coordinatesToIndex(dst));
        resetPossibleDestinations();
    }

    /**
     * Calls the private method removeBuilders to remove the player builders, then removes the builders cells from the occupiedCells.
     * @param nickname player who loss/whose builders placement was wrong
     */
    @Override
    public void removePlayer (String nickname) {
        removeBuilders(getOccupiedCells().get(nickname).get(firstBuilderIndex),
                getOccupiedCells().get(nickname).get(secondBuilderIndex));
        occupiedCells.remove(nickname);
    }

    /**
     * This method modifies the height of the destination cell and creates also a new building based on reached height
     * placing it in the right destination.
     * @param dstCoord coordinates of the destination of the build
     * @param buildDome this flag is true when a dome has to be built
     */
    @Override
    public void modifyHeight(Coordinates dstCoord, boolean buildDome) {
        ImageView newBuilding = null;
        int oldHeight;
        int dstIndex = coordinatesToIndex(dstCoord);

        StackPane clickedCell = (StackPane) tile.getChildren().get(dstIndex);

        oldHeight = getHeights().get(dstCoord);
        if (oldHeight != 0)
            Platform.runLater(() -> clickedCell.getChildren().remove(buildingIndexInStack));

        super.modifyHeight(dstCoord, buildDome);

        //adds the right image in function to height
        if (buildDome) {
            switch (oldHeight) {
                case 0:
                    newBuilding = new ImageView (new Image(getClass().getResourceAsStream(groundDome)));
                    break;
                case 1:
                    newBuilding = new ImageView (new Image(getClass().getResourceAsStream(domeFirstLevel)));
                    break;
                case 2:
                    newBuilding = new ImageView (new Image(getClass().getResourceAsStream(domeSecondLevel)));
                    break;
                case 3:
                    newBuilding = new ImageView (new Image(getClass().getResourceAsStream(domeThirdLevel)));
                    break;
            }
        }
        else {
            switch (getHeights().get(dstCoord)) {
                case 1:
                    newBuilding = new ImageView (new Image(getClass().getResourceAsStream(firstLevel)));
                    break;
                case 2:
                    newBuilding = new ImageView (new Image(getClass().getResourceAsStream(secondLevel)));
                    break;
                case 3:
                    newBuilding = new ImageView (new Image(getClass().getResourceAsStream(thirdLevel)));
                    break;
            }
        }

        if (newBuilding != null) {
            newBuilding.setPreserveRatio(true);
            newBuilding.fitHeightProperty().bind(scene.heightProperty().multiply(Gui.ratioCellHeight));

            ImageView finalNewBuilding = newBuilding;

            //if (getOccupiedCells().get(nickname).contains(indexToCoord(dstIndex)))
            Platform.runLater(() -> clickedCell.getChildren().add(buildingIndexInStack, finalNewBuilding));
            //else
            //    Platform.runLater(() -> clickedCell.getChildren().add(finalNewBuilding));
        }
    }

    /**
     * This method is used to set from Gui an handler for the mouse clicked event on cells
     * @param handler handles what will happen when a user clicks on a possible cell
     */
    public void setCurrClickCellHandler(EventHandler<MouseEvent> handler) {
        currClickCellHandler = handler;
    }

    /**
     * Creates a new cell of the map (represented by the tilePane)
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
     * Returns the position(index) of the builder in the StackPane (cell)
     * @param cellStackCoord coordinates of the cell with the builder
     */
    private int getBuilderIndexInStack(Coordinates cellStackCoord) {
        int result = 0;
        if (getHeights().get(cellStackCoord) != 0)
            result = builderIndexInStack;

        return result;
    }

    /**
     * This method creates a new builder and positions it in the right cell
     * @param color color of the builder owner
     * @param cellCoord where the new builder has to be put
     */
    private void createBuilder(String color, Coordinates cellCoord) {

        StackPane dstCell = (StackPane) tile.getChildren().get(coordinatesToIndex(cellCoord));

        ImageView builder = null;
        switch (color) {
            case "LIGHT_BLUE":
                builder = new ImageView(new Image(getClass().getResourceAsStream(builderLightBlue)));
                break;
            case "MAGENTA":
                builder = new ImageView(new Image(getClass().getResourceAsStream(builderMagenta)));
                break;
            case "WHITE":
                builder = new ImageView(new Image(getClass().getResourceAsStream(builderWhite)));
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
    private void resetBuilder (Coordinates builderCell) {

        StackPane oldBuilderCell = (StackPane) tile.getChildren().get(coordinatesToIndex(builderCell));

        int currBuilderIndexStack = getBuilderIndexInStack(builderCell);
        ImageView builder = (ImageView) oldBuilderCell.getChildren().get(currBuilderIndexStack);
        builder.setOpacity(1);
    }

    /**
     * MoveBuilder moves the builder in the chosen cell and removes any already present builder
     * @param src index of source of move step
     * @param dst index of the destination cell
     */
    private void moveBuilder (int src, int dst) {

        int builderIndex;
        builderIndex= getBuilderIndexInStack(indexToCoord(src));
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
     * Small method to set the MouseClicked event handler for the effective possible destinations of move/build after the turn
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
     * Resets the possible destinations cells setting to null the mouse handlers and to TRANSPARENT the cells color
     */
    private void resetPossibleDestinations() {

        for (Node cell : tile.getChildren()) {
            cell.setOnMouseClicked(null);
            cell.setOnMouseEntered(null);
            cell.setStyle ("-fx-background-color: TRANSPARENT");
        }
    }

    /**
     * Removes the builders from builder1Coord and builder2Coord if a player loses or if buildersPlacement was not successful
     */
    private void removeBuilders (Coordinates builder1Coord, Coordinates builder2Coord) {

        ImageView builder1, builder2;

        StackPane cellBuilder1 = (StackPane) tile.getChildren().get(coordinatesToIndex(builder1Coord));
        builder1 = (ImageView) cellBuilder1.getChildren().get(getBuilderIndexInStack(builder1Coord));
        Platform.runLater(() -> cellBuilder1.getChildren().remove(builder1));

        StackPane cellBuilder2 = (StackPane) tile.getChildren().get(coordinatesToIndex(builder2Coord));
        builder2 = (ImageView) cellBuilder2.getChildren().get(getBuilderIndexInStack(builder1Coord));
        Platform.runLater(() -> cellBuilder2.getChildren().remove(builder2));
    }
}
