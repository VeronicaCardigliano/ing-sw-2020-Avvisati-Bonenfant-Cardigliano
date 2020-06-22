package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.gameMap.Coordinates;
import it.polimi.ingsw.network.Messages;
import it.polimi.ingsw.server.view.ViewManager;
import it.polimi.ingsw.server.view.VirtualView;

import java.io.IOException;
import java.util.Set;


public class Controller extends AbstractController implements ConnectionObserver{

    private Model model;
    private final ViewManager viewManager;

    public Controller (Model model, ViewManager viewManager) {

        this.model = model;
        this.viewManager = viewManager;

        model.setBuilderBuiltObserver(viewManager);
        model.setBuilderMovementObserver(viewManager);
        model.setBuildersPlacedObserver(viewManager);
        model.setChosenStepObserver(viewManager);
        model.setColorAssignmentObserver(viewManager);
        model.setEndGameObserver(viewManager);
        model.setErrorsObserver(viewManager);
        model.setGodChoiceObserver(viewManager);
        model.setPlayerAddedObserver(viewManager);
        model.setPlayerLoseObserver(viewManager);
        model.setPlayerTurnObserver(viewManager);
        model.setViewSelectObserver(viewManager);
        model.setStateObserver(viewManager);
        model.setPossibleBuildObserver(viewManager);
        model.setPossibleMoveObserver(viewManager);

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
            }

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
                model.setChallenger(model.getCurrPlayer());
                viewManager.chooseMatchGodCards(model.getChallenger(), model.getNumberOfPlayers(),
                        model.getGodDescriptions());

            }
        }
    }


    @Override
    public synchronized void onColorChoice(String nickname, String color){

        if (model.getCurrState() == Model.State.SETUP_COLOR && model.getCurrPlayer().equals(nickname)) {
            if (model.assignColor(color)) {
                model.setNextPlayer();
                if (model.getCurrPlayer().equals(model.getStartPlayerNickname())) {
                    model.setNextState();
                    viewManager.askBuilders(model.getCurrPlayer());
                } else
                    viewManager.askColor(model.getCurrPlayer(), model.getChosenColors());
            }
        }
    }


    @Override
    public synchronized void onGodCardChoice(String nickname, String godCardName) {
        if (model.getCurrState() == Model.State.SETUP_CARDS && model.getCurrPlayer().equals(nickname) &&
                model.currPlayerNullGodCard()) {

            if (model.assignCard(godCardName)) {
                model.setNextPlayer();

                if (!model.currPlayerNullGodCard()) {
                    viewManager.chooseStartPlayer(model.getChallenger(), model.getPlayersNickname());
                }
                else
                    viewManager.askGod(model.getCurrPlayer(), model.getMatchGodCardsDescriptions(), model.getChosenCards());
            } else viewManager.askGod(nickname, model.getMatchGodCardsDescriptions(), model.getChosenCards());
        }
    }

    @Override
    public synchronized void onMatchGodCardsChoice(String nickname, Set<String> godNames) {
        if(model.getCurrState() == Model.State.SETUP_CARDS && model.getCurrPlayer().equals(nickname)) {

            if(model.setMatchCards(godNames)) {
                model.setNextPlayer();
                viewManager.askGod(model.getCurrPlayer(), model.getMatchGodCardsDescriptions(), model.getChosenCards());

            }
            else
                viewManager.chooseMatchGodCards(nickname, model.getNumberOfPlayers(), model.getGodDescriptions());
        }
    }

    @Override
    public synchronized void onBuilderSetup(String nickname, Coordinates builder1, Coordinates builder2){
        if (model.getCurrState() == Model.State.SETUP_BUILDERS && model.getCurrPlayer().equals(nickname))

            if (model.setCurrPlayerBuilders(builder1, builder2)){
                model.setNextPlayer();

                if (model.getCurrPlayer().equals(model.getStartPlayerNickname())) {
                    model.setNextState();

                    //game starts
                    //Initialize the turn
                    model.startTurn();

                    if(model.getCurrStep().equals("REQUIRED"))
                        viewManager.askStep(model.getCurrPlayer(), model.getCurrStateList());
                    else {
                        model.findPossibleDestinations();
                        checkHasLost();
                    }
                }

                else
                    viewManager.askBuilders(model.getCurrPlayer());

            } else viewManager.askBuilders(nickname);

    }

