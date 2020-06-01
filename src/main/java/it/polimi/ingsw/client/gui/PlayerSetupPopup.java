package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.View;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PlayerSetupPopup extends Stage{

    AnchorPane anchorPane;
    private Button submit;

    //initOwner sets the Stage in which the popup'll appear
    public PlayerSetupPopup(Stage ownerStage, TextField nickInsertion, TextField birthdayInsertion) {

        this.initOwner(ownerStage);
        this.initStyle(StageStyle.DECORATED);
        this.setTitle("New player");
        this.setResizable(false);
        this.anchorPane = new AnchorPane();

        Label nicknameRequest = new Label ("Insert nickname: ");
        Label birthdayRequest = new Label ("Insert birthday in the format aaaa.mm.gg: ");
        this.submit = new Button("Submit");

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

        AnchorPane.setLeftAnchor(nicknameRequest,Gui.marginLength);
        AnchorPane.setTopAnchor(nicknameRequest,Gui.marginLength);

        AnchorPane.setLeftAnchor(nickInsertion,Gui.marginLength);
        AnchorPane.setTopAnchor(nickInsertion,Gui.marginLength*2);

        AnchorPane.setLeftAnchor(birthdayRequest,Gui.marginLength);
        AnchorPane.setTopAnchor(birthdayRequest,Gui.marginLength*4);

        AnchorPane.setLeftAnchor(birthdayInsertion,Gui.marginLength);
        AnchorPane.setTopAnchor(birthdayInsertion,Gui.marginLength*5);

        anchorPane.setPrefSize((float) Gui.sceneWidth/3, (float) Gui.sceneHeight/2.5);

        AnchorPane.setRightAnchor(submit, Gui.marginLength);
        AnchorPane.setBottomAnchor(submit,Gui.marginLength);
        anchorPane.getChildren().addAll(nicknameRequest, birthdayRequest, nickInsertion, birthdayInsertion, submit);

        Scene scene = new Scene(anchorPane);
        this.setScene(scene);
        this.show();
    }

    protected Button getSubmit() {
        return submit;
    }
}