package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.GameMap;
import it.polimi.ingsw.client.View;
import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.gameMap.Coordinates;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;
import java.util.stream.Collectors;


public class Gui extends View {

    public static final int minNumberOfPlayers = 2;
    public static final int maxNumberOfPlayers = 3;
    public static final int mapDimension = 5;
    public static final int sceneWidth = 960, sceneHeight = 540;
    public static final double marginLength = 20;
    private static final int minSceneWidth = 800;
    private static final int maxSceneWidth = 2500;

    public static final double mapRatioFromSides = 280.0/sceneWidth;
    public static final double mapPatioFromTop = 75.0/sceneHeight;
    public static final double mapRatioFromBottom = 70.0/sceneHeight;
    public static final double ratioCellHeight = 78.0/sceneHeight;

    public final static int fontSize = 14;
    public final static double selectionOpacity = 0.7;
    public static final int godsForPlayer = 1;
    public static final Color SEA = Color.rgb(51,184,253);
    private final static int maxMessagesShown = 3;

    private static final String buttonCoralSrc = "file:src/main/resources/btn_coral.png";
    private static final String buttonCoralPressedSrc = "file:src/main/resources/btn_coral_pressed.png";
    protected static final String submitButton = "file:src/main/resources/btn_submit.png";
    protected static final String submitButtonPressed = "file:src/main/resources/btn_submit_pressed.png";
    private static final String nameTagSrc = "file:src/main/resources/nameTag.png";
    private static final String versusSrc = "file:src/main/resources/versus.png";
    private static final String backgroundSrc = "file:src/main/resources/SantoriniBoard.png";
    private static final String titleSrc = "file:src/main/resources/title.png";

    private Map<String, String> matchGodCards = new HashMap<>();
    private Stage primaryStage;
    private Scene primaryScene;
    private HomeScene homeScene;
    private BorderPane root;
    private AnchorPane bottomAnchorPane;
    private AnchorPane home;
    private VBox bottomMessagesVBox;
    private VBox playersRegion;
    private TilePane tile;
    private VBox dialogRegion;
    private GuiMap gameMap;
    private boolean buildDome;
    private Insets playersRegionInsets;
    private int numMessages;
    private Label connectionErrorLbl;

    private Map<String, Text> playersNameTags = new HashMap<>();

    /**
     * Constructor that creates the primaryStage, sets the home scene and creates the main scene opened after the connection
     * The main stage is composed by a BorderPane as root with inside a VBox as playersRegion with players infos on the left,
     * a VBox as dialogRegion (on the right) in which are shown the requests during the match,
     * an AnchorPane in the bottom in which are printed messages on the left and buttons on the right to show godCards and to quit,
     * the title on the top in a StackPane and finally in the center a tilePane which represents the gameBoard
     *
     * It also creates a starting homeScene
     * @param primaryStageParam principal stage of the match
     */
    public Gui(Stage primaryStageParam) {

        this.primaryStage = primaryStageParam;

        this.home = new AnchorPane();
        TextField IPInsertion = new TextField ("IP");
        TextField portInsertion = new TextField("Port");

        homeScene = new HomeScene (home, sceneWidth, sceneHeight, IPInsertion, portInsertion);

        homeScene.getPlayBtn().setOnMouseClicked(mouseEvent -> {
            try {
                int portNum = Integer.parseInt(portInsertion.getText());
                notifyConnection(IPInsertion.getText(), portNum);
            }
            catch (NumberFormatException e) {
                onConnectionError("WRONG FORMAT: Insert an Integer as port value");
            }
        });;

        this.root = new BorderPane();
        this.primaryScene = new Scene (root, sceneWidth, sceneHeight);

        tile = new TilePane();
        gameMap = new GuiMap(tile, primaryScene);

        this.numMessages = 0;
        this.playersRegion = new VBox();
        this.playersRegionInsets = new Insets(0, primaryScene.getWidth()* mapRatioFromSides /4, 0, 0);
        this.dialogRegion = new VBox();

        this.bottomAnchorPane = new AnchorPane();

        /* Test purpose
        bottomAnchorPane.setBackground(new Background(new BackgroundFill(Color.YELLOWGREEN, null, null)));
        dialogRegion.setBackground(new Background(new BackgroundFill(Color.DARKRED, null, null)));
        playersRegion.setBackground(new Background(new BackgroundImage(new Image("file:src/main/resources/title_sky.png"), BackgroundRepeat.NO_REPEAT,
              BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(playersRegion.getPrefWidth() - playersRegion.getBorder().getInsets().getRight(), playersRegion.getHeight(), false, false, false, false))));
        playersRegion.setStyle("-fx-background-color: linear-gradient(to right, WHITE 80%, TRANSPARENT)"); */

        playersRegion.prefWidthProperty().bind(primaryScene.widthProperty().multiply(mapRatioFromSides));
        playersRegion.setAlignment(Pos.CENTER_LEFT);
        playersRegion.setSpacing(marginLength);
        dialogRegion.prefWidthProperty().bind(primaryScene.widthProperty().multiply(mapRatioFromSides));
        dialogRegion.setSpacing(marginLength);
        dialogRegion.setAlignment(Pos.CENTER);
        bottomAnchorPane.prefHeightProperty().bind(primaryScene.heightProperty().multiply(mapRatioFromBottom));

        this.bottomMessagesVBox = new VBox();
        bottomMessagesVBox.setSpacing(marginLength/10);
        bottomAnchorPane.getChildren().add(bottomMessagesVBox);
        AnchorPane.setLeftAnchor(bottomMessagesVBox, marginLength);
        AnchorPane.setTopAnchor(bottomMessagesVBox, marginLength/4);
        bottomMessagesVBox.setAlignment(Pos.CENTER);

        root.setLeft(playersRegion); //list of players + gods
        root.setRight(dialogRegion); //dialog with user
        root.setTop(setTitle(primaryScene));
        root.setBottom(bottomAnchorPane);
        root.setCenter(tile);

        root.setBackground(new Background(
                new BackgroundImage(new Image(backgroundSrc), BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(0, 0, false, false, false, true))));

        primaryStage.minWidthProperty().bind(home.heightProperty().multiply((double)sceneWidth/sceneHeight));
        primaryStage.minHeightProperty().bind(home.widthProperty().divide((double)sceneWidth/sceneHeight));

        primaryStage.widthProperty().addListener((o, oldValue, newValue)->{
            if(newValue.intValue() < minSceneWidth) {
                primaryStage.setResizable(false);
                primaryStage.setWidth(minSceneWidth);
                primaryStage.setResizable(true);
            }
        });

        primaryStage.setScene(homeScene);

        //TODO: verify max Width
        primaryStage.setMaxWidth(maxSceneWidth);

        primaryStage.setOnCloseRequest(windowEvent -> {

            notifyDisconnection(getNickname());
            primaryStage.close();
            Platform.exit();
            System.exit(0);
        });
    }

    /**
     * This method creates the title StackPane
     * @param s main scene
     * @return the StackPane of the title (titlePane)
     */
    private StackPane setTitle(Scene s) {

        StackPane titlePane = new StackPane();
        titlePane.prefHeightProperty().bind(s.heightProperty().multiply(mapPatioFromTop));
        titlePane.prefWidthProperty().bind(s.widthProperty());
        ImageView title = new ImageView (titleSrc);

        title.setPreserveRatio(true);

        title.fitWidthProperty().bind(titlePane.prefWidthProperty());
        title.fitHeightProperty().bind(titlePane.prefHeightProperty());
        //title.setFitHeight(s.getHeight()*ratioFromBottom);

        //the % after center are for the x and y pos of center the circle of shapes, % after radius is the % of reaching of the second color
        titlePane.setStyle("-fx-font-weight: bold; -fx-background-color: radial-gradient(center 50% 30%, radius 100%, rgb(51,184,253), TRANSPARENT)");

        titlePane.setAlignment(Pos.CENTER);
        titlePane.getChildren().add(title);
        return titlePane;
    }

    /**
     * This method creates a button given the following parameters:
     * @param btnName name shown on the button
     * @param backgroundSrc background image of the button
     * @param parent the pane in which the button has to be positioned
     * @param handler what to do when the button is pressed
     * @param pressedBtnSrc background image of the pressed button
     * @return the new button object
     */
    private Button createButton(String btnName, String backgroundSrc, Pane parent, EventHandler<MouseEvent> handler, String pressedBtnSrc) {

        Button button = new Button(btnName);
        button.setBackground(new Background(new BackgroundImage(new Image(backgroundSrc), BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(0, 0, false, false, false, true))));

        button.setOnMouseEntered(mouseEvent -> {
            Button enteredButton = (Button) mouseEvent.getSource();
            DropShadow shadow = new DropShadow();
            enteredButton.setEffect(shadow);
        });

        button.setOnMouseExited(mouseEvent -> {
            Button enteredButton = (Button) mouseEvent.getSource();
            enteredButton.setEffect(null);
        });

        button.setOnMousePressed(mouseEvent -> {
            Button pressedButton = (Button) mouseEvent.getSource();
            pressedButton.setBackground(new Background(new BackgroundImage(new Image(pressedBtnSrc), BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(0, 0, false, false, false, true))));

        });

        button.setOnMouseReleased(mouseEvent -> {
            Button pressedButton = (Button) mouseEvent.getSource();
            pressedButton.setBackground(new Background(new BackgroundImage(new Image(backgroundSrc), BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(0, 0, false, false, false, true))));

        });

        //button.setPrefWidth(stage.getWidth()/12);
        button.prefWidthProperty().bind(primaryScene.widthProperty().divide(11));
        button.prefHeightProperty().bind(primaryScene.heightProperty().divide(15));

        Platform.runLater(() -> parent.getChildren().add(button));
        button.setOnMouseClicked(handler);

        return button;
    }

    @Override
    public void run() {
        primaryStage.show();
    }

    /**
     * Creates a ChoicePopup allowing the first player to choose the number of players of the match, with a
     * choiceBox to show which are the possibilities
     */
    @Override
    public void askNumberOfPlayers() {

        setState(ViewState.NUMPLAYERS);
        Set<String> possibleNumPlayers = new HashSet<>();

        if (home.getChildren().contains(connectionErrorLbl))
            Platform.runLater(()->home.getChildren().remove(connectionErrorLbl));

        for (int i = minNumberOfPlayers; i <= maxNumberOfPlayers; i++)
            possibleNumPlayers.add(String.valueOf(i));

        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        Platform.runLater(() -> {
            ChoicePopup popup = new ChoicePopup(primaryStage, possibleNumPlayers,
                    "You're the first player! Select number of players: ", "New match", choiceBox);

            popup.getSubmit().setOnMouseClicked(mouseEvent -> {

                notifyNumberOfPlayers(Integer.parseInt(choiceBox.getValue()));
                popup.close();
            });
        });
    }

