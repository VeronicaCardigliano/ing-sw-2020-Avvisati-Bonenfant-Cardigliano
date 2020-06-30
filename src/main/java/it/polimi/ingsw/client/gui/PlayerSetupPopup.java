package it.polimi.ingsw.client.gui;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * Creates a new window with two Text and TextField objects for the insertion of nickname and date and a submit button
 */
public class PlayerSetupPopup extends Stage {

    private AnchorPane anchorPane;
    private Button submit;

    /**
     * Scene made by an anchorPane which contains texts and textFields and a button with its eventHandlers
     * Closing this stage, an alert stage is opened to confirm the quit of the game, and if confirmed both popup and
     * primaryStage are closed
     * @param ownerStage the Stage on which the popup'll appear
     * @param nickInsertion textField to insert the player name
     * @param birthdayInsertion textField to insert the birthday of the player
     */
    public PlayerSetupPopup(Stage ownerStage, TextField nickInsertion, TextField birthdayInsertion, EventHandler<WindowEvent> handler) {

        this.initOwner(ownerStage);
        this.initStyle(StageStyle.DECORATED);
        this.setTitle("New player");
        this.setResizable(false);
        this.anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-background-color: lightslategrey");

        Text nicknameRequest = new Text ("Insert nickname: ");
        Text birthdayRequest = new Text ("Insert birthday in the format yyyy.mm.dd: ");

        nicknameRequest.setFill(Color.WHITE);
        nicknameRequest.setFont(new Font("Arial", Gui.fontSize));

        birthdayRequest.setFill(Color.WHITE);
        birthdayRequest.setFont(new Font("Arial", Gui.fontSize));

        AnchorPane.setLeftAnchor(nicknameRequest,Gui.marginLength);
        AnchorPane.setTopAnchor(nicknameRequest,Gui.marginLength);

        AnchorPane.setLeftAnchor(nickInsertion,Gui.marginLength);
        AnchorPane.setTopAnchor(nickInsertion,Gui.marginLength*2);

        AnchorPane.setLeftAnchor(birthdayRequest,Gui.marginLength);
        AnchorPane.setTopAnchor(birthdayRequest,Gui.marginLength*4.5);

        AnchorPane.setLeftAnchor(birthdayInsertion,Gui.marginLength);
        AnchorPane.setTopAnchor(birthdayInsertion,Gui.marginLength*5.5);

        anchorPane.setPrefSize((float) Gui.sceneWidth/2.5, (float) Gui.sceneHeight/2.5);

        anchorPane.getChildren().addAll(nicknameRequest, birthdayRequest, nickInsertion, birthdayInsertion);

        this.submit = new GuiButton("Submit", Gui.submitButton, null, Gui.submitButtonPressed);

        submit.setPrefWidth((float) Gui.sceneWidth/14);
        submit.setTextFill(Color.WHITESMOKE);
        AnchorPane.setRightAnchor(submit, Gui.marginLength);
        AnchorPane.setBottomAnchor(submit,Gui.marginLength);
        Platform.runLater(()-> anchorPane.getChildren().add(submit));

        this.setOnCloseRequest(handler);

        Scene scene = new Scene(anchorPane);
        this.setScene(scene);
        this.show();
    }

    /**
     * @return the submit button used to set the handler of an event from outside of the class
     */
    protected Button getSubmit() {
        return submit;
    }

    /**
     * @param node node to be added to the main pane of the stage
     */
    protected void addChildren(Node node) {
        Platform.runLater(() -> anchorPane.getChildren().add(node));
    }

    /**
     * @param node node of which I wonder if present in the main pane of the stage
     * @return true if the node is present, false otherwise
     */
    protected boolean isChildPresent(Node node) {
        return anchorPane.getChildren().contains(node);
    }

}