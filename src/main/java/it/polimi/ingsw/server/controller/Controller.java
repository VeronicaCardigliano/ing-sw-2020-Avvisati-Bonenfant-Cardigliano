package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.gameMap.Coordinates;
import it.polimi.ingsw.server.parser.Messages;
import it.polimi.ingsw.server.view.ViewManager;
import it.polimi.ingsw.server.view.VirtualView;

import java.io.IOException;
import java.util.Set;


public class Controller extends AbstractController implements ConnectionObserver{

    private final Model model;
    private final ViewManager viewManager;
    private String startPlayerNickname;
    private String challenger;

    public Controller (Model model, ViewManager viewManager) {

        this.model = model;
        this.viewManager = viewManager;

    }

    /**
     * This update of Controller is called by a specific View's notify when the user insert a number
     * @param num is the number entered bu the user
     */
    @Override
    public synchronized void onNumberInsertion(int num) {

        if (model.getCurrState() == Model.State.SETUP_NUMOFPLAYERS && (model.getNumberOfPlayers() != 2 ||
        model.getNumberOfPlayers() != 3)) {

            if (model.setNumberOfPlayers(num)) {
                model.setNextState();
                //viewManager.askNickAndDate();
            } //else
                //viewManager.askNumberOfPlayers(); //broadcast message
        }
    }

    @Override
    public synchronized void onNicknameAndDateInsertion(String nickname, String birthday) {
        if (model.getCurrState() == Model.State.SETUP_PLAYERS) {

            if (model.getPlayers().size() < model.getNumberOfPlayers()){
                model.addPlayer(nickname, birthday);
            }

            if (model.getPlayers().size() == model.getNumberOfPlayers()) {
                model.setNextState();
                //setNextPlayer used to initialize first player :)
                model.setNextPlayer();
                challenger = model.getCurrPlayer().getNickname();
                viewManager.chooseMatchGodCards(challenger, model.getNumberOfPlayers(), model.getGodDescriptions());

            }
        }
    }


    @Override
    public synchronized void onColorChoice(String nickname, String color){

        if (model.getCurrState() == Model.State.SETUP_COLOR && model.getCurrPlayer().getNickname().equals(nickname)) {
            if (model.assignColor(color)) {
                model.setNextPlayer();
                if (model.getCurrPlayer().getNickname().equals(startPlayerNickname)) {
                    model.setNextState();
                    viewManager.askBuilders(model.getCurrPlayer().getNickname());
                } else
                    viewManager.askColor(model.getCurrPlayer().getNickname(), model.getChosenColors());
            }
        }
    }


    @Override
    public synchronized void onGodCardChoice(String nickname, String godCardName) {
        if (model.getCurrState() == Model.State.SETUP_CARDS && model.getCurrPlayer().getNickname().equals(nickname) &&
            model.getCurrPlayer().getGodCard() == null) {

            if (model.assignCard(godCardName)) {
                model.setNextPlayer();

                if (model.getCurrPlayer().getGodCard() != null) {
                    viewManager.chooseStartPlayer(challenger, model.getPlayersNickname());
                }
                else
                    viewManager.askGod(model.getCurrPlayer().getNickname(), model.getMatchGodCardsDescriptions(), model.getChosenCards());
            } else viewManager.askGod(nickname, model.getMatchGodCardsDescriptions(), model.getChosenCards());
        }
    }

    @Override
    public synchronized void onMatchGodCardsChoice(String nickname, Set<String> godNames) {
        if(model.getCurrState() == Model.State.SETUP_CARDS && model.getCurrPlayer().getNickname().equals(nickname)) {

            if(model.setMatchCards(godNames)) {
                model.setNextPlayer();
                viewManager.askGod(model.getCurrPlayer().getNickname(), model.getMatchGodCardsDescriptions(), model.getChosenCards());

            }
            else
                viewManager.chooseMatchGodCards(nickname, model.getNumberOfPlayers(), model.getGodDescriptions());
        }
    }

    @Override
    public synchronized void onBuilderSetup(String nickname, Coordinates builder1, Coordinates builder2){
        if (model.getCurrState() == Model.State.SETUP_BUILDERS && model.getCurrPlayer().getNickname().equals(nickname))

            if (model.setCurrPlayerBuilders(builder1, builder2)){
                model.setNextPlayer();

                if (model.getCurrPlayer().getNickname().equals(startPlayerNickname)) {
                    model.setNextState();

                    //game starts
                    //Initialize the turn
                    model.getCurrPlayer().getGodCard().startTurn();

                    if(model.getCurrPlayer().getGodCard().getCurrState().equals("BOTH"))
                        viewManager.askStep(model.getCurrPlayer().getNickname());
                    else {
                        model.findPossibleDestinations();
                    }
                } else
                    viewManager.askBuilders(model.getCurrPlayer().getNickname());

            } else viewManager.askBuilders(nickname);

    }

//-------------

    @Override
    public synchronized void onBuilderBuild(String nickname, Coordinates src, Coordinates dst, boolean buildDome) {

        if (model.getCurrState() == Model.State.GAME && model.getCurrPlayer().getNickname().equals(nickname) &&
                model.getCurrPlayer().getGodCard().getCurrState().equals("BUILD") &&
                model.effectiveBuild(src, dst, buildDome) && model.hasNotLostDuringBuild()) {

                manageNextState(nickname);
            }
    }

    @Override
    public synchronized void onBuilderMove(String nickname, Coordinates src, Coordinates dst) {

        if (model.getCurrState() == Model.State.GAME && model.getCurrPlayer().getNickname().equals(nickname) &&
                model.getCurrPlayer().getGodCard().getCurrState().equals("MOVE") && model.effectiveMove(src, dst) &&
                model.hasNotLostDuringMove() && !model.endGame()) {

                manageNextState(nickname);
            }
    }

    private void manageNextState(String nickname) {
        if (model.getCurrPlayer().getGodCard().getCurrState().equals("END")) {
            model.setNextPlayer();
            model.getCurrPlayer().startTurn();

            if (model.getCurrPlayer().getGodCard().getCurrState().equals("BOTH"))
                viewManager.askStep(model.getCurrPlayer().getNickname());
            else
                model.findPossibleDestinations();

        } else if (model.getCurrPlayer().getGodCard().getCurrState().equals("BOTH")) //check step
            viewManager.askStep(nickname);
        else
            model.findPossibleDestinations();
    }


//------------

    @Override
    public synchronized void onStepChoice(String player, String chosenStep) {
        if (model.getCurrPlayer().getNickname().equals(player) && model.getCurrPlayer().getGodCard().getCurrState().equals("BOTH")) {
            if(model.setStepChoice(chosenStep))
                model.findPossibleDestinations();
        }
    }

    @Override
    public synchronized void onDisconnection(String nickname) {
        viewManager.remove(nickname);
        model.deletePlayer(nickname);
    }

    @Override
    public synchronized void onConnection(VirtualView view) throws IOException {
        //boolean acceptConnection = true;

        if(viewManager.getNumberOfViews() == 0 && model.getCurrState().equals(Model.State.SETUP_NUMOFPLAYERS)) {
            viewManager.add(view);
            viewManager.askNumberOfPlayers();

        } else if(model.getCurrState().equals(Model.State.SETUP_PLAYERS) && viewManager.getNumberOfViews() < model.getNumberOfPlayers()) {
            viewManager.add(view);

            //if now all needed clients are connected ask them to insert name and birth date
            if(viewManager.getNumberOfViews() == model.getNumberOfPlayers())
                viewManager.askNickAndDate();

        } else {
            //acceptConnection = true;
            view.send(Messages.errorMessage("Too many clients connected"));
            view.send(Messages.disconnect());
            view.disconnect();
        }


    }

    @Override
    public void onSetStartPlayer(String nickname, String startPlayer) {
        if(model.getCurrState().equals(Model.State.SETUP_CARDS) && challenger.equals(nickname)) {
            if(model.setStartPlayer(nickname, startPlayer)) {
                startPlayerNickname = startPlayer;
                model.setNextState();
                viewManager.askColor(model.getCurrPlayer().getNickname(), model.getChosenColors());
            }
        } else
            viewManager.chooseStartPlayer(nickname, model.getPlayersNickname());
    }
}