    /**
     * Creates a PlayersSetupPopup to make each player insert their nickname and birthday date, with two TextFields
     * whose content is sent to the server after the submit button is pressed
     */
    @Override
    public void askNickAndDate() {

        setState(ViewState.NICKDATE);

        TextField nickInsertion = new TextField ("nickname");
        TextField birthdayInsertion = new TextField("birthday");

        Platform.runLater(() -> {
            PlayerSetupPopup popup = new PlayerSetupPopup(primaryStage, nickInsertion, birthdayInsertion);

            popup.getSubmit().setOnMouseClicked(mouseEvent -> {
                setNickname(nickInsertion.getText());
                setDate(birthdayInsertion.getText());
                notifyNewPlayer(getNickname(), getDate());
                popup.close();
            });
        });
    }

    /**
     * Sends a GodCardsPopup to the Challenger with all the possible cards, sent through godDescriptionsParam
     * @param numOfPlayers number of the players of the match, and also of the godCards to choose
     */
    @Override
    public void chooseMatchGodCards(int numOfPlayers, Map<String, String> godDescriptionsParam) {

        setState(ViewState.MATCHGODS);

        Platform.runLater(() -> {
            GodCardsPopup popup = new GodCardsPopup(primaryStage, numOfPlayers, godDescriptionsParam);

            popup.getSubmit().setOnMouseClicked(mouseEvent -> {
                if (popup.getSelectionsNum() == numOfPlayers) {

                    notifyMatchGodCardsChoice(getNickname(), popup.getChosenGodCards());
                    popup.close();
                }
            });
        });
    }

    /**
     * Creates a GodCardsPopup for each player to make him choose his godCard
     * @param godDescriptions map of each card with its description
     * @param chosenCards already chosen cards
     */
    @Override
    public void askGodCard(Map<String, String> godDescriptions, Set<String> chosenCards) {

        setState(ViewState.PLAYERGOD);
        matchGodCards = godDescriptions;
        Map<String, String> availableGods = new HashMap<>();

        for(String godName : godDescriptions.keySet().stream().filter(godName -> !chosenCards.contains(godName)).collect(Collectors.toSet()))
            availableGods.put(godName, godDescriptions.get(godName));


        Platform.runLater(() -> {
            GodCardsPopup popup = new GodCardsPopup(primaryStage, godsForPlayer, availableGods);

            popup.getSubmit().setOnMouseClicked(mouseEvent -> {
                if (popup.getSelectionsNum() == godsForPlayer) {

                    notifyGodCardChoice(getNickname(), popup.getChosenGodCards().iterator().next());
                    popup.close();
                }
            });
        });
    }

    /**
     * Creates a ChoicePopup to make the Challenger choose the first player of the match
     * @param players list of players ready to play
     */
    @Override
    public void chooseStartPlayer(Set<String> players) {

        setState(ViewState.STARTPLAYER);

        ChoiceBox<String> choiceBox= new ChoiceBox<>();
        Platform.runLater(() -> {
            ChoicePopup popup = new ChoicePopup(primaryStage, players, "Choose the first player", "StartPlayer", choiceBox);
            popup.getSubmit().setOnMouseClicked(mouseEvent -> {

                notifySetStartPlayer(getNickname(), choiceBox.getValue());
                popup.close();
            });
        });
    }

    //TODO: make the server send available colors, not chosen one to avoid the manual insertion of them ?

    /**
     * Creates a ChoicePopup to make each player choose his color
     * @param chosenColors already chosen colors, not to be showed as possibilities
     */
    @Override
    public void askBuilderColor(Set<String> chosenColors) {

        Set<String> availableColors = new HashSet<>(Set.of("MAGENTA", "WHITE", "LIGHT_BLUE"));
        availableColors.removeAll(chosenColors);

        Set<String> printableColors = new HashSet<>();
        for (String s: availableColors)
            printableColors.add(s.replace('_',' ').toLowerCase());
        
        setState(ViewState.BUILDERCOLOR);

        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        Platform.runLater(() -> {
            ChoicePopup popup = new ChoicePopup(primaryStage, printableColors, "Choose builders color", "Builders color", choiceBox);
            popup.getSubmit().setOnMouseClicked(mouseEvent -> {

                notifyColorChoice(getNickname(), choiceBox.getValue().replace(' ','_').toUpperCase());
                popup.close();
            });
        });
    }

