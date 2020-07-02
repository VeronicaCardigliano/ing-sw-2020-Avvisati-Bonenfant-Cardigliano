package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.GameMap;
import it.polimi.ingsw.client.View;
import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.gameMap.Builder;
import it.polimi.ingsw.server.model.gameMap.Coordinates;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author veronica
 * Graphical User Interface which extends the abstract class View, to share methods and attributes with Cli
 */
public class Gui extends View {

    public static final int minNumberOfPlayers = 2;
    public static final int maxNumberOfPlayers = 3;
    public static final int mapDimension = 5;
    public static final int sceneWidth = 960, sceneHeight = 540;
    public static final double marginLength = 20;
    private static final int minSceneWidth = 900;
    private static final int maxSceneWidth = 2500;

    public static final double mapRatioFromSides = 280.0/sceneWidth;
    public static final double mapPatioFromTop = 75.0/sceneHeight;
    public static final double mapRatioFromBottom = 70.0/sceneHeight;
    public static final double ratioCellHeight = 78.0/sceneHeight;

    public final static int fontSize = 15;
    public final static double selectionOpacity = 0.7;
    public static final int godsForPlayer = 1;
    private static final Color SEA = Color.rgb(51,184,253);
    private final static int maxMessagesShown = 3;
    private final static int maxNicknameLength = 10;
    private static final int builderIndexInStack = 1;

    private static final String warning = "\u26A0";
    private static final String buttonCoralSrc = "/Images/btn_coral.png";
    private static final String buttonCoralPressedSrc = "/Images/btn_coral_pressed.png";
    protected static final String submitButton = "/Images/btn_submit.png";
    protected static final String submitButtonPressed = "/Images/btn_submit_pressed.png";
    private static final String nameTagSrc = "/Images/nameTag.png";
    private static final String versusSrc = "/Images/versus.png";
    private static final String backgroundSrc = "/Images/SantoriniBoard.png";
    private static final String titleSrc = "/Images/title.png";
    private static final String iconSrc = "/Images/icon.png";
    private static final Font stdFont = new Font("Arial", fontSize);

    private Map<String, String> matchGodCards = new HashMap<>();
    private Stage primaryStage;
    private Scene primaryScene;
    private HomeScene homeScene;
    private BorderPane root;
    private AnchorPane bottomAnchorPane;
    private AnchorPane homePane;
    private VBox bottomMessagesVBox;
    private VBox playersRegion;
    private TilePane tile;
    private VBox dialogRegion;
    private Insets playersRegionInsets;
    private int numMessages;
    private Text connectionErrorText = new Text();
    private Text setupErrorText = new Text();
    private GodCardsPopup godCardsPopup;
    private PlayerSetupPopup playerSetupPopup;
    private ChoicePopup choiceSetupPopup;
    private Map<String, Text> playersNameTags = new HashMap<>();
    private boolean buildDome;
    private boolean challenger;
    private TextField IPInsertion;
    private TextField portInsertion;
    private Label connecting;
    private Button playAgainBtn;

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

        super();
        this.primaryStage = primaryStageParam;

        this.homePane = new AnchorPane();

        IPInsertion = new TextField ("IP");
        IPInsertion.setFont(new Font("Arial", fontSize/1.2));
        IPInsertion.setStyle("-fx-text-inner-color: antiquewhite; -fx-accent: lightsalmon;");
        portInsertion = new TextField("Port");
        portInsertion.setFont(new Font("Arial", fontSize/1.2));
        portInsertion.setStyle("-fx-text-inner-color: antiquewhite; -fx-accent: lightsalmon ;");

        homeScene = new HomeScene (homePane, sceneWidth, sceneHeight, IPInsertion, portInsertion);
        activatePlayBtn();

        setupErrorText.setFill(Color.FIREBRICK);
        setupErrorText.setFont(stdFont);
        AnchorPane.setBottomAnchor(setupErrorText, Gui.marginLength);
        AnchorPane.setLeftAnchor(setupErrorText, Gui.marginLength);
        challenger = false;

        this.root = new BorderPane();
        this.primaryScene = new Scene (root, sceneWidth, sceneHeight);

        tile = new TilePane();
        gameMap = new GuiMap(tile, primaryScene);

        this.numMessages = 0;
        this.playAgainBtn = new GuiButton("Play Again", submitButton, mouseEvent -> resetAll(), submitButtonPressed);
        playAgainBtn.setTextFill(Color.WHITESMOKE);
        setPrimarySceneButtonBinding(playAgainBtn);
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

        root.setLeft(playersRegion); //list of players + gods
        root.setRight(dialogRegion); //dialog with user
        root.setTop(setTitle(primaryScene));
        root.setBottom(bottomAnchorPane);
        root.setCenter(tile);

        root.setBackground(new Background(
                new BackgroundImage(new Image(getClass().getResourceAsStream(backgroundSrc)), BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(0, 0, false, false, false, true))));

        primaryStage.minWidthProperty().bind(homePane.heightProperty().multiply((double)sceneWidth/sceneHeight));
        primaryStage.minHeightProperty().bind(homePane.widthProperty().divide((double)sceneWidth/sceneHeight));

