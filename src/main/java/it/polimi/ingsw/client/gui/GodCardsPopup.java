package it.polimi.ingsw.client.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.*;

public class GodCardsPopup extends Stage {

    private final static int gap = 10;
    private final static int cardsWidth = 100;
    private Map<String, ImageView> cards;
    private Set<String> chosenGodCards;

    private int maxSelectionsNum;
    private int selectionsNum;

    public GodCardsPopup (Stage ownerStage, int selectionsNum, Map<String, String> godCardsDescriptions, Gui gui) {

        initOwner(ownerStage);
        maxSelectionsNum = selectionsNum;
        setTitle("GodCards");
        setResizable(false);
        this.chosenGodCards = new HashSet<>();
        this.cards = new HashMap<>();
        initializeCards();
        this.selectionsNum = 0;
        VBox vbox = new VBox();
        Button submit = new Button("Submit");
        TilePane tilePane = new TilePane();

        tilePane.setHgap(gap);
        tilePane.setVgap(gap);

        vbox.setBackground(new Background(new BackgroundFill(Color.LIGHTSLATEGREY, null, null)));

        //if the popup appears to choose the matchGods, it has to tell the player that he's the challenger
        if (selectionsNum > Gui.godsForPlayer) {
            Label label = new Label ("You're the Challenger of this match! Choose " + selectionsNum + " godCards for the match:");
            label.setTextFill(Color.WHITE);
            vbox.getChildren().add(label);
            vbox.setAlignment(Pos.CENTER);
        }
        else if (selectionsNum == Gui.godsForPlayer) {
            Label label = new Label ("Choose your GodCard from the available ones: ");
            label.setTextFill(Color.WHITE);
            vbox.getChildren().add(label);
            vbox.setAlignment(Pos.CENTER);
        }

        vbox.getChildren().add(tilePane);

        //if selectionsNum is 0, it means the match is in GAME state and i just want to see MatchCards and descriptions
        if (selectionsNum != 0)
            vbox.getChildren().add(submit);

        submit.setBackground(new Background(new BackgroundImage(new Image("file:src/main/resources/btn_submit.png"), BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(0, 0, false, false, false, true))));

        submit.setPrefWidth((float) Gui.sceneWidth/14);
        submit.setTextFill(Color.WHITESMOKE);

        submit.setOnMouseEntered(mouseEvent -> {
            Button enteredButton = (Button) mouseEvent.getSource();
            DropShadow shadow = new DropShadow();
            enteredButton.setEffect(shadow);
        });

        submit.setOnMouseExited(mouseEvent -> {
            Button enteredButton = (Button) mouseEvent.getSource();
            enteredButton.setEffect(null);
        });

        submit.setOnMousePressed(mouseEvent -> {
            Button pressedButton = (Button) mouseEvent.getSource();
            pressedButton.setBackground(new Background(new BackgroundImage(new Image(Gui.submitButtonPressed), BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(0, 0, false, false, false, true))));

        });

        vbox.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, Color.TRANSPARENT, Color. TRANSPARENT,Color.TRANSPARENT,
                null, null, null, null, CornerRadii.EMPTY,
                BorderStroke.MEDIUM, new Insets(gap, gap, gap, gap))));
        vbox.setSpacing(gap);

        vbox.setAlignment(Pos.BOTTOM_RIGHT);


        for (String s: godCardsDescriptions.keySet()) {
            ImageView tmp = cards.get(s);
            tilePane.getChildren().add(tmp);

            tmp.setOnMouseEntered(mouseEvent -> Tooltip.install(tmp, new Tooltip(godCardsDescriptions.get(s))));

            submit.setOnMouseClicked(mouseEvent -> {
                if (selectionsNum == maxSelectionsNum) {
                        /*
                        Map<String,String> matchGodCards = new HashMap<>();
                        for(String godName : godCardsDescriptions.keySet().stream().filter(godName -> chosenGodCards.contains(godName)).collect(Collectors.toSet()))
                            matchGodCards.put(godName, godCardsDescriptions.get(godName));*/
                    gui.setGodCardsChoice(chosenGodCards);
                    close();
                }
            });
        }

        Scene scene = new Scene(vbox);
        this.setScene(scene);
        this.show();
    }

    private void initializeCards() {

        ImageView card01 = new ImageView ("file:src/main/resources/01.png");
        ImageView card02 = new ImageView ("file:src/main/resources/02.png");
        ImageView card03 = new ImageView ("file:src/main/resources/03.png");
        ImageView card04 = new ImageView ("file:src/main/resources/04.png");
        ImageView card05 = new ImageView ("file:src/main/resources/05.png");
        ImageView card06 = new ImageView ("file:src/main/resources/06.png");
        ImageView card08 = new ImageView ("file:src/main/resources/08.png");
        ImageView card09 = new ImageView ("file:src/main/resources/09.png");
        ImageView card10 = new ImageView ("file:src/main/resources/10.png");
        ImageView card16 = new ImageView ("file:src/main/resources/16.png");
        ImageView card21 = new ImageView ("file:src/main/resources/21.png");
        ImageView card23 = new ImageView ("file:src/main/resources/23.png");
        ImageView card29 = new ImageView ("file:src/main/resources/29.png");
        ImageView card30 = new ImageView ("file:src/main/resources/30.png");

        cards.put("Apollo", card01);
        cards.put("Artemis", card02);
        cards.put("Athena", card03);
        cards.put("Atlas", card04);
        cards.put("Demeter", card05);
        cards.put("Hephaestus", card06);
        cards.put("Minotaur", card08);
        cards.put("Pan", card09);
        cards.put("Prometheus", card10);
        cards.put("Chronus", card16);
        cards.put("Hestia", card21);
        cards.put("Limus", card23);
        cards.put("Triton", card29);
        cards.put("Zeus", card30);

        for (ImageView card: cards.values()) {
            card.setFitWidth(cardsWidth);
            card.setPreserveRatio(true);
        }

        for (String godCardName: cards.keySet()) {

            ImageView image = cards.get(godCardName);
            image.setOnMouseClicked(mouseEvent -> {
                if (selectionsNum < maxSelectionsNum) {
                    selectionsNum++;
                    image.setOpacity(Gui.selectionOpacity);
                    chosenGodCards.add(godCardName);
                }
            });
        }
    }

}
