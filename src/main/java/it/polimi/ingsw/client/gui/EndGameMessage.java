package it.polimi.ingsw.client.gui;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class EndGameMessage {

    Button playAgainBtn;

    /**
     * End game message of victory or loss with a button to eventually play again.
     * @param message string to print
     */
    public EndGameMessage(String message, Color color, Pane ownerPane, EventHandler<MouseEvent> handler) {

        Text text = new Text(message);
        //text.prefWidthProperty().bind(playersRegion.prefWidthProperty().subtract(marginLength*2));
        text.setFont(new Font("Courier" ,Gui.fontSize*3));

        text.setFill(color);

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

        Platform.runLater(()-> ownerPane.getChildren().add(text));

        playAgainBtn = new GuiButton("Play Again", Gui.submitButton, ownerPane, handler, Gui.submitButtonPressed);
        playAgainBtn.setTextFill(Color.WHITESMOKE);
    }

    /**
     * @return the submit button used to set the handler of an event from outside of the class
     */
    protected Button getPlayAgainBtn() {
        return playAgainBtn;
    }

}
