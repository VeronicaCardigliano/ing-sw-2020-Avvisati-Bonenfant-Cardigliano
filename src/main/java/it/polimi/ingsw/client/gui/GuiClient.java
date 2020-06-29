package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.NetworkHandler;
import javafx.application.Application;
import javafx.stage.Stage;


/**
 * JavaFX App
 */
public class GuiClient extends Application {

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("SANTORINI");

        Gui view = new Gui(primaryStage);

        NetworkHandler nh = new NetworkHandler();

        nh.setView(view);

        view.setBuilderBuildObserver(nh);
        view.setBuilderMoveObserver(nh);
        view.setColorChoiceObserver(nh);
        view.setNewPlayerObserver(nh);
        view.setNumberOfPlayersObserver((nh));
        view.setStepChoiceObserver(nh);
        view.setBuilderSetupObserver(nh);
        view.setGodCardChoiceObserver(nh);
        view.setStartPlayerObserver(nh);
        view.setConnectionObserver(nh);

        nh.setBuilderBuiltObserver(view);
        nh.setBuilderMovementObserver(view);
        nh.setBuildersPlacedObserver(view);
        nh.setChosenStepObserver(view);
        nh.setColorAssignmentObserver(view);
        nh.setEndGameObserver(view);
        nh.setErrorsObserver(view);
        nh.setGodChoiceObserver(view);
        nh.setPlayerAddedObserver(view);
        nh.setPlayerLoseObserver(view);
        nh.setPlayerTurnObserver(view);
        nh.setStateObserver(view);
        nh.setPossibleBuildObserver(view);
        nh.setPossibleMoveObserver(view);
        nh.setSocketObserver(view);
        nh.setOpponentDisconnectionObserver(view);
        nh.setStartPlayerSetObserver(view);

        view.run();
    }

    public static void main(String[] args) {

        launch(args);
    }

}