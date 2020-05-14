package it.polimi.ingsw.client;

import it.polimi.ingsw.client.cli.Cli;

import java.io.IOException;


public class Client {
    public static void main(String[] args) {
        NetworkHandler nh = new NetworkHandler("localhost", 2033);
        Cli view = new Cli(System.in);

        nh.setView(view);

        view.setObservers(nh);

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

        (new Thread(nh)).start();

        //view.askNumberOfPlayers();
    }
}
