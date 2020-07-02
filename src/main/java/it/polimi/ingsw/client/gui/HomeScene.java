package it.polimi.ingsw.client.gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import static it.polimi.ingsw.client.gui.Gui.sceneHeight;
import static it.polimi.ingsw.client.gui.Gui.sceneWidth;

/**
 * The home scene is composed by an AnchorPane with two nameTags composed by a StackPane
 * with an image and a TextField to insert IP and Port values, and a play button which sends a request of connection.
 * If the request is successful, the home stage gives way to the main stage
 */

public class HomeScene extends Scene {

    private static final String playButtonSrc = "/Images/PlayButton.png";
    private static final String IPInsertionSrc = "/Images/IP_insertion.png";
    private static final String PortInsertionSrc = "/Images/Port_insertion.png";
    private static final String homeBackgroundSrc = "/Images/homeScreenBackground.png";

    public static final double networkReqRatio = 232.0/ sceneWidth;
    public static final double networkInsertionRatio = 100.0/ sceneWidth;
    public static final double playBtnRatio = 283.0/ sceneWidth;

    private ImageView playBtn = new ImageView(new Image (getClass().getResourceAsStream(playButtonSrc)));

    /**
     * Creates a new HomeScene setting home pane children's properties and handlers on mouse events.
     * @param home main pane
     * @param width width of the scene
     * @param height height of the scene
     * @param IPInsertion TextField to insert the IP
     * @param portInsertion TextField to insert the port
     */
    public HomeScene(Pane home, double width, double height, TextField IPInsertion, TextField portInsertion) {

        super(home, width, height);

        home.prefWidthProperty().bind(home.widthProperty());
        home.prefHeightProperty().bind(home.heightProperty());
        home.setBackground(new Background(
                new BackgroundImage(new Image(getClass().getResourceAsStream(homeBackgroundSrc)), BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(0, 0, false, false, false, true))));


        DropShadow shadow = new DropShadow();
        playBtn.setOnMouseEntered(mouseEvent -> playBtn.setEffect(shadow));
        playBtn.setOnMouseExited(mouseEvent -> playBtn.setEffect(null));
        playBtn.setPreserveRatio(true);

        ImageView IPNameTag = new ImageView(new Image(getClass().getResourceAsStream(IPInsertionSrc)));
        ImageView portNameTag = new ImageView(new Image(getClass().getResourceAsStream(PortInsertionSrc)));

        IPNameTag.setPreserveRatio(true);
        portNameTag.setPreserveRatio(true);
        playBtn.fitWidthProperty().bind(this.widthProperty().multiply(playBtnRatio));
        IPNameTag.fitWidthProperty().bind(this.widthProperty().multiply(networkReqRatio));
        portNameTag.fitWidthProperty().bind(this.widthProperty().multiply(networkReqRatio));

        IPInsertion.prefWidthProperty().bind(this.widthProperty().multiply(networkInsertionRatio));
        portInsertion.prefWidthProperty().bind(this.widthProperty().multiply(networkInsertionRatio));

        IPInsertion.maxWidthProperty().bind(IPNameTag.fitWidthProperty().divide(2.5));
        IPInsertion.setAlignment(Pos.CENTER);
        portInsertion.setAlignment(Pos.CENTER);
        portInsertion.maxWidthProperty().bind(portNameTag.fitWidthProperty().divide(2.5));

        IPInsertion.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        portInsertion.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));

        StackPane IPRequest = new StackPane();
        IPRequest.getChildren().addAll(IPNameTag, IPInsertion);
        StackPane portRequest = new StackPane();
        portRequest.getChildren().addAll(portNameTag, portInsertion);
        Glow glow = new Glow();
        glow.setLevel(0.2);

        IPRequest.setOnMouseEntered(mouseEvent -> IPRequest.setEffect(shadow));
        portRequest.setOnMouseEntered(mouseEvent -> portRequest.setEffect(shadow));
        portRequest.setOnMouseExited(mouseEvent -> portRequest.setEffect(null));
        IPRequest.setOnMouseExited(mouseEvent -> IPRequest.setEffect(null));

        portRequest.setOnMouseClicked(mouseEvent -> {
            portRequest.setEffect(glow);
            IPRequest.setEffect(null);
            portRequest.setOnMouseEntered(null);
            portRequest.setOnMouseExited(null);
            IPRequest.setOnMouseEntered(mouseEvent1 -> IPRequest.setEffect(shadow));
            IPRequest.setOnMouseExited(mouseEvent1 -> IPRequest.setEffect(null));
        });

        IPRequest.setOnMouseClicked(mouseEvent -> {
            IPRequest.setEffect(glow);
            portRequest.setEffect(null);
            portRequest.setOnMouseEntered(mouseEvent1 -> portRequest.setEffect(shadow));
            portRequest.setOnMouseExited(mouseEvent1 -> portRequest.setEffect(null));
            IPRequest.setOnMouseEntered(null);
            IPRequest.setOnMouseExited(null);
        });

        IPInsertion.setOnMouseClicked(mouseEvent -> {
            IPRequest.setEffect(glow);
            portRequest.setEffect(null);
            portRequest.setOnMouseEntered(mouseEvent1 -> portRequest.setEffect(shadow));
            portRequest.setOnMouseExited(mouseEvent1 -> portRequest.setEffect(null));
            IPRequest.setOnMouseEntered(null);
            IPRequest.setOnMouseExited(null);
        });

        portInsertion.setOnMouseClicked(mouseEvent -> {
            portRequest.setEffect(glow);
            IPRequest.setEffect(null);
            portRequest.setOnMouseEntered(null);
            portRequest.setOnMouseExited(null);
            IPRequest.setOnMouseEntered(mouseEvent1 -> IPRequest.setEffect(shadow));
            IPRequest.setOnMouseExited(mouseEvent1 -> IPRequest.setEffect(null));
        });

        VBox networkRequests = new VBox();
        networkRequests.setAlignment(Pos.CENTER_LEFT);
        networkRequests.setSpacing(Gui.marginLength);
        networkRequests.getChildren().addAll(IPRequest, portRequest);

        home.getChildren().addAll(networkRequests, playBtn);

        AnchorPane.setBottomAnchor(networkRequests, (double) sceneHeight/10);
        AnchorPane.setLeftAnchor(networkRequests, (double) sceneWidth/10);

        AnchorPane.setBottomAnchor(playBtn, Gui.marginLength*2.5);
        AnchorPane.setRightAnchor(playBtn, (double) sceneHeight/3);
    }


    /**
     * Used to be able to set the handlers of a mouseEvents in Gui
     * @return play button used to send connection infos and, if correct, to change scene
     */
    protected ImageView getPlayBtn () {
        return playBtn;
    }

}