    /**
     * For each cell in the tile (map), if the state is BUILDERPLACEMENT and the cell is not already occupied,
     * clicking on it a new builder is added calling the method createBuilder in GuiMap.
     * This method updates also occupiedCells
     */
    @Override
    public void placeBuilders () {

        setState(ViewState.BUILDERPLACEMENT);
        printMessage("Click cells to place your builders");

        for (int i = 0; i < tile.getChildren().size(); i++) {

            tile.getChildren().get(i).setOnMouseClicked(mouseEvent -> {

                if (getState().toString().equals("BUILDERPLACEMENT")) {

                    List<Integer> occupiedCellsIndexes = new ArrayList<>();
                    //searches for the index of the cell on which the user clicked
                    int index = 0;
                    StackPane eventSource = (StackPane) mouseEvent.getSource();
                    for (Node node : tile.getChildren()) {
                        if (!node.equals(eventSource))
                            index++;
                        else
                            break;
                    }

                    //sets the occupiedCellsIndexes List, with indexes instead of Coordinates
                    for (String player : gameMap.getOccupiedCells().keySet()) {

                        occupiedCellsIndexes.add(gameMap.coordinatesToIndex(gameMap.getOccupiedCells().get(player).get(GameMap.firstBuilderIndex)));
                        //if also the second builder have been already set
                        if (gameMap.getOccupiedCells().get(player).size() > 1)
                            occupiedCellsIndexes.add(gameMap.coordinatesToIndex(gameMap.getOccupiedCells().get(player).get(GameMap.secondBuilderIndex)));
                    }

                    if (!occupiedCellsIndexes.contains(index)) {

                        gameMap.createBuilder(gameMap.getChosenColorsForPlayer().get(getNickname()), index);

                        //if the key "currPlayer" is already present, it means that the first builder position is been added
                        if (!gameMap.getOccupiedCells().containsKey(getNickname()))
                            gameMap.setOccupiedCells(getNickname(), gameMap.indexToCoord(index), null);

                        else {
                            gameMap.setOccupiedCells(getNickname(), null, gameMap.indexToCoord(index));
                            //if it's the second builder of getNickname() player, notify is called and, after the update, the state changes
                            notifySetupBuilders(getNickname(), gameMap.getOccupiedCells().get(getNickname()).get(GameMap.firstBuilderIndex),
                                    gameMap.getOccupiedCells().get(getNickname()).get(GameMap.secondBuilderIndex));
                        }
                    }
                }
            });
        }
    }

    /**
     * Creates a label and a choiceBox to ask the user to select how he wants to continue his turn or if he wants to end it.
     * @param possibleSteps set of the possible choices to continue the game
     */
    @Override
    public void chooseNextStep (Set<String> possibleSteps) {

        Label chooseStep = new Label ("Choose the next step: ");
        chooseStep.setFont(new Font("Arial", fontSize));
        ChoiceBox<String> stepChoice = new ChoiceBox<>();
        for (String step: possibleSteps)
            stepChoice.getItems().add(step);
        // set a default value
        stepChoice.setValue(possibleSteps.iterator().next());

        Platform.runLater(() -> dialogRegion.getChildren().addAll(chooseStep, stepChoice));

        Button okBtn = createButton("Ok", submitButton, dialogRegion,  mouseEvent -> {
            notifyStepChoice(getNickname(), stepChoice.getValue());
            Platform.runLater(() -> dialogRegion.getChildren().clear());
        }, submitButtonPressed);
        okBtn.setTextFill(Color.WHITESMOKE);
    }

    /**
     * Private method used to print maxMessagesShown messages in the bottom left of the scene.
     * @param message error or information messages received from server
     */
    private void printMessage(String message) {

        Text messageText = new Text(message);
        messageText.setFont(new Font("Arial", fontSize));
        messageText.setFill(Color.RED);

        if (numMessages >= maxMessagesShown) {
            numMessages = 0;
            Platform.runLater(() -> bottomMessagesVBox.getChildren().clear());
        }

        Platform.runLater(() -> bottomMessagesVBox.getChildren().add(messageText));
        numMessages++;
    }

    /**
     * Shows the possible destinations for a move for both builders if the builder hasn't been chosen and gives the possibility
     * to choose the turn builder. Otherwise, it allows to move with the chosen builder calling moveOnBuilderChosen()
     */
    @Override
    public void move() {

        if (getChosenBuilderNum() == 0) {

            //getChosenBuilderNum() returns 0 until a turn builder is chosen, until the user don't choose it prints all the possible dsts
            gameMap.showPossibleMoveDst(possibleDstBuilder1, possibleDstBuilder2, 0, null);

            printMessage("Select your turn builder");
            chooseTurnBuilder();
        }

        else {
            printMessage("Select where you want to move");
            moveOnBuilderChosen();
        }
    }

