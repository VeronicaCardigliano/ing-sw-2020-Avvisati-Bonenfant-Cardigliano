package it.polimi.ingsw.client;
import it.polimi.ingsw.client.cli.Cli;


public class Client {
    public static void main(String[] args) {
        NetworkHandler nh = new NetworkHandler("localhost", 2033);
        View view = new Cli();

        nh.setView(view);

        //view.setObservers(nh);

        view.setBuilderBuildObserver(nh);
        view.setBuilderMoveObserver(nh);
        view.setColorChoiceObserver(nh);
        view.setNewPlayerObserver(nh);
        view.setNumberOfPlayersObserver((nh));
        view.setStepChoiceObserver(nh);
        view.setBuilderSetupObserver(nh);
        view.setDisconnectionObserver(nh);
        view.setGodCardChoiceObserver(nh);
        view.setStartPlayerObserver(nh);

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
        view.run();

    }
}