package it.polimi.ingsw.client.gui;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Set;

/**
 * This class is a window, composed by an AnchorPane, which allows the user to do a choice through a choiceBox in the setup phase
 */

public class ChoicePopup extends Stage {

    private AnchorPane anchorPane;
    private Button submit;

    public ChoicePopup(Stage ownerStage, Set<String> choices, String requestLabel, String title,
                       ChoiceBox<String> choiceBox, EventHandler<WindowEvent> handler) {

        this.initOwner(ownerStage);
        this.setTitle(title);
        this.setResizable(false);

        anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-background-color: lightslategrey");

        for (String s: choices)
            choiceBox.getItems().add(s);

        //sets the default value of the choiceBox to the first element of the set
        choiceBox.setValue(choices.iterator().next());
        choiceBox.setStyle("-fx-background-color: steelblue; -fx-border-color: midnightblue; -fx-mark-color: midnightblue;" +
                " -fx-border-radius: 20; -fx-background-radius: 20;");

        Text requests = new Text (requestLabel);
        requests.setFill(Color.WHITE);

        AnchorPane.setLeftAnchor(requests,Gui.marginLength);
        AnchorPane.setTopAnchor(requests,Gui.marginLength);

        AnchorPane.setLeftAnchor(choiceBox,Gui.marginLength);
        AnchorPane.setTopAnchor(choiceBox,Gui.marginLength * 2.5);

        anchorPane.setPrefSize((double)Gui.sceneWidth/3, (double)Gui.sceneHeight/3.5);
        anchorPane.getChildren().addAll(requests,choiceBox);

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
    protected boolean isChildPresent (Node node) {
        return anchorPane.getChildren().contains(node);
    }

}