    /**
     * This method makes the user choose the turn builder and than calls moveOnBuilderChosen or buildToDst to complete the first step
     * of the turn.
     */
    private void chooseTurnBuilder() {

        for (Coordinates coord: gameMap.getOccupiedCells().get(getNickname())) {

            int currBuilderIndexStack;

            StackPane tmp = (StackPane) tile.getChildren().get(gameMap.coordinatesToIndex(coord));

            currBuilderIndexStack = gameMap.getCurrentBuilderIndexInStack(coord);

            tmp.getChildren().get(currBuilderIndexStack).setOnMouseEntered(mouseEvent -> {
                ImageView builderToHandle = (ImageView) mouseEvent.getSource();
                DropShadow shadow = new DropShadow();
                builderToHandle.setEffect(shadow);
            });

            tmp.getChildren().get(currBuilderIndexStack).setOnMouseExited(mouseEvent -> {
                ImageView builderToHandle = (ImageView) mouseEvent.getSource();
                builderToHandle.setEffect(null);
            });

            tmp.getChildren().get(currBuilderIndexStack).setOnMouseClicked(mouseEvent -> {

                int currentBuilderIndexInStack;
                //takes the builder and sets its opacity to show that it's been selected
                ImageView clickedBuilder = (ImageView) mouseEvent.getSource();
                clickedBuilder.setOpacity(Gui.selectionOpacity);

                //controls if the chosen builder is the first one or the second one of current player's builders
                Coordinates coordOfFirstBuilderCell = gameMap.getOccupiedCells().get(getNickname()).get(GameMap.firstBuilderIndex);
                StackPane firstBuilderCell = (StackPane) tile.getChildren().get(gameMap.coordinatesToIndex(coordOfFirstBuilderCell));
                Coordinates coordOfSecondBuilderCell = gameMap.getOccupiedCells().get(getNickname()).get(GameMap.secondBuilderIndex);
                StackPane secondBuilderCell = (StackPane) tile.getChildren().get(gameMap.coordinatesToIndex(coordOfSecondBuilderCell));

                clickedBuilder.setOnMouseClicked(null);
                clickedBuilder.setOnMouseEntered(null);

                currentBuilderIndexInStack = gameMap.getCurrentBuilderIndexInStack(coordOfFirstBuilderCell);

                if (firstBuilderCell.getChildren().get(currentBuilderIndexInStack).equals(clickedBuilder)) {

                    secondBuilderCell.getChildren().get(gameMap.getCurrentBuilderIndexInStack(coordOfSecondBuilderCell)).setOnMouseClicked(null);
                    secondBuilderCell.getChildren().get(gameMap.getCurrentBuilderIndexInStack(coordOfSecondBuilderCell)).setOnMouseEntered(null);
                    currentTurnBuilderPos = gameMap.getOccupiedCells().get(getNickname()).get(GameMap.firstBuilderIndex);
                    setChosenBuilderNum(1);
                }

                else {

                    firstBuilderCell.getChildren().get(gameMap.getCurrentBuilderIndexInStack(coordOfFirstBuilderCell)).setOnMouseClicked(null);
                    firstBuilderCell.getChildren().get(gameMap.getCurrentBuilderIndexInStack(coordOfFirstBuilderCell)).setOnMouseEntered(null);
                    currentTurnBuilderPos = gameMap.getOccupiedCells().get(getNickname()).get(GameMap.secondBuilderIndex);
                    setChosenBuilderNum(2);
                }

                gameMap.resetMap();

                if(getState().toString().equals("MOVE")) {
                    printMessage("Select where you want to move");
                    moveOnBuilderChosen();
                }
                else {
                    printMessage("Select where you want to build");
                    if ((!possibleDstBuilder1forDome.isEmpty() && getChosenBuilderNum() == 1) || (!possibleDstBuilder2forDome.isEmpty() &&
                            getChosenBuilderNum() == 2))
                        askForDome();
                    buildToDst();
                }

            });
        }
    }

    /**
     * On turn builder chosen, shows the possible destinations and adds an eventHandler on them in order to notify the chosen
     * move destination after being clicked. It updates also the currentTrunBuilderPos.
     */
    private void moveOnBuilderChosen() {

        gameMap.showPossibleMoveDst(possibleDstBuilder1, possibleDstBuilder2, getChosenBuilderNum(), mouseEvent -> {
            int dstIndex = 0;
            StackPane clickedCell = (StackPane) mouseEvent.getSource();

            //finds the index of the clickedCell
            for (Node node : tile.getChildren()) {
                if (!node.equals(clickedCell))
                    dstIndex++;
                else
                    break;
            }

            notifyMove(getNickname(), currentTurnBuilderPos, gameMap.indexToCoord(dstIndex));
            currentTurnBuilderPos = gameMap.indexToCoord(dstIndex);
            gameMap.resetMap();
        });
    }

    /**
     * On turn builder chosen, shows the possible destinations to build and adds an eventHandler on them to notify the
     * build destination choice. It creates also the new building calling the gameMap method "createBuilding"
     */
    private void buildToDst() {

        gameMap.showPossibleBuildDst(possibleDstBuilder1, possibleDstBuilder2, possibleDstBuilder1forDome, possibleDstBuilder2forDome,
                getChosenBuilderNum(), buildDome, mouseEvent -> {

                //if the turnBuilder have been chosen
                if (currentTurnBuilderPos != null) {
                    int index = 0;

                    StackPane clickedCell = (StackPane) mouseEvent.getSource();

                    //finds the index of the clickedCell
                    for (Node node : tile.getChildren()) {
                        if (!node.equals(clickedCell))
                            index++;
                        else
                            break;
                    }

                    gameMap.createBuilding(index, buildDome, getNickname());

                    notifyBuild(getNickname(), currentTurnBuilderPos, gameMap.indexToCoord(index), buildDome);
                    gameMap.resetMap();
                }
            });
    }

    /**
     * This method sets the buildDome value to true or false after the current player clicks on yes or no buttons
     */
    private void askForDome() {

        Label domeRequest = new Label ("Do you want to build a dome? ");
        Platform.runLater(() -> dialogRegion.getChildren().add(domeRequest));

        Button yesBtn = createButton("YES", submitButton, dialogRegion,
                mouseEvent ->  {
                    buildDome = true;
                    afterDomeChoice();
                }, submitButtonPressed);

        Button noBtn = createButton("NO", submitButton, dialogRegion,
                mouseEvent -> {
                    buildDome = false;
                    afterDomeChoice();
                }, submitButtonPressed);

        yesBtn.setTextFill(Color.WHITESMOKE);
        noBtn.setTextFill(Color.WHITESMOKE);

        dialogRegion.setAlignment(Pos.CENTER);
    }

    private void afterDomeChoice() {

        Platform.runLater(() -> dialogRegion.getChildren().clear());
        gameMap.showPossibleBuildDst(possibleDstBuilder1, possibleDstBuilder2, possibleDstBuilder1forDome,
                possibleDstBuilder2forDome, getChosenBuilderNum(), buildDome, null);
        if (getChosenBuilderNum() == 0) {
            printMessage("Select your turn builder");
            chooseTurnBuilder();
        }
        else {
            printMessage("Select where you want to build");
            buildToDst();
        }
    }

    @Override
    public void build() {

        if (getChosenBuilderNum() == 0) {

            //if the player can build a dome somewhere, he's asked what he wants to build
            if (!possibleDstBuilder1forDome.isEmpty() && !possibleDstBuilder2forDome.isEmpty())
                askForDome();
            else {
                gameMap.showPossibleBuildDst(possibleDstBuilder1, possibleDstBuilder2, possibleDstBuilder1forDome,
                        possibleDstBuilder2forDome, getChosenBuilderNum(), buildDome, null);
                printMessage("Select your turn builder");
                chooseTurnBuilder();
            }
        }

        else {

            if ((!possibleDstBuilder1forDome.isEmpty() && getChosenBuilderNum() == 1) ||
                    (!possibleDstBuilder2forDome.isEmpty() && getChosenBuilderNum() == 2))
                askForDome();

            else {
                gameMap.showPossibleBuildDst(possibleDstBuilder1, possibleDstBuilder2, possibleDstBuilder1forDome,
                        possibleDstBuilder2forDome, getChosenBuilderNum(), buildDome, null);
                printMessage("Select where you want to build");
                buildToDst();
            }
        }
    }

    @Override
    public void onBuilderBuild(String nickname, Coordinates src, Coordinates dst, boolean dome, boolean result) {

        if(result) {
            if (getNickname().equals(nickname))
                gameMap.resetMap();

            else
                gameMap.createBuilding(gameMap.coordinatesToIndex(dst), dome, nickname);
        }

        else
            printMessage("Could not build");
    }

    @Override
    public void onBuilderMovement(String nickname, Coordinates src, Coordinates dst, boolean result) {

        if(result) {
            if(getNickname().equals(nickname)) {
                currentTurnBuilderPos = dst;
                gameMap.resetMap();
            }

            //does the effective move of the builder in map
            gameMap.moveBuilder(gameMap.coordinatesToIndex(src), gameMap.coordinatesToIndex(dst));

            //updates the occupied cells, if the src is the first builder, updates the first builder pos, otherwise the second builder one
            if (Coordinates.equals(gameMap.getOccupiedCells().get(nickname).get(GameMap.firstBuilderIndex), src))
                gameMap.setOccupiedCells(nickname, dst, gameMap.getOccupiedCells().get(nickname).get(GameMap.secondBuilderIndex));
            else
                gameMap.setOccupiedCells(nickname, gameMap.getOccupiedCells().get(nickname).get(GameMap.firstBuilderIndex), dst);
        }
        else
            printMessage("Could not move.");
    }

    @Override
    public void onBuilderPushed(String nickname, Coordinates src, Coordinates dst) {

        gameMap.pushBuilder(nickname,src, dst);
    }

    @Override
    public void updatePossibleBuildDst(Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2, Set<Coordinates> possibleDstBuilder1forDome, Set<Coordinates> possibleDstBuilder2forDome) {

        this.possibleDstBuilder1 = possibleDstBuilder1;
        this.possibleDstBuilder2 = possibleDstBuilder2;
        this.possibleDstBuilder1forDome = possibleDstBuilder1forDome;
        this.possibleDstBuilder2forDome = possibleDstBuilder2forDome;
        buildDome = false;
        setState(ViewState.BUILD);
        build();
    }

    @Override
    public void updatePossibleMoveDst(Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2) {
        this.possibleDstBuilder1 = possibleDstBuilder1;
        this.possibleDstBuilder2 = possibleDstBuilder2;
        setState(ViewState.MOVE);

        if (getChosenBuilderNum() == 0)
            gameMap.showPossibleMoveDst(possibleDstBuilder1, possibleDstBuilder2, 0, null);
        move();
    }

    @Override
    public void onBuildersPlacedUpdate(String nickname, Coordinates positionBuilder1, Coordinates positionBuilder2, boolean result) {
        if (result) {
            //resets the handler of cell clicked
            for (Node node : tile.getChildren())
                node.setOnMouseClicked(null);

            if (getNickname().equals(nickname))
                printMessage("Builders positioned correctly.");
            else {
                gameMap.setOccupiedCells(nickname, positionBuilder1, positionBuilder2);
                gameMap.createBuilder(gameMap.getChosenColorsForPlayer().get(nickname), gameMap.coordinatesToIndex(positionBuilder1));
                gameMap.createBuilder(gameMap.getChosenColorsForPlayer().get(nickname), gameMap.coordinatesToIndex(positionBuilder2));
            }
        }
        else
            printMessage("ERROR in builders placement");
    }

    @Override
    public void onChosenStep(String nickname, String step, boolean result) {
        if(result)
            printMessage(nickname + " chose " + step);
        else
            printMessage("ERROR: wrong step.");
    }

    @Override
    public void onColorAssigned(String nickname, String color, boolean result) {
        if(result)
            gameMap.setChosenColorsForPlayer(nickname, color);
        else
            printMessage("Invalid insertion of color.");
    }

    @Override
    public void onEndGameUpdate(String winnerNickname) {

        if (!getNickname().equals(winnerNickname))
            printMessage("Player " + winnerNickname + " wins!");
        else
            createEndGameMessage("YOU WIN!");
        //printMessage("Player " + winnerNickname + " wins!!");
    }

    /**
     * Creates and end game message of victory or loss with a button to  eventually play again.
     * @param message string to print
     */
    private void createEndGameMessage(String message) {

        Text text = new Text(message);
        //text.prefWidthProperty().bind(playersRegion.prefWidthProperty().subtract(marginLength*2));
        text.setFont(new Font ("Courier" ,fontSize*3));
        text.setFill(Color.LIGHTSALMON);

        Blend blend = new Blend();
        blend.setMode(BlendMode.MULTIPLY);

        DropShadow ds = new DropShadow();
        ds.setColor(Color.MIDNIGHTBLUE);
        ds.setOffsetX(5);
        ds.setOffsetY(5);
        ds.setRadius(5);
        ds.setSpread(0.6);

        blend.setBottomInput(ds);

        DropShadow ds1 = new DropShadow();
        ds1.setColor(Color.WHITESMOKE);
        ds1.setRadius(20);
        ds1.setSpread(0.5);

        blend.setTopInput(ds1);

        text.setEffect(blend);

        Platform.runLater(()-> dialogRegion.getChildren().add(text));

        Button playAgainBtn = createButton("Play Again", submitButton, dialogRegion,  mouseEvent -> {

            //resets all
            Platform.runLater(() -> primaryStage.setScene(homeScene));
            Platform.runLater(() -> playersRegion.getChildren().clear());
            Platform.runLater(() -> bottomAnchorPane.getChildren().clear());
            for (int i = 0; i < tile.getChildren().size(); i++) {
                StackPane cell = (StackPane) tile.getChildren().get(i);
                cell.getChildren().clear();
            }
            Platform.runLater(() -> dialogRegion.getChildren().clear());
            Platform.runLater(() -> dialogRegion.getChildren().clear());
        }, submitButtonPressed);

        playAgainBtn.setTextFill(Color.WHITESMOKE);
    }


    @Override
    public void onWrongInsertionUpdate(String error) {
        printMessage(error);
    }

    @Override
    public void onWrongNumberInsertion() {
        printMessage("Invalid number Insertion.");
    }

    @Override
    public void onGodCardAssigned(String nickname, String card, boolean result) {
        if(result) {
            if(getNickname().equals(nickname))
                printMessage("GodCard assigned correctly.");
            setChosenGodCard(nickname, card);
        } else
            printMessage("Invalid insertion of godCard.");
    }

    @Override
    public void onMatchGodCardsAssigned(String nickname, Set<String> godCardsToUse, boolean result) {
        if(result)
            printMessage("GodCards correctly chosen. Wait for the other players to choose theirs.");
        else
            printMessage("Error assigning");
    }

    /**
     * Prints a message to notify the entry of a new player, if the result is false asks again for nick and birthday.
     */
    @Override
    public void onPlayerAdded(String nickname, boolean result) {
        if(result)
            printMessage(nickname + " joined the game!");
        else {
            setNickname(null);
            setDate(null);
            printMessage("Invalid nickname or date. Could not join the game.");
            askNickAndDate();
        }
    }

    @Override
    public void onLossUpdate(String nickname) {
        if (!getNickname().equals(nickname))
            printMessage(nickname + " lost!");
        else
            createEndGameMessage("YOU LOSE");
    }

    /**
     * Resets the turn builder if it's not null (it's not the first turn), glows the name of the current player
     * in the nameTag and resets the old one.
     * @param nickname of the current player
     */
    @Override
    public void onPlayerTurn(String nickname) {

        String state = getState().toString();
        if (currentTurnBuilderPos != null) {

            gameMap.resetBuilder(currentTurnBuilderPos);
            currentTurnBuilderPos = null;
            setChosenBuilderNum(0);
        }

        if (state.equals("BUILD") || state.equals("MOVE") || state.equals("STEP") || state.equals("BUILDERPLACEMENT")) {
            for (String player : playersNameTags.keySet())
                playersNameTags.get(player).setEffect(null);

            Glow glow = new Glow();
            glow.setLevel(1);
            playersNameTags.get(nickname).setEffect(glow);

        }
        printMessage("Turn of: " + nickname);
    }

    /**
     * Prints the message of starting player chosen.
     * @param nickname starting player
     * @param result true if the choice was successful
     */
    @Override
    public void onStartPlayerSet(String nickname, boolean result) {
        if(result)
            printMessage("The starting player is" + nickname);
        else
            printMessage("ERROR: could not set starting player.");
    }

    /**
     * When the match is in the state SETUP_BUILDERS, the playersRegion is featured with players nicknames and gods,
     * when the match is in SETUP_PLAYERS, the user is advised to wait for players to enter
     * @param currState currState of the game
     */
    @Override
    public void onStateUpdate(Model.State currState) {

        boolean firstPlayer = true;
        String state = currState.toString();

        if (state.equals(Model.State.SETUP_BUILDERS.toString())) {

            Map<String, String> chosenGodCards = getChosenGodCardsForPlayer();

            Platform.runLater(() ->playersRegion.getChildren().clear());

            playersRegion.setBorder(new Border(new BorderStroke(SEA, SEA, SEA,Color.TRANSPARENT, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                    BorderStrokeStyle.SOLID, null, CornerRadii.EMPTY, BorderStroke.DEFAULT_WIDTHS, playersRegionInsets)));

            playersRegion.setBackground(new Background(new BackgroundFill(Color.ANTIQUEWHITE, null, playersRegionInsets)));

            for (String player: chosenGodCards.keySet()) {

                if (!firstPlayer) {

                    ImageView vs = new ImageView(versusSrc);
                    vs.fitWidthProperty().bind(playersRegion.prefWidthProperty().subtract(playersRegionInsets.getRight()));
                    vs.setPreserveRatio(true);
                    Platform.runLater(() -> playersRegion.getChildren().add(vs));
                }

                firstPlayer = false;

                ImageView nameTag = new ImageView(nameTagSrc);
                nameTag.setPreserveRatio(true);
                nameTag.fitWidthProperty().bind(playersRegion.prefWidthProperty().subtract(playersRegionInsets.getRight()).divide(1.2));

                StackPane tagImageText = new StackPane();

                Text text = new Text(player + ": "+ chosenGodCards.get(player));
                text.setFont(new Font ("Courier" ,(float)fontSize));
                text.setStyle("-fx-font-weight: bold");

                playersNameTags.put(player, text);

                String color = gameMap.getChosenColorsForPlayer().get(player);
                switch (color) {
                    case "LIGHT_BLUE":
                        text.setFill(Color.LIGHTBLUE);
                        break;
                    case "MAGENTA":
                        text.setFill(Color.MAGENTA);
                        break;
                    case "WHITE":
                        text.setFill(Color.WHITE);
                        break;
                }

                Platform.runLater(() -> tagImageText.getChildren().addAll(nameTag, text));
                Platform.runLater(() -> playersRegion.getChildren().add(tagImageText));
            }

            HBox bottomBtns = new HBox();
            bottomBtns.setSpacing(marginLength);

            createButton("GodCards",buttonCoralSrc, bottomBtns, mouseEvent ->
                    new GodCardsPopup(primaryStage, 0, matchGodCards), buttonCoralPressedSrc);

            createButton("QUIT",buttonCoralSrc, bottomBtns, mouseEvent -> {

                notifyDisconnection(getNickname());
                primaryStage.close();
                Platform.exit();
                System.exit(0);
            }, buttonCoralPressedSrc);

            Platform.runLater(() -> bottomAnchorPane.getChildren().add(bottomBtns));
            AnchorPane.setBottomAnchor(bottomBtns, Gui.marginLength);
            AnchorPane.setRightAnchor(bottomBtns, Gui.marginLength);
        }

        else if (state.equals(Model.State.SETUP_PLAYERS.toString())) {

            Platform.runLater(() ->primaryStage.minWidthProperty().bind(root.heightProperty().multiply((double)Gui.sceneWidth/Gui.sceneHeight)));
            Platform.runLater(() ->primaryStage.minHeightProperty().bind(root.widthProperty().divide((double)Gui.sceneWidth/Gui.sceneHeight)));
            Platform.runLater(() ->primaryStage.setScene(primaryScene));

            Label label = new Label("Waiting for players... ");
            label.setTextFill(Color.RED);
            label.setFont(new Font("Arial", fontSize));

            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), label);
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.0);
            fadeTransition.setCycleCount(Animation.INDEFINITE);
            fadeTransition.play();

            playersRegion.setAlignment(Pos.CENTER);
            Platform.runLater(() ->playersRegion.getChildren().add(label));
        }
    }

    /**
     * Predefined alert to confirm the leaving of the match, returns true if the user clicks on YES button
     */
    protected static boolean confirmQuit () {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().getButtonTypes().clear();
        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getDialogPane().getButtonTypes().add(yes);
        alert.getDialogPane().getButtonTypes().add(cancel);

        alert.setTitle("Quit");
        alert.setHeaderText("Closing this window you'll leave the game");
        alert.setContentText("Do you want to quit?");
        return alert.showAndWait().orElse(cancel) == yes;
    }

    /**
     * Creates a label of error and shows it in the bottom-left of the home scene
     * @param message error message received from server
     */
    @Override
    public void onConnectionError(String message) {

        if (!home.getChildren().contains(connectionErrorLbl)) {
            connectionErrorLbl = new Label(message);
            connectionErrorLbl.setTextFill(Color.DARKRED);
            connectionErrorLbl.setFont(new Font("Arial", fontSize));
            Platform.runLater(()->home.getChildren().add(connectionErrorLbl));
            AnchorPane.setBottomAnchor(connectionErrorLbl, marginLength);
            AnchorPane.setLeftAnchor(connectionErrorLbl, (double) sceneWidth/10);
        }
        else
            Platform.runLater(()->connectionErrorLbl.setText(message));
    }
}