//-------------

    @Override
    public synchronized void onBuilderBuild(String nickname, Coordinates src, Coordinates dst, boolean buildDome) {

        if (model.getCurrState() == Model.State.GAME && model.getCurrPlayer().equals(nickname) &&
                model.getCurrStep().equals("BUILD") &&
                model.effectiveBuild(src, dst, buildDome) && model.hasNotLostDuringBuild() && !model.endGame()) {

            manageNextState(nickname);
        }

    }

    @Override
    public synchronized void onBuilderMove(String nickname, Coordinates src, Coordinates dst) {

        if (model.getCurrState() == Model.State.GAME && model.getCurrPlayer().equals(nickname) &&
                model.getCurrStep().equals("MOVE") && model.effectiveMove(src, dst) &&
                model.hasNotLostDuringMove() && !model.endGame()) {

            manageNextState(nickname);
        }
    }

//------------

    @Override
    public synchronized void onStepChoice(String player, String chosenStep) {
        if (model.getCurrPlayer().equals(player) && model.getCurrStep().equals("REQUIRED")) {
            if(!chosenStep.equals("END")) {
                if (model.setStepChoice(chosenStep)){
                    model.findPossibleDestinations();
                    checkHasLost();
                }

            }
            else {
                model.setNextPlayer();
                model.startTurn();

                if (model.getCurrStep().equals("REQUIRED"))
                    viewManager.askStep(model.getCurrPlayer(), model.getCurrStateList());
                else{
                    model.findPossibleDestinations();
                    checkHasLost();
                }
            }
        }
    }

    private void manageNextState(String nickname) {
        //here the player must end the turn
        if (model.getCurrStep().equals("END")) {
            model.setNextPlayer();
            model.startTurn();

            if (model.getCurrStep().equals("REQUIRED"))
                viewManager.askStep(model.getCurrPlayer(), model.getCurrStateList());
            else{
                model.findPossibleDestinations();
                checkHasLost();
            }
        }
        else if (model.getCurrStep().equals("REQUIRED"))
            viewManager.askStep(nickname, model.getCurrStateList());
        else{
            model.findPossibleDestinations();
            checkHasLost();
        }
    }

    /**
     * requires That findPossibleDestination has been called right before
     */
    private void checkHasLost(){
        if (!model.hasNotLostDuringMove() && !model.hasNotLostDuringBuild()){
            String playerToRemove = model.getCurrPlayer();
            model.setNextPlayer();
            model.notifyLoss(playerToRemove);
            model.deletePlayer(playerToRemove);
            //TODO should i remove the player from the viewManager?
            if (model.endGame())
                this.model = new Model();
            else {
                model.startTurn();
                if (model.getCurrStep().equals("REQUIRED"))
                    viewManager.askStep(model.getCurrPlayer(),model.getCurrStateList());
                else{
                    model.findPossibleDestinations();
                    //recursive function to check if next player has lost too or not
                    checkHasLost();
                }
            }
        }
    }

    @Override
    public synchronized void onDisconnection(String nickname) {
        viewManager.remove(nickname);
        model.deletePlayer(nickname);
        if (model.endGame()){
            for (Player p : model.getPlayers())
                viewManager.remove(p.getNickname());
            this.model = new Model();
        }
    }

    @Override
    public synchronized void onConnection(VirtualView view) throws IOException {

        if(viewManager.getNumberOfViews() == 0 && model.getCurrState().equals(Model.State.SETUP_NUMOFPLAYERS)) {
            viewManager.add(view);
            viewManager.askNumberOfPlayers();

        } else if(model.getCurrState().equals(Model.State.SETUP_PLAYERS) && viewManager.getNumberOfViews() < model.getNumberOfPlayers()) {
            viewManager.add(view);

            //if now all needed clients are connected ask them to insert name and birth date
            if(viewManager.getNumberOfViews() == model.getNumberOfPlayers())
                viewManager.askNickAndDate();

        } else {
            view.send(Messages.errorMessage("Too many clients connected"));
            view.send(Messages.disconnect());
            view.disconnect();
        }
    }

    @Override
    public void onSetStartPlayer(String nickname, String startPlayer) {
        if(model.getCurrState().equals(Model.State.SETUP_CARDS) && model.getChallenger().equals(nickname)) {
            if(model.setStartPlayer(nickname, startPlayer)) {
                model.setStartPlayerNickname(startPlayer);
                model.setNextState();
                viewManager.askColor(model.getCurrPlayer(), model.getChosenColors());
            }
        } else
            viewManager.chooseStartPlayer(nickname, model.getPlayersNickname());
    }
}