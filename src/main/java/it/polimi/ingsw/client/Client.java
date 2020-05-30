package it.polimi.ingsw.client;
import it.polimi.ingsw.client.cli.Cli;




public class Client {

    static String defaultIp = "localhost";
    static int defaultPort = 2033;

    public static void main(String[] args) {

        String ip;
        int port;

        View view;

        if(args.length == 3) {

            if(args[0].equals("CLI"))
                view = new Cli();
            else
                view = new Cli(); //todo change to GUI

            ip = args[1];
            port = Integer.parseInt(args[2]);


        } else {
            System.out.println("usage: java -jar Client [CLI | GUI] [ip] [port]");
            return;
        }

        NetworkHandler nh = new NetworkHandler(ip, port);
        //View view = new Cli();

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