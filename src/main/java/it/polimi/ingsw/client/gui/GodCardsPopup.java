package it.polimi.ingsw.client.gui;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.*;

/**
 * This class is a Stage which shows the cards contained in godCardsDescriptions keyValues(), used for
 * setup purpose, to choose match/player cards, and during the match to show matchGodCards powers.
 * In the latter case, it's important for the stage to be unique, so a singleton pattern is used.
 */
public class GodCardsPopup extends Stage {

    private final static int cardsWidth = 100;
    private static GodCardsPopup firstInstance;
    private final Set<String> chosenGodCards;
    private Button submit;
    private final TilePane tilePane;
    private final AnchorPane bottomPane;

    private final int maxSelectionsNum;
    private int selectionsNum;


    /**
     * This public method returns the unique instance of godCardsPopup if already created, otherwise creates it.
     */
    public static GodCardsPopup getInstance(Stage ownerStage, int maxSelections, Map<String, String> godCardsDescriptions,
                                            EventHandler<WindowEvent> handler) {
        if (firstInstance == null) {
            firstInstance = new GodCardsPopup(ownerStage, maxSelections, godCardsDescriptions, handler);
        }
        firstInstance.show();
        return firstInstance;
    }


    /**
     * The scene is composed by a VBox with:
     * a labelVBox with different labels depending on maxSelection and so on the state of the match,
     * a tilePane with the cards,
     * a bottom anchor pane with a space to show errors and eventually a submit button
     * @param ownerStage the Stage on which the popup'll appear
     * @param maxSelections maximum number of cards that can be selected
     * @param godCardsDescriptions map of cards and descriptions that have to be showed
     * @param handler to set the close request handler based on the state of the game
     */
    private GodCardsPopup (Stage ownerStage, int maxSelections, Map<String, String> godCardsDescriptions, EventHandler<WindowEvent> handler) {

        initOwner(ownerStage);
        maxSelectionsNum = maxSelections;
        setTitle("GodCards");
        setResizable(false);
        this.chosenGodCards = new HashSet<>();
        this.selectionsNum = 0;
        VBox vbox = new VBox();
        VBox labelBox = new VBox();
        Label label = new Label();
        bottomPane = new AnchorPane();

        tilePane = new TilePane();
        tilePane.setHgap(Gui.marginLength/2);
        tilePane.setVgap(Gui.marginLength/2);

        vbox.setStyle("-fx-background-color: lightslategrey");
        vbox.setBackground(new Background(new BackgroundFill(Color.LIGHTSLATEGREY, null, null)));

        //if the popup appears to choose the matchGods, it has to tell the player that he's the challenger
        if (maxSelections > Gui.godsForPlayer)
            label.setText("You're the Challenger of this match! Choose " + maxSelections + " godCards for the match:");

        else if (maxSelections == Gui.godsForPlayer)
            label.setText("Choose your GodCard from the available ones: ");

        else if (maxSelections == 0)
            label.setText("GodCards of the match: ");

        Label tooltipsNotice = new Label ("Hold your mouse over the godCard to see the power.");
        tooltipsNotice.setFont(new Font("Arial", Gui.fontSize));

        label.setTextFill(Color.WHITE);
        label.setFont(new Font("Arial", Gui.fontSize));
        labelBox.getChildren().add(label);
        labelBox.getChildren().add(tooltipsNotice);
        labelBox.setAlignment(Pos.CENTER);
        vbox.getChildren().add(labelBox);
        vbox.getChildren().add(tilePane);

        //if selectionsNum is 0, it means the match is in GAME state and I just want to see MatchCards and descriptions
        if (maxSelections != 0) {

            this.submit = new GuiButton("Submit", Gui.submitButton, null, Gui.submitButtonPressed);

            submit.setPrefWidth((float) Gui.sceneWidth/14);
            submit.setTextFill(Color.WHITESMOKE);
            AnchorPane.setRightAnchor(submit, Gui.marginLength/2);
            AnchorPane.setBottomAnchor(submit, Gui.marginLength/2);
            bottomPane.getChildren().add(submit);
            vbox.getChildren().add(bottomPane);
        }

        vbox.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, Color.TRANSPARENT, Color. TRANSPARENT,Color.TRANSPARENT,
                null, null, null, null, CornerRadii.EMPTY,
                BorderStroke.MEDIUM, new Insets(Gui.marginLength/2, Gui.marginLength/2, Gui.marginLength/2, Gui.marginLength/2))));
        vbox.setSpacing(Gui.marginLength/2);

        initializeImages(godCardsDescriptions);

        // only if the match is not in phase GAME, so it is a setup popup, it may create a "confirm quit" message
        if (maxSelections != 0)
            this.setOnCloseRequest(handler);
        Scene scene = new Scene(vbox);
        this.setScene(scene);
    }

    /**
     * Returns the number of selections, useful to know when the choice is completed
     * @return the number of cards selected from the user
     */
    protected int getSelectionsNum() {
        return selectionsNum;
    }

    /**
     * @return set of chosenGodCards, selected from the user
     */
    protected Set<String> getChosenGodCards() {
        return chosenGodCards;
    }

    /**
     * Adds a Text which'll show error messages
     * @param node text node
     */
    protected void addErrorMessage(Text node) {
        Platform.runLater(() -> bottomPane.getChildren().add(node));
    }

    /**
     * Lets you know if a node is present in the bottom pane
     * @param node node to search for
     * @return true if the node is present in the bottomPane (AnchorPane) of the stage
     */
    protected boolean isPresentOnBottom (Node node) {
        return bottomPane.getChildren().contains(node);
    }

    /**
     * Method which returns the submit button. Can be used, for example, to set the handler of an event from outside the class.
     * @return the submit button of the stage
     */
    protected Button getSubmit() {
        return submit;
    }

    /**
     * Resets the first instance giving the possibility to create a new, different godCardsPopup in the phase of setup
     */
    protected void resetPopup () {
        firstInstance = null;
    }

    /**
     * This private method creates the imageViews and adds them to the tilePane, setting also the eventHandler
     * for MouseClicked event depending on the number of cards already selected (a card can be so selected, deselected or ignored)
     * The descriptions are shown as tooltips installed on the imageViews.
     *
     * @param godCardsDescriptions map of godCards names and descriptions to be shown
     */
    private void initializeImages(Map<String, String> godCardsDescriptions) {
        //takes the keyset (names) of all the gods to show in the popup, based on the game phase and chosen ones
        for (String s: godCardsDescriptions.keySet()) {

            ImageView tmp = new ImageView (new Image (getClass().getResourceAsStream("/Images/" + s + ".png")));
            tmp.setFitWidth(cardsWidth);
            tmp.setPreserveRatio(true);

            tmp.setOnMouseClicked(mouseEvent -> {

                if (chosenGodCards.contains(s)) {
                    selectionsNum--;
                    tmp.setOpacity(1);
                    chosenGodCards.remove(s);
                }

                else if (selectionsNum < maxSelectionsNum) {
                    selectionsNum++;
                    tmp.setOpacity(Gui.selectionOpacity);
                    chosenGodCards.add(s);
                }

            });

            tilePane.getChildren().add(tmp);

            tmp.setOnMouseEntered(mouseEvent -> Tooltip.install(tmp, new Tooltip(godCardsDescriptions.get(s))));
        }
    }
}