        primaryStage.widthProperty().addListener((o, oldValue, newValue)-> {
            if(newValue.intValue() < minSceneWidth) {
                primaryStage.setResizable(false);
                primaryStage.setWidth(minSceneWidth);
                primaryStage.setResizable(true);
            }
        });

        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream(iconSrc)));
        primaryStage.setScene(homeScene);

        primaryStage.setMaxWidth(maxSceneWidth);

        primaryStage.setOnCloseRequest(windowEvent -> {

            if (getState() != ViewState.CONNECTION)
                notifyDisconnection();
            primaryStage.close();
            Platform.exit();
            System.exit(0);
        });
    }

    private void setFadeTransition(Label label) {
        label.setTextFill(Color.RED);
        label.setFont(stdFont);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), label);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setCycleCount(Animation.INDEFINITE);
        fadeTransition.play();
    }

    /**
     * Private method used to set the handler on a mouse clicked event, which is reset after each click, to avoid
     * multiple connection requests simultaneously.
     */
    private void activatePlayBtn () {
        IPInsertion.setEditable(true);
        portInsertion.setEditable(true);
        homeScene.getPlayBtn().setOnMouseClicked(mouseEvent -> {
            try {
                int portNum = Integer.parseInt(portInsertion.getText());

                connecting = new Label("Connecting...");
                setFadeTransition(connecting);

                Platform.runLater(()-> homePane.getChildren().add(connecting));
                AnchorPane.setBottomAnchor(connecting, marginLength*2);
                AnchorPane.setRightAnchor(connecting, (double) sceneHeight/2);

                notifyConnection(IPInsertion.getText(), portNum);

                homeScene.getPlayBtn().setOnMouseClicked(null);
                IPInsertion.setEditable(false);
                portInsertion.setEditable(false);
            }
            catch (NumberFormatException e) {
                onConnectionError("WRONG FORMAT: Insert an Integer as port value");
            }
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
        ImageView title = new ImageView (new Image(getClass().getResourceAsStream(titleSrc)));

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
     * Used to print a generic message in function of the currState value. Prints on home if the state is CONNECTION,
     * on a popup if there's a popup opened, or on the primary scene through the specific method
     * @param message error or information messages
     */
    private void printMessage(String message, boolean onPopup) {

        String currState = getState().toString();

        switch (currState) {

            case "CONNECTION":
                if (!homePane.getChildren().contains(connectionErrorText)) {
                    connectionErrorText.setText(message);
                    connectionErrorText.setStyle("-fx-font-weight: bold; -fx-stroke: red");
                    connectionErrorText.setFont(stdFont);
                    Platform.runLater(()-> homePane.getChildren().add(connectionErrorText));
                    AnchorPane.setBottomAnchor(connectionErrorText, marginLength);
                    AnchorPane.setLeftAnchor(connectionErrorText, (double) sceneWidth/10);
                }
                else
                    Platform.runLater(()->connectionErrorText.setText(message));
                break;
            case "NUMPLAYERS":
                printOnPrimaryScene(message);

            case "NICKDATE":
                if (onPopup) {
                    if (!playerSetupPopup.isChildPresent(setupErrorText))
                        playerSetupPopup.addChildren(setupErrorText);

                    setupErrorText.setText(message);
                }
                else
                    printOnPrimaryScene(message);
                break;
            case "STARTPLAYER":
            case "BUILDERCOLOR":
                setupErrorText.setText(message);
                if (!choiceSetupPopup.isChildPresent(setupErrorText))
                    choiceSetupPopup.addChildren(setupErrorText);
                break;
            case "MATCHGODS":
            case "PLAYERGOD":
                if (onPopup) {
                    setupErrorText.setText(message);
                    AnchorPane.setBottomAnchor(setupErrorText, Gui.marginLength/2);
                    AnchorPane.setLeftAnchor(setupErrorText, Gui.marginLength/2);
                    if (!godCardsPopup.isPresentOnBottom(setupErrorText))
                        godCardsPopup.addErrorMessage(setupErrorText);
                }
                else
                    printOnPrimaryScene(message);
                break;
            case "BUILDERPLACEMENT":
            case "STEP":
            case "MOVE":
            case "BUILD":
                printOnPrimaryScene(message);
                break;
        }
    }

    /**
     * Private method used to add a message on the primary scene (in bottomMessagesVBox)
     * @param message the message to print
     */
    private void printOnPrimaryScene(String message) {

        Text messageText = new Text(message);
        messageText.setFont(stdFont);
        messageText.setFill(Color.RED);

        if (numMessages >= maxMessagesShown) {
            numMessages = 0;
            Platform.runLater(() -> bottomMessagesVBox.getChildren().clear());
        }
        Platform.runLater(() -> bottomMessagesVBox.getChildren().add(messageText));
        numMessages++;
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

        super.askNumberOfPlayers();

        Platform.runLater(()-> homePane.getChildren().remove(connecting));
        if (homePane.getChildren().contains(connectionErrorText))
            Platform.runLater(()-> homePane.getChildren().remove(connectionErrorText));

        Set<String> possibleNumPlayers = new HashSet<>();

        for (int i = minNumberOfPlayers; i <= maxNumberOfPlayers; i++)
            possibleNumPlayers.add(String.valueOf(i));

        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        Platform.runLater(() -> {
            choiceSetupPopup = new ChoicePopup(primaryStage, possibleNumPlayers,
                    "You're the first player! Select number of players: ", "New match", choiceBox,
                    windowEvent -> popupClosing(choiceSetupPopup, windowEvent));
            choiceSetupPopup.getSubmit().setOnMouseClicked(mouseEvent -> {

                notifyNumberOfPlayers(Integer.parseInt(choiceBox.getValue()));
                choiceSetupPopup.close();
            });
        });
    }

    /**
     * This private method is used to set the handler of a windowEvent, so that, except during the connection phase,
     * an alert will open on close request to warn the user that closing that window he'll leave the game
     * @param popup stage to close, on which the event happened
     * @param windowEvent event to handle
     */
    private void popupClosing (Stage popup, WindowEvent windowEvent) {
        if (!getState().equals(ViewState.CONNECTION)) {
            if (confirmQuit()) {
                popup.close();
                primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
            } else
                windowEvent.consume();
        }
        else
            popup.close();
    }

    /**
     * Creates a PlayersSetupPopup to make each player insert their nickname and birthday date, with two TextFields
     * whose content is sent to the server after the submit button is pressed
     */
    @Override
    public void askNickAndDate() {

        super.askNickAndDate();

        TextField nickInsertion = new TextField ("nickname");
        TextField birthdayInsertion = new TextField("birthday");
        nickInsertion.setFont(stdFont);
        nickInsertion.setStyle("-fx-text-inner-color: white; -fx-control-inner-background: steelblue; -fx-accent: lightsalmon;");
        birthdayInsertion.setStyle("-fx-text-inner-color: white; -fx-control-inner-background: steelblue; -fx-accent: lightsalmon;");
        birthdayInsertion.setFont(stdFont);

        Platform.runLater(() -> {
            playerSetupPopup = new PlayerSetupPopup(primaryStage, nickInsertion, birthdayInsertion,
                    windowEvent -> popupClosing(playerSetupPopup, windowEvent));

            playerSetupPopup.getSubmit().setOnMouseClicked(mouseEvent -> {
                String nickname = nickInsertion.getText();
                String birthday = birthdayInsertion.getText();
                if (nickname.length() > maxNicknameLength) {
                    printMessage("Too long nickname. Max lenght: " + maxNicknameLength, true);
                }
                else if (nickname.isEmpty() || birthday.isEmpty()) {

                    printMessage("You must enter a name and a date", true);
                }
                else {
                    setNickname(nickname);
                    setDate(birthday);
                    notifyNewPlayer(getNickname(), getDate());
                }
            });
        });
    }

    /**
     * Sends a GodCardsPopup to the Challenger with all the possible cards, sent through godDescriptionsParam
     * @param numOfPlayers number of the players of the match, and also of the godCards to choose
     */
    @Override
    public void chooseMatchGodCards(int numOfPlayers, Map<String, String> godDescriptionsParam) {

        super.chooseMatchGodCards(numOfPlayers, godDescriptionsParam);

        challenger = true;
        Platform.runLater(() -> {
            godCardsPopup = GodCardsPopup.getInstance(primaryStage, numOfPlayers, godDescriptionsParam,
                    windowEvent -> popupClosing(godCardsPopup, windowEvent));

            godCardsPopup.getSubmit().setOnMouseClicked(mouseEvent -> {
                if (godCardsPopup.getSelectionsNum() == numOfPlayers)
                    notifyMatchGodCardsChoice(getNickname(), godCardsPopup.getChosenGodCards());
                else
                    printMessage("Not all cards have been chosen yet", true);
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

        super.askGodCard(godDescriptions, chosenCards);

        if (godCardsPopup != null)
            godCardsPopup.resetPopup();

        matchGodCards = godDescriptions;

        Map<String, String> availableGods = new HashMap<>();
        for(String godName : godDescriptions.keySet().stream().filter(godName -> !chosenCards.contains(godName)).collect(Collectors.toSet()))
            availableGods.put(godName, godDescriptions.get(godName));

        Platform.runLater(() -> {
            godCardsPopup = GodCardsPopup.getInstance(primaryStage, godsForPlayer, availableGods,
                    windowEvent -> popupClosing(godCardsPopup, windowEvent));

            godCardsPopup.getSubmit().setOnMouseClicked(mouseEvent -> {
                if (godCardsPopup.getSelectionsNum() == godsForPlayer)
                    notifyGodCardChoice(getNickname(), godCardsPopup.getChosenGodCards().iterator().next());
                else
                    printMessage("GodCard still not chosen", true);
            });
        });
    }

    /**
     * Creates a ChoicePopup to make the Challenger choose the first player of the match
     * @param players list of players ready to play
     */
    @Override
    public void chooseStartPlayer(Set<String> players) {

        super.chooseStartPlayer(players);

        ChoiceBox<String> choiceBox= new ChoiceBox<>();
        Platform.runLater(() -> {
            choiceSetupPopup = new ChoicePopup(primaryStage, players, "Choose the first player", "StartPlayer", choiceBox,
                    windowEvent -> popupClosing(choiceSetupPopup, windowEvent));
            choiceSetupPopup.getSubmit().setOnMouseClicked(mouseEvent -> notifySetStartPlayer(getNickname(), choiceBox.getValue()));
        });
    }

    /**
     * Creates a ChoicePopup to make each player choose his color
     * @param chosenColors already chosen colors, not to be showed as possibilities
     */
    @Override
    public void askBuilderColor(Set<String> chosenColors) {

        super.askBuilderColor(chosenColors);

        Set<String> availableColors = Stream.of(Builder.BuilderColor.values()).map(Enum::name).collect(Collectors.toSet());
        availableColors.removeAll(chosenColors);

        Set<String> printableColors = new HashSet<>();
        for (String s: availableColors)
            printableColors.add(s.replace('_',' ').toLowerCase());

        setState(ViewState.BUILDERCOLOR);

        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        Platform.runLater(() -> {
            choiceSetupPopup = new ChoicePopup(primaryStage, printableColors, "Choose builders color", "Builders color", choiceBox,
                    windowEvent -> popupClosing(choiceSetupPopup, windowEvent));
            choiceSetupPopup.getSubmit().setOnMouseClicked(mouseEvent ->
                    notifyColorChoice(getNickname(), choiceBox.getValue().replace(' ','_').toUpperCase()));
        });
    }

    /**
     * For each cell in the tile (map), if the state is BUILDERPLACEMENT and the cell is not already occupied,
     * clicking on it a new builder is added calling the method createBuilder in GuiMap.
     * This method updates also occupiedCells
     */
    @Override
    public void placeBuilders () {

        super.placeBuilders();
        printMessage("Click cells to place your builders", false);

        for (int i = 0; i < tile.getChildren().size(); i++) {

            tile.getChildren().get(i).setOnMouseClicked(mouseEvent -> {

                if (getState().toString().equals("BUILDERPLACEMENT")) {

                    boolean free = true;
                    //searches for the index of the cell on which the user clicked
                    int index = 0;
                    StackPane eventSource = (StackPane) mouseEvent.getSource();
                    for (Node node : tile.getChildren()) {
                        if (!node.equals(eventSource))
                            index++;
                        else
                            break;
                    }

                    for (String player : gameMap.getOccupiedCells().keySet()) {
                        if (gameMap.getOccupiedCells().get(player).contains(GameMap.indexToCoord(index)))
                            free = false;
                    }

                    ArrayList<Coordinates> currPlayerBuildersPositions = gameMap.getOccupiedCells().get(getNickname());

                    //if the clicked cell is free and eventually different from the other one chosen by the player
                    if (free && (currPlayerBuildersPositions == null ||
                            !currPlayerBuildersPositions.get(GameMap.firstBuilderIndex).equals(GameMap.indexToCoord(index)))) {

                        if (currPlayerBuildersPositions != null) {

                            gameMap.setOccupiedCells(getNickname(),null, GameMap.indexToCoord(index));
                            currPlayerBuildersPositions = gameMap.getOccupiedCells().get(getNickname());
                            notifySetupBuilders(getNickname(), currPlayerBuildersPositions.get(GameMap.firstBuilderIndex),
                                    currPlayerBuildersPositions.get(GameMap.secondBuilderIndex));
                        }

                        else
                            gameMap.setOccupiedCells(getNickname(), GameMap.indexToCoord(index), null);
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

        super.chooseNextStep(possibleSteps);

        Label chooseStep = new Label ("Choose the next step: ");
        chooseStep.setFont(stdFont);
        ChoiceBox<String> stepChoice = new ChoiceBox<>();
        for (String step: possibleSteps)
            stepChoice.getItems().add(step);
        // set a default value
        if (!possibleSteps.isEmpty()) {
            stepChoice.setValue(possibleSteps.iterator().next());
            stepChoice.setStyle("-fx-background-color: tomato; -fx-border-color: brown; -fx-mark-color: brown; -fx-border-radius: 20; -fx-background-radius: 20;");
        }

        Platform.runLater(() -> dialogRegion.getChildren().addAll(chooseStep, stepChoice));

        Button okBtn = new GuiButton("Ok", submitButton,  mouseEvent -> {
            notifyStepChoice(getNickname(), stepChoice.getValue());
            Platform.runLater(() -> dialogRegion.getChildren().clear());
        }, submitButtonPressed);
        setPrimarySceneButtonBinding(okBtn);
        okBtn.setTextFill(Color.WHITESMOKE);
        Platform.runLater(()-> dialogRegion.getChildren().add(okBtn));
    }

    /**
     * Sets the correct binding for a button on the primary scene
     * @param button button to bind
     */
    void setPrimarySceneButtonBinding (Button button) {

        button.prefWidthProperty().bind(primaryScene.widthProperty().divide(11));
        button.prefHeightProperty().bind(primaryScene.heightProperty().divide(15));
    }


    /**
     * Shows the possible destinations for a move for both builders if the builder hasn't been chosen and gives the possibility
     * to choose the turn builder. Otherwise, it allows to move with the chosen builder calling moveOnBuilderChosen()
     */
    @Override
    public void move() {

        if (gameMap.getChosenBuilderNum() == 0) {

            //getChosenBuilderNum() returns 0 until a turn builder is chosen, until the user don't choose it prints all the possible dsts
            ((GuiMap)gameMap).setCurrClickCellHandler(null);
            gameMap.setPossibleDst(possibleDstBuilder1, possibleDstBuilder2);

            printMessage("Select your turn builder", false);
            chooseTurnBuilder();
        }

        else {
            printMessage("Select where you want to move", false);
            moveOnBuilderChosen();
        }
    }

    /**
     * This method makes the user choose the turn builder and than calls moveOnBuilderChosen or buildToDst to complete the first step
     * of the turn. After the choice, both the clicked builder and the other one are reset (can't be selected anymore)
     * and the current turn builder position is set, with the chosen builder number.
     */
    private void chooseTurnBuilder() {

        boolean first = true;
        for (Coordinates coord: gameMap.getOccupiedCells().get(getNickname())) {

            //verifies that the occupiedCell does not contain a locked builder
            if ((first && (!possibleDstBuilder1.isEmpty() || !possibleDstBuilder1forDome.isEmpty())) ||
                    (!first && (!possibleDstBuilder2.isEmpty() || !possibleDstBuilder2forDome.isEmpty()))) {

                int currBuilderIndexStack;
                StackPane tmp = (StackPane) tile.getChildren().get(GameMap.coordinatesToIndex(coord));
                currBuilderIndexStack = getBuilderIndexInStack(coord);

                tmp.getChildren().get(currBuilderIndexStack).setOnMouseEntered(mouseEvent -> {
                    if (!getState().equals(ViewState.CONNECTION)) {
                        ImageView builderToHandle = (ImageView) mouseEvent.getSource();
                        DropShadow shadow = new DropShadow();
                        builderToHandle.setEffect(shadow);
                    }
                });

                tmp.getChildren().get(currBuilderIndexStack).setOnMouseExited(mouseEvent -> {
                    ImageView builderToHandle = (ImageView) mouseEvent.getSource();
                    builderToHandle.setEffect(null);
                });

                tmp.getChildren().get(currBuilderIndexStack).setOnMouseClicked(mouseEvent -> {

                    if (!getState().equals(ViewState.CONNECTION)) {
                        //takes the builder and sets its opacity to show that it's been selected
                        ImageView clickedBuilder = (ImageView) mouseEvent.getSource();
                        clickedBuilder.setOpacity(Gui.selectionOpacity);
                        clickedBuilder.setOnMouseEntered(null);
                        clickedBuilder.setOnMouseClicked(null);

                        //controls if the chosen builder is the first one or the second one of current player's builders
                        Coordinates coordOfFirstBuilderCell = gameMap.getOccupiedCells().get(getNickname()).get(GameMap.firstBuilderIndex);
                        StackPane firstBuilderCell = (StackPane) tile.getChildren().get(GameMap.coordinatesToIndex(coordOfFirstBuilderCell));
                        Coordinates coordOfSecondBuilderCell = gameMap.getOccupiedCells().get(getNickname()).get(GameMap.secondBuilderIndex);
                        StackPane secondBuilderCell = (StackPane) tile.getChildren().get(GameMap.coordinatesToIndex(coordOfSecondBuilderCell));

                        int currentBuilderIndexInStack = getBuilderIndexInStack(coordOfFirstBuilderCell);

                        //if the clicked builder is the first one, I reset the not clicked one (second one)
                        if (firstBuilderCell.getChildren().get(currentBuilderIndexInStack).equals(clickedBuilder)) {

                            int secondBuilderIndexInStack = getBuilderIndexInStack(coordOfSecondBuilderCell);

                            ImageView secondBuilder = (ImageView) secondBuilderCell.getChildren().get(secondBuilderIndexInStack);
                            secondBuilder.setOnMouseClicked(null);
                            secondBuilder.setOnMouseEntered(null);
                            gameMap.setCurrentTurnBuilderPos(gameMap.getOccupiedCells().get(getNickname()).get(GameMap.firstBuilderIndex));
                            gameMap.setChosenBuilderNum(1);
                        } else {

                            int firstBuilderIndexInStack = getBuilderIndexInStack(coordOfFirstBuilderCell);
                            ImageView firstBuilder = (ImageView) firstBuilderCell.getChildren().get(firstBuilderIndexInStack);
                            firstBuilder.setOnMouseClicked(null);
                            firstBuilder.setOnMouseEntered(null);
                            gameMap.setCurrentTurnBuilderPos(gameMap.getOccupiedCells().get(getNickname()).get(GameMap.secondBuilderIndex));
                            gameMap.setChosenBuilderNum(2);
                        }

                        gameMap.setPossibleDst(null, null);

                        if (getState().toString().equals("MOVE")) {
                            printMessage("Select where you want to move", false);
                            moveOnBuilderChosen();
                        } else {
                            printMessage("Select where you want to build", false);
                            if ((!possibleDstBuilder1forDome.isEmpty() && gameMap.getChosenBuilderNum() == 1) ||
                                    (!possibleDstBuilder2forDome.isEmpty() && gameMap.getChosenBuilderNum() == 2))
                                askForDome();
                            buildToDst();
                        }
                    }
                });
                first = false;
            }
        }
    }

    /**
     * On turn builder chosen, shows the possible destinations and adds an eventHandler on them in order to notify the chosen
     * move destination after being clicked. It updates also the currentTrunBuilderPos.
     */
    private void moveOnBuilderChosen() {

        ((GuiMap)gameMap).setCurrClickCellHandler(mouseEvent -> {
            int dstIndex = 0;
            StackPane clickedCell = (StackPane) mouseEvent.getSource();

            //finds the index of the clickedCell
            for (Node node : tile.getChildren()) {
                if (!node.equals(clickedCell))
                    dstIndex++;
                else
                    break;
            }

            notifyMove(getNickname(), gameMap.getCurrentTurnBuilderPos(), GameMap.indexToCoord(dstIndex));
            gameMap.setCurrentTurnBuilderPos(GameMap.indexToCoord(dstIndex));
        });

        gameMap.setPossibleDst(possibleDstBuilder1, possibleDstBuilder2);
    }

    /**
     * On turn builder chosen, shows the possible destinations to build and adds an eventHandler on them to notify the
     * build destination choice.
     */
    private void buildToDst() {

        ((GuiMap)gameMap).setCurrClickCellHandler(mouseEvent -> {

            //if the turnBuilder have been chosen
            if (gameMap.getCurrentTurnBuilderPos() != null) {
                int index = 0;

                StackPane clickedCell = (StackPane) mouseEvent.getSource();

                //finds the index of the clickedCell
                for (Node node : tile.getChildren()) {
                    if (!node.equals(clickedCell))
                        index++;
                    else
                        break;
                }
                notifyBuild(getNickname(), gameMap.getCurrentTurnBuilderPos(), GameMap.indexToCoord(index), buildDome);
            }
        });
        if (!buildDome)
            gameMap.setPossibleDst(possibleDstBuilder1, possibleDstBuilder2);
        else
            gameMap.setPossibleDst(possibleDstBuilder1forDome, possibleDstBuilder2forDome);
    }

    /**
     * This method sets the buildDome value to true or false after the current player clicks on yes or no buttons
     */
    private void askForDome() {

        Label domeRequest = new Label ("Do you want to build a dome? ");
        domeRequest.setFont(stdFont);
        Platform.runLater(() -> dialogRegion.getChildren().add(domeRequest));

        Button yesBtn = new GuiButton("YES", submitButton, mouseEvent ->  {
            buildDome = true;
            afterDomeChoice();
        }, submitButtonPressed);

        Button noBtn = new GuiButton ("NO", submitButton, mouseEvent -> {
            buildDome = false;
            afterDomeChoice();
        }, submitButtonPressed);

        yesBtn.setTextFill(Color.WHITESMOKE);
        setPrimarySceneButtonBinding(yesBtn);
        noBtn.setTextFill(Color.WHITESMOKE);
        setPrimarySceneButtonBinding(noBtn);
        Platform.runLater(()-> dialogRegion.getChildren().addAll(yesBtn, noBtn));
        dialogRegion.setAlignment(Pos.CENTER);
    }

    /**
     * Shows the possible destinations after the choice of building a dome or not (if yes, shows just where a dome can
     * be built), then it proceeds with the BUILD step
     */
    private void afterDomeChoice() {

        Platform.runLater(() -> dialogRegion.getChildren().clear());

        ((GuiMap)gameMap).setCurrClickCellHandler(null);
        if (!buildDome)
            gameMap.setPossibleDst(possibleDstBuilder1, possibleDstBuilder2);
        else
            gameMap.setPossibleDst(possibleDstBuilder1forDome, possibleDstBuilder2forDome);

        if (gameMap.getChosenBuilderNum() == 0) {
            printMessage("Select your turn builder",false);
            chooseTurnBuilder();
        }
        else {
            printMessage("Select where you want to build", false);
            buildToDst();
        }
    }

    /**
     * If the builder has not been chosen yet, if the player can build a dome somewhere, asks for building a dome or not,
     * otherwise shows the possible destinations and to select a builder.
     * If the builder has been chosen, if it can build a dome somewhere, asks for building a dome or not, otherwise
     * calls "buildToDst" to show the possible destinations for that builder and make them clickable
     */
    @Override
    public void build() {

        if (gameMap.getChosenBuilderNum() == 0) {

            //if the player can build a dome somewhere, he's asked what he wants to build
            if (!possibleDstBuilder1forDome.isEmpty() || !possibleDstBuilder2forDome.isEmpty())
                askForDome();
            else {
                ((GuiMap)gameMap).setCurrClickCellHandler(null);
                if (!buildDome)
                    gameMap.setPossibleDst(possibleDstBuilder1, possibleDstBuilder2);
                else
                    gameMap.setPossibleDst(possibleDstBuilder1forDome, possibleDstBuilder2forDome);
                printMessage("Select your turn builder", false);
                chooseTurnBuilder();
            }
        }

        else {

            if ((!possibleDstBuilder1forDome.isEmpty() && gameMap.getChosenBuilderNum() == 1) ||
                    (!possibleDstBuilder2forDome.isEmpty() && gameMap.getChosenBuilderNum() == 2))
                askForDome();

            else {
                printMessage("Select where you want to build", false);
                buildToDst();
            }
        }
    }

    /**
     * If the build is successful, resets the possible destinations for the player who did it and creates
     * the new building for the other ones. Otherwise, prints a message of error.
     * @param nickname current player who's building
     * @param src builder position in coordinates
     * @param dst where the player has choose to build
     * @param dome true if the player wanted to build/has built a dome
     * @param result true if the build was successful
     */
    @Override
    public void onBuilderBuild(String nickname, Coordinates src, Coordinates dst, boolean dome, boolean result) {

        super.onBuilderBuild(nickname,src, dst, dome, result);

        if (!result)
            printMessage("Could not build", false);
    }

    /**
     * If the move is successful, resets the possible destinations for the player who did it, updates the
     * currentTurnBuilderPos to the dest of the move, updates the occupied cells and does the effective move
     * through GuiMap method moveBuilder
     * @param nickname of the current player
     * @param src coordinates of the previous position of the builder
     * @param dst coordinates of the destination of the move step
     * @param result true if the movement is successful
     */
    @Override
    public void onBuilderMovement(String nickname, Coordinates src, Coordinates dst, boolean result) {
        super.onBuilderMovement(nickname, src, dst, result);
        if(result) {
            if(getNickname().equals(nickname))
                gameMap.setPossibleDst(null, null);
        }
        else
            printMessage("Could not move.", false);
    }

    /**
     * Updates the possible set of coordinates of cells in which the player in the BUILD step can build a new floor
     */
    @Override
    public void updatePossibleBuildDst(Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2,
                                       Set<Coordinates> possibleDstBuilder1forDome, Set<Coordinates> possibleDstBuilder2forDome) {


        super.updatePossibleBuildDst(possibleDstBuilder1, possibleDstBuilder2,possibleDstBuilder1forDome, possibleDstBuilder2forDome);
        buildDome = false;
        build();
    }

    /**
     * Updates the possible set of coordinates of cells in which the player in the MOVE step can move his builders
     */
    @Override
    public void updatePossibleMoveDst(Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2) {

        super.updatePossibleMoveDst(possibleDstBuilder1, possibleDstBuilder2);
        if (gameMap.getChosenBuilderNum() == 0) {
            ((GuiMap)gameMap).setCurrClickCellHandler(null);
            gameMap.setPossibleDst(possibleDstBuilder1, possibleDstBuilder2);
        }
        move();
    }

    /**
     * After a successful placement, the handler for MouseClicked event is reset, for the player who did the placement
     * is showed a message of successful placement and for the others the gameMap is updated
     * @param nickname of the player who just placed his builders
     * @param positionBuilder1 coordinates of the first builder
     * @param positionBuilder2 coordinates of the second one
     * @param result true if the placement of the builders is correct
     */
    @Override
    public void onBuildersPlacedUpdate(String nickname, Coordinates positionBuilder1, Coordinates positionBuilder2, boolean result) {

        super.onBuildersPlacedUpdate(nickname, positionBuilder1, positionBuilder2, result);
        if (result) {
            //resets the handler of cell clicked
            for (Node node : tile.getChildren())
                node.setOnMouseClicked(null);

            if (getNickname().equals(nickname))
                printMessage("Builders positioned correctly.", false);
        }
        else if (getNickname().equals(nickname)) {
            printMessage("ERROR in builders placement", false);
            gameMap.removePlayer(nickname);
        }

    }

    /**
     * If result is true, shows a message of step choice to the other players, otherwise shows an error for the player
     * who did the step choice
     * @param nickname the player who chose the step from a list of possible ones
     * @param step chosen step
     */
    @Override
    public void onChosenStep(String nickname, String step, boolean result) {

        if(result)
            printMessage("Step correctly chosen", false);
        else
            printMessage("ERROR: wrong step.", false);
    }

    /**
     * Closes the choiceSetupPopup of color choice for the player who has successfully chosen his color
     * @param nickname of the player who chose the color
     * @param color chosen color
     * @param result true if successful, false if unsuccessful
     */
    @Override
    public void onColorAssigned(String nickname, String color, boolean result) {

        super.onColorAssigned(nickname, color, result);

        if (getNickname().equals(nickname)) {
            if (result)
                Platform.runLater(()->choiceSetupPopup.close());
            else
                printMessage("Invalid insertion of color.", true);
        }
    }

    /**
     * Prints endGame messages of loss and victory and a normal message to inform about who won
     * @param winnerNickname name of the player who won
     */
    @Override
    public void onEndGameUpdate(String winnerNickname) {

        Platform.runLater(() -> dialogRegion.getChildren().clear());

        if (!getNickname().equals(winnerNickname)) {

            printMessage("Player " + winnerNickname + " wins!", false);
            new EndGameMessage("YOU LOSE", Color.BLUE, dialogRegion, playAgainBtn);
        }
        else
            new EndGameMessage("YOU WIN!", Color.LIGHTSALMON, dialogRegion, playAgainBtn);
    }

    /**
     * Private method used to reset primaryScene and homeScene for a new match. It creates also a new GuiMap.
     */
    private void resetAll () {

        if (homePane.getChildren().contains(connectionErrorText))
            Platform.runLater(()-> homePane.getChildren().remove(connectionErrorText));

        Platform.runLater(() -> primaryStage.setScene(homeScene));
        activatePlayBtn();
        Platform.runLater(() -> playersRegion.getChildren().clear());
        playersRegion.setBorder(null);
        playersRegion.setBackground(null);

        tile = new TilePane();
        gameMap = new GuiMap(tile, primaryScene);
        root.setCenter(tile);

        Platform.runLater(() -> bottomAnchorPane.getChildren().clear());
        Platform.runLater(() -> dialogRegion.getChildren().clear());
    }

    /**
     * Prints a generic message of error passed by server
     */
    @Override
    public void onWrongInsertionUpdate(String error) {
        if (getState().equals(ViewState.NICKDATE))
            printMessage(error, true);
        else
            printMessage(error, false);
    }

    /**
     * Prints a message of wrong number insertion and asks again the num of players.
     */
    @Override
    public void onWrongNumberInsertion() {
        super.onWrongNumberInsertion();
        printMessage("Invalid number Insertion.", false);
        askNumberOfPlayers();
    }

    /**
     * Closes godCardsPopup if result is true, otherwise prints an error message
     * @param nickname name of the player who chose his godCard
     * @param card chosen GodCard
     * @param result true or false if the choice was successful or not
     */
    @Override
    public void onGodCardAssigned(String nickname, String card, boolean result) {
        super.onGodCardAssigned(nickname, card, result);

        if(getNickname().equals(nickname)) {
            if (result) {
                printMessage("GodCard assigned correctly.", false);
                Platform.runLater(() -> godCardsPopup.close());
                godCardsPopup.resetPopup();
            }
            else
                printMessage("Invalid insertion of godCard.", true);
        }
        else if (result) {
            printMessage(nickname + " chose " + card + " godCard", false);
        }
    }

    /**
     * For the challenger, if result is positive (result = true), closes the godCardsPopup, otherwise prints an error message.
     * @param godCardsToUse the godCards of the match chosen by the challenger
     * @param result true if the insertion was successful
     */
    @Override
    public void onMatchGodCardsAssigned(Set<String> godCardsToUse, boolean result) {

        if (challenger) {
            if (result) {
                printMessage("Godcards correctly chosen", false);
                Platform.runLater(() -> godCardsPopup.close());
            }
            else
                printMessage("Error assigning cards", true);
        }
    }

    /**
     * Prints a message to notify the entry of a new player and closes his player setup popup.
     * If the result is false prints a message of error and resets nick and date
     */
    @Override
    public void onPlayerAdded(String nickname, boolean result) {

        boolean onPopup = false;
        if(result) {
            if (getNickname() != null && getNickname().equals(nickname))
                Platform.runLater(() -> playerSetupPopup.close());

                //if Nickname == null, it means the setUp player popup is still open
            else  if (getNickname() == null)
                onPopup = true;
            printMessage (nickname + " joined the game!", onPopup);
        }
    }

    /**
     * Super removes the player from the map of players - godCards.
     * This method sends a normal message to all the players about who has lost, and an endgame one to the player who lost
     * It also removes the loser builders from the map through the method remove builders of gameMap
     * @param nickname player who lost
     */
    @Override
    public void onLossUpdate(String nickname) {

        super.onLossUpdate(nickname);

        if (!getNickname().equals(nickname)) {
            printMessage(nickname + " lost!", false);
        }
        else {
            Platform.runLater(() -> dialogRegion.getChildren().clear());
            new EndGameMessage("YOU LOSE", Color.BLUE, dialogRegion, playAgainBtn);
        }
        matchGodCards.remove(nickname);
        setPlayersRegion();
    }

    /**
     * Resets the turn builder if it's not null (it's not the first turn), glows the name of the current player
     * in the nameTag and resets the old one.
     * @param nickname of the current player
     */
    @Override
    public void onPlayerTurn(String nickname) {
        super.onPlayerTurn(nickname);
        String state = getState().toString();

        if (state.equals("BUILD") || state.equals("MOVE") || state.equals("STEP") || state.equals("BUILDERPLACEMENT")) {

            for (String player : playersNameTags.keySet())
                playersNameTags.get(player).setEffect(null);

            Glow glow = new Glow();
            glow.setLevel(1);
            playersNameTags.get(nickname).setEffect(glow);

        }
        printMessage("Turn of: " + nickname, false);
    }

    /**
     * Prints the message of starting player chosen.
     * @param nickname starting player
     * @param result true if the choice was successful
     */
    @Override
    public void onStartPlayerSet(String nickname, boolean result) {
        if (challenger) {
            if (result)
                Platform.runLater(() -> choiceSetupPopup.close());
            else
                printMessage("ERROR: could not set starting player.", false);
        }
        if (result)
            printMessage("The starting player is " + nickname, false);
    }

    /**
     * When the match is in the state SETUP_BUILDERS, the playersRegion is featured with players nicknames and gods,
     * when the match is in SETUP_PLAYERS, the user is advised to wait for players to enter and the bottom messages VBox
     * is set. Also the scene is changed from homeScene to primaryScene.
     * @param currState currState of the model
     */
    @Override
    public void onStateUpdate(Model.State currState) {

        switch (currState.toString()) {

            case "SETUP_BUILDERS":

                playersRegion.setBorder(new Border(new BorderStroke(SEA, SEA, SEA, Color.TRANSPARENT, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                        BorderStrokeStyle.SOLID, null, CornerRadii.EMPTY, BorderStroke.DEFAULT_WIDTHS, playersRegionInsets)));

                playersRegion.setBackground(new Background(new BackgroundFill(Color.ANTIQUEWHITE, null, playersRegionInsets)));

                setPlayersRegion();

                HBox bottomBtns = new HBox();
                bottomBtns.setSpacing(marginLength);

                Button godCardsBtn = new GuiButton("GodCards", buttonCoralSrc, mouseEvent ->
                        GodCardsPopup.getInstance(primaryStage, 0, matchGodCards, null), buttonCoralPressedSrc);

                Button quitBtn = new GuiButton("QUIT", buttonCoralSrc, mouseEvent -> {
                    notifyDisconnection();
                    primaryStage.close();
                    Platform.exit();
                    System.exit(0);
                }, buttonCoralPressedSrc);

                setPrimarySceneButtonBinding(godCardsBtn);
                setPrimarySceneButtonBinding(quitBtn);
                Platform.runLater(() -> bottomAnchorPane.getChildren().add(bottomBtns));
                AnchorPane.setBottomAnchor(bottomBtns, Gui.marginLength);
                AnchorPane.setRightAnchor(bottomBtns, Gui.marginLength);
                Platform.runLater(() -> bottomBtns.getChildren().addAll(godCardsBtn, quitBtn));
                break;

            case "SETUP_PLAYERS":
                this.bottomMessagesVBox = new VBox();
                bottomMessagesVBox.setSpacing(marginLength / 10);
                bottomAnchorPane.getChildren().add(bottomMessagesVBox);
                AnchorPane.setLeftAnchor(bottomMessagesVBox, marginLength);
                AnchorPane.setTopAnchor(bottomMessagesVBox, marginLength / 4);
                bottomMessagesVBox.setAlignment(Pos.CENTER);

                Platform.runLater(() -> primaryStage.minWidthProperty().bind(root.heightProperty().multiply((double) Gui.sceneWidth / Gui.sceneHeight)));
                Platform.runLater(() -> primaryStage.minHeightProperty().bind(root.widthProperty().divide((double) Gui.sceneWidth / Gui.sceneHeight)));
                Platform.runLater(() -> primaryStage.setScene(primaryScene));

                Label label = new Label("Waiting for players... ");
                setFadeTransition(label);

                playersRegion.setAlignment(Pos.CENTER);
                Platform.runLater(() -> playersRegion.getChildren().add(label));
                if (homePane.getChildren().contains(connecting))
                    Platform.runLater(()-> homePane.getChildren().remove(connecting));
                break;
        }
    }

    /**
     * Predefined alert popup to confirm the leaving of the match, returns true if the user clicks on YES button,
     * false if it clicks on CANCEL and so the closure is canceled
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
        alert.setResizable(true);
        return alert.showAndWait().orElse(cancel) == yes;
    }

    /**
     * Prints a message of connection error
     * @param message error message received from server
     */
    @Override
    public void onConnectionError(String message) {
        Platform.runLater(()-> homePane.getChildren().remove(connecting));
        printMessage(message, false);
        activatePlayBtn();
    }

    /**
     * Notifies the player that they've been disconnected and creates a play again button, so that each user can choose
     * to play again or to quit the game.
     */
    @Override
    public void onDisconnection() {

        super.onDisconnection();

        if (!homePane.getChildren().contains(connecting))
            printMessage(warning + " You're disconnected " + warning, false);
        else
             Platform.runLater(()-> homePane.getChildren().remove(connecting));

        if (!dialogRegion.getChildren().contains(playAgainBtn))
            Platform.runLater(()-> dialogRegion.getChildren().add(playAgainBtn));

        ViewState currState = getState();
        if (currState.equals(ViewState.NICKDATE) || currState.equals(ViewState.MATCHGODS) ||
                currState.equals(ViewState.PLAYERGOD) || currState.equals(ViewState.BUILDERCOLOR))
            Platform.runLater(()->playersRegion.getChildren().clear());

        setState(ViewState.CONNECTION);
    }

    /**
     * This method is used to set the players region (left side of the root borderPane) with player names and nametags
     * images and their chosen godCards
     */
    private void setPlayersRegion () {

        boolean firstPlayer = true;
        Map<String, String> chosenGodCards = getChosenGodCardsForPlayer();
        Platform.runLater(() ->playersRegion.getChildren().clear());
        playersNameTags.clear();

        for (String player: chosenGodCards.keySet()) {

            if (!firstPlayer) {

                ImageView vs = new ImageView(new Image(getClass().getResourceAsStream(versusSrc)));
                vs.fitWidthProperty().bind(playersRegion.prefWidthProperty().subtract(playersRegionInsets.getRight()));
                vs.setPreserveRatio(true);
                Platform.runLater(() -> playersRegion.getChildren().add(vs));
            }

            firstPlayer = false;

            ImageView nameTag = new ImageView(new Image(getClass().getResourceAsStream(nameTagSrc)));
            nameTag.setPreserveRatio(true);
            nameTag.fitWidthProperty().bind(playersRegion.prefWidthProperty().subtract(playersRegionInsets.getRight()));

            StackPane tagImageText = new StackPane();

            Text text = new Text(player + ": " + chosenGodCards.get(player));
            text.setFont(stdFont);
            text.setStyle("-fx-font-weight: bold");

            playersNameTags.put(player, text);

            String color = getChosenColorsForPlayer().get(player);
            color = color.replaceAll("_","").toLowerCase();

            text.setFill(Color.web(color));

            Platform.runLater(() -> tagImageText.getChildren().addAll(nameTag, text));
            Platform.runLater(() -> playersRegion.getChildren().add(tagImageText));
        }
    }

    /**
     * Notifies the players that someone has disconnected
     * @param nickname of the player who disconnected, if it has already a nickname, null otherwise.
     */
    @Override
    public void onOpponentDisconnection(String nickname) {
        printMessage(warning + " " + nickname + " disconnected " + warning, false);
    }

    /**
     * Returns the position(index) of the builder in the StackPane (cell)
     * @param cellStackCoord coordinates of the cell with the builder
     */
    private int getBuilderIndexInStack(Coordinates cellStackCoord) {
        int result = 0;
        if (gameMap.getHeights().get(cellStackCoord) != 0)
            result = builderIndexInStack;

        return result;
    }
}


