package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.NetworkHandler;
import it.polimi.ingsw.client.View;

/**
 * Cli Client entry point class
 */
public abstract class CliClient {

    /**
     * initializes a network handler and a Cli
     */
    public static void launch() {
        NetworkHandler nh = new NetworkHandler();
        View view = new Cli();

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
        nh.setOpponentDisconnectionObserver(view);
        nh.setSocketObserver(view);
        nh.setStartPlayerSetObserver(view);

        view.run();

    }
}
