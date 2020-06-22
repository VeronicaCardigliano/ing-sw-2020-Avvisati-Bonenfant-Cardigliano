package it.polimi.ingsw.client.gui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Set;

/**
 * This class is a window, composed by an AnchorPane, which allows the user to do a choice through a choiceBox in the setup phase
 */

public class ChoicePopup extends Stage {

    private Button submit;

    public ChoicePopup(Stage ownerStage, Set<String> choices, String requestLabel, String title, ChoiceBox<String> choiceBox) {

        this.initOwner(ownerStage);
        this.setTitle(title);
        this.setResizable(false);

        AnchorPane anchorPane = new AnchorPane();

        submit = new Button("Submit");

        submit.setBackground(new Background(new BackgroundImage(new Image(Gui.submitButton), BackgroundRepeat.NO_REPEAT,
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

        submit.setOnMouseReleased(mouseEvent -> {
            Button pressedButton = (Button) mouseEvent.getSource();
            pressedButton.setBackground(new Background(new BackgroundImage(new Image("file:src/main/resources/btn_submit.png"), BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(0, 0, false, false, false, true))));

        });

        for (String s: choices)
            choiceBox.getItems().add(s);

        //sets the default value of the choiceBox to the first element of the set
        choiceBox.setValue(choices.iterator().next());

        Label requests = new Label (requestLabel);

        AnchorPane.setLeftAnchor(requests,Gui.marginLength);
        AnchorPane.setTopAnchor(requests,Gui.marginLength);
        AnchorPane.setLeftAnchor(choiceBox,Gui.marginLength);
        AnchorPane.setTopAnchor(choiceBox,Gui.marginLength * 2.5);
        anchorPane.setPrefSize((double)Gui.sceneWidth/3, (double)Gui.sceneHeight/3.5);

        AnchorPane.setRightAnchor(submit, Gui.marginLength);
        AnchorPane.setBottomAnchor(submit,Gui.marginLength);
        anchorPane.getChildren().addAll(requests,choiceBox,submit);

        this.setOnCloseRequest(windowEvent -> {
            if (Gui.confirmQuit()) {
                this.close();
                //ownerStage.fireEvent(new WindowEvent(ownerStage, WindowEvent.WINDOW_CLOSE_REQUEST));
                ownerStage.close();
                Platform.exit();
                System.exit(0);
            }
            else
                windowEvent.consume();
        });

        Scene scene = new Scene(anchorPane);
        this.setScene(scene);
        this.show();
    }

    /**
     * @return the submit button used to send the choice to the server
     */
    protected Button getSubmit() {
        return submit;
    }
}
