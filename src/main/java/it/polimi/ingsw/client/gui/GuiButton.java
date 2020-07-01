package it.polimi.ingsw.client.gui;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

/**
 * Generic button which extends Button class and can be created with more specific parameters
 */
public class GuiButton extends Button {

    /**
     * This method creates a button given the specific parameter
     * @param btnName name shown on the button
     * @param backgroundSrc background image of the button
     * @param handler what to do when the button is pressed
     * @param pressedBtnSrc background image of the pressed button
     */
    public GuiButton(String btnName, String backgroundSrc, EventHandler<MouseEvent> handler, String pressedBtnSrc) {

        this.setText(btnName);
        this.setBackground(new Background(new BackgroundImage(new Image(getClass().getResourceAsStream(backgroundSrc)), BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(0, 0, false, false, false, true))));

        this.setOnMouseEntered(mouseEvent -> {
            Button enteredButton = (Button) mouseEvent.getSource();
            DropShadow shadow = new DropShadow();
            enteredButton.setEffect(shadow);
        });

        this.setOnMouseExited(mouseEvent -> {
            Button enteredButton = (Button) mouseEvent.getSource();
            enteredButton.setEffect(null);
        });

        this.setOnMousePressed(mouseEvent -> {
            Button pressedButton = (Button) mouseEvent.getSource();
            pressedButton.setBackground(new Background(new BackgroundImage(new Image(getClass().getResourceAsStream(pressedBtnSrc)), BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(0, 0, false, false, false, true))));

        });

        this.setOnMouseReleased(mouseEvent -> {
            Button pressedButton = (Button) mouseEvent.getSource();
            pressedButton.setBackground(new Background(new BackgroundImage(new Image(getClass().getResourceAsStream(backgroundSrc)), BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(0, 0, false, false, false, true))));

        });

        this.setOnMouseClicked(handler);
    }

}
