package it.polimi.ingsw.client.gui;

import javafx.application.Platform;
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
 */
public class GodCardsPopup extends Stage {

    private final static int cardsWidth = 100;
    private Set<String> chosenGodCards;
    private Button submit;
    private TilePane tilePane;
    private AnchorPane bottomPane;

    private int maxSelectionsNum;
    private int selectionsNum;

    /**
     * The scene is composed by a VBox with:
     * different labels depending on maxSelection and so on the state of the match
     * a tilePane with the cards
     * a bottom anchor pane with a space to show errors and eventually a submit button
     * @param ownerStage the Stage on which the popup'll appear
     * @param maxSelections maximum number of cards that can be selected
     * @param godCardsDescriptions map of cards and descriptions that have to be showed
     */
    public GodCardsPopup (Stage ownerStage, int maxSelections, Map<String, String> godCardsDescriptions) {

        initOwner(ownerStage);
        maxSelectionsNum = maxSelections;
        setTitle("GodCards");
        setResizable(false);
        this.chosenGodCards = new HashSet<>();
        this.selectionsNum = 0;
        VBox vbox = new VBox();
        Label label = new Label();
        bottomPane = new AnchorPane();
        submit = new Button("Submit");

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

        vbox.getChildren().addAll(label, tooltipsNotice);
        vbox.getChildren().add(tilePane);

        //if selectionsNum is 0, it means the match is in GAME state and I just want to see MatchCards and descriptions
        if (maxSelections != 0) {

            this.submit = new GuiButton("Submit", Gui.submitButton, bottomPane, null, Gui.submitButtonPressed);
            submit.setPrefWidth((float) Gui.sceneWidth/14);
            submit.setTextFill(Color.WHITESMOKE);

            AnchorPane.setRightAnchor(submit, Gui.marginLength/2);
            AnchorPane.setBottomAnchor(submit, Gui.marginLength/2);
            vbox.getChildren().add(bottomPane);
        }

        vbox.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, Color.TRANSPARENT, Color. TRANSPARENT,Color.TRANSPARENT,
                null, null, null, null, CornerRadii.EMPTY,
                BorderStroke.MEDIUM, new Insets(Gui.marginLength/2, Gui.marginLength/2, Gui.marginLength/2, Gui.marginLength/2))));
        vbox.setSpacing(Gui.marginLength/2);

        vbox.setAlignment(Pos.CENTER);

        initializeImages(godCardsDescriptions);

        // only if the match is not in phase GAME, so it is a setup popup, it creates a "confirmQuit" message
        if (maxSelections != 0) {
            this.setOnCloseRequest(windowEvent -> {
                if (Gui.confirmQuit()) {
                    this.close();
                    ownerStage.fireEvent(new WindowEvent(ownerStage, WindowEvent.WINDOW_CLOSE_REQUEST));
                } else
                    windowEvent.consume();
            });
        }

        Scene scene = new Scene(vbox);
        this.setScene(scene);
        this.show();
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
     * Adds a Text which'll show error messages if still not present
     * @param node text node
     */
    protected void addErrorMessage(Text node) {
        Platform.runLater(() -> bottomPane.getChildren().add(node));
    }

    /**
     * @param node node to search for
     * @return true if the node is present in the bottomPane of the stage
     */
    protected boolean isPresentOnBottom (Node node) {
        return bottomPane.getChildren().contains(node);
    }

    /**
     * @return the submit button used to set the handler of an event from outside of the class
     */
    protected Button getSubmit() {
        return submit;
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

            ImageView tmp = new ImageView (new Image (getClass().getResourceAsStream("/" + s + ".png")));
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
