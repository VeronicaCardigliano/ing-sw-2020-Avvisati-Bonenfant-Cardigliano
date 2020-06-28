package it.polimi.ingsw.client.gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
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
 * This class is a Popup which shows the cards contained in godCardsDescriptions keyValues(), used both for
 * setup purpose, to choose match/player cards, and during the match to show matchGodCards powers.
 * The base is a VBox with a tilePane with the cards, buttons and another VBox with the label (with different alignments)
 */
public class GodCardsPopup extends Stage {

    private final static int cardsWidth = 100;
    private Set<String> chosenGodCards;
    private Button submit;
    private TilePane tilePane;
    private AnchorPane bottomPane = new AnchorPane();

    private int maxSelectionsNum;
    private int selectionsNum;

    public GodCardsPopup (Stage ownerStage, int maxSelections, Map<String, String> godCardsDescriptions) {

        initOwner(ownerStage);
        maxSelectionsNum = maxSelections;
        setTitle("GodCards");
        setResizable(false);
        this.chosenGodCards = new HashSet<>();
        this.selectionsNum = 0;
        VBox vbox = new VBox();
        VBox labelBox = new VBox();
        Label label = new Label();
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
        labelBox.getChildren().add(label);
        labelBox.getChildren().add(tooltipsNotice);
        labelBox.setAlignment(Pos.CENTER);
        vbox.getChildren().add(labelBox);
        vbox.getChildren().add(tilePane);

        //if selectionsNum is 0, it means the match is in GAME state and i just want to see MatchCards and descriptions
        if (maxSelections != 0) {

            Tooltip notReady= new Tooltip("Not all cards have been chosen yet");
            submit.setBackground(new Background(new BackgroundImage(new Image("file:src/main/resources/btn_submit.png"), BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(0, 0, false, false, false, true))));

            submit.setPrefWidth((float) Gui.sceneWidth/14);
            submit.setTextFill(Color.WHITESMOKE);

            submit.setOnMouseEntered(mouseEvent -> {
                Button enteredButton = (Button) mouseEvent.getSource();
                DropShadow shadow = new DropShadow();
                enteredButton.setEffect(shadow);
                if (selectionsNum < maxSelectionsNum)
                    Tooltip.install(enteredButton, notReady);
                else
                    Tooltip.uninstall(enteredButton, notReady);
            });

            submit.setOnMouseExited(mouseEvent -> {
                Button enteredButton = (Button) mouseEvent.getSource();
                enteredButton.setEffect(null);
            });

            submit.setOnMousePressed(mouseEvent -> {
                Button pressedButton = (Button) mouseEvent.getSource();
                pressedButton.setBackground(new Background(new BackgroundImage(new Image(getClass().getResourceAsStream(Gui.submitButtonPressed)), BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(0, 0, false, false, false, true))));

            });

            submit.setOnMouseReleased(mouseEvent -> {
                Button pressedButton = (Button) mouseEvent.getSource();
                pressedButton.setBackground(new Background(new BackgroundImage(new Image(getClass().getResourceAsStream(Gui.submitButton)), BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(0, 0, false, false, false, true))));

            });

            bottomPane.getChildren().add(submit);
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

    protected int getSelectionsNum() {
        return selectionsNum;
    }

    protected Set<String> getChosenGodCards() {
        return chosenGodCards;
    }

    protected void addErrorMessage(Text node) {
        Platform.runLater(() -> bottomPane.getChildren().add(node));
    }

    protected boolean isPresentOnBottom (Node node) {
        return bottomPane.getChildren().contains(node);
    }

    protected Button getSubmit() {
        return submit;
    }

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
