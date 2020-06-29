package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.gameMap.Coordinates;
import it.polimi.ingsw.network.Messages;
import it.polimi.ingsw.server.view.ViewManager;
import it.polimi.ingsw.server.view.VirtualView;

import java.util.Set;


public class Controller extends AbstractController implements ConnectionObserver{

    private Model model;
    private final ViewManager viewManager;

    public Controller (Model model, ViewManager viewManager) {
        this.model = model;
        this.viewManager = viewManager;
        setModelObservers();
    }

    public void setModelObservers() {
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
        model.setStartPlayerSetObserver(viewManager);
    }


    /**
     * This update of Controller is called by a specific View's notify when the user inserts a number
     * @param num Number of players that will play the game
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


    /**
     * This update of Controller is called by a specific View's notify when the user inserts nickname and birthday.
     * @param nickname Nickname the user wants to use
     * @param birthday Birthday of the user
     */
    @Override
    public synchronized void onNicknameAndDateInsertion(String nickname, String birthday) {
        if (model.getCurrState() == Model.State.SETUP_PLAYERS) {

            if (model.getPlayers().size() < model.getNumberOfPlayers()){
                model.addPlayer(nickname, birthday);
            }

            if (model.getPlayers().size() == model.getNumberOfPlayers()) {
                model.setNextState();
                //setNextPlayer used to initialize first player
                model.setNextPlayer();
                model.setChallenger(model.getCurrPlayer());
                viewManager.chooseMatchGodCards(model.getChallenger(), model.getNumberOfPlayers(),
                        model.getGodDescriptions());

            }
        }
    }


    /**
     * This update of Controller is called by a specific View's notify when the user inserts a color
     * @param nickname Nickname of the user making the call
     * @param color Color the user wants to use
     */
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


    /**
     * This update of Controller is called by a specific View's notify when the user inserts a god card name
     * @param nickname Nickname of the user making the call
     * @param godCardName God card the user wants to use
     */
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


    /**
     * This update of Controller is called by a specific View's notify when the user inserts a list of god cards
     * @param nickname Nickname of the user making the call
     * @param godNames Set of gods the challenger has chosen
     */
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
    /**
     * This update of Controller is called by a specific View's notify when the user inserts a number
     * @param nickname nickname of the user making the call
     * @param godNames set of gods the challenger has chosen
     */
    public synchronized void onBuilderSetup(String nickname, Coordinates builder1, Coordinates builder2){
        if (model.getCurrState() == Model.State.SETUP_BUILDERS && model.getCurrPlayer().equals(nickname))

            if (model.setCurrPlayerBuilders(builder1, builder2)){
                model.setNextPlayer();

                if (model.getCurrPlayer().equals(model.getStartPlayerNickname())) {
                    model.setNextState();
                    model.startTurn();

                    if(model.getCurrStep().equals("REQUIRED"))
                        viewManager.askStep(model.getCurrPlayer(), model.getCurrStateList());
                    else {
                        checkHasLost();
                    }
                }

                else
                    viewManager.askBuilders(model.getCurrPlayer());

            } else viewManager.askBuilders(nickname);

    }


    /**
     * This update of Controller is called by a specific View's notify when the user wants to build
     * @param nickname Nickname of the user making the call
     * @param src This cell should contain the playing builder
     * @param dst Cell where the player wants to build
     * @param buildDome True if the player wants to build a dome
     */
    @Override
    public synchronized void onBuilderBuild(String nickname, Coordinates src, Coordinates dst, boolean buildDome) {
        if (model.getCurrState() == Model.State.GAME && model.getCurrPlayer().equals(nickname) &&
                model.getCurrStep().equals("BUILD") &&
                model.effectiveBuild(src, dst, buildDome) && model.hasNotLostDuringBuild()) {

            if(!model.endGame())
                manageNextState(nickname);
            else {
                viewManager.removeAndDisconnectAll();
                model = new Model();
                setModelObservers();
            }
        }

    }


    /**
     * This update of Controller is called by a specific View's notify when the user wants to move
     * @param nickname Nickname of the user making the call
     * @param src This cell should contain the playing builder
     * @param dst Cell where the player wants to move
     */
    @Override
    public synchronized void onBuilderMove(String nickname, Coordinates src, Coordinates dst) {
        if (model.getCurrState() == Model.State.GAME && model.getCurrPlayer().equals(nickname) &&
                model.getCurrStep().equals("MOVE") && model.effectiveMove(src, dst) &&
                model.hasNotLostDuringMove()) {

            if(!model.endGame())
                manageNextState(nickname);
            else {
                viewManager.removeAndDisconnectAll();
                model = new Model();
                setModelObservers();
            }
        }
    }


    /**
     * This update of Controller is called by a specific View's notify when the user wants to move
     * @param nickname Nickname of the user making the call
     * @param chosenStep Step like MOVE or BUILD the user has chosen
     */
    @Override
    public synchronized void onStepChoice(String nickname, String chosenStep) {
        if (model.getCurrPlayer().equals(nickname) && model.getCurrStep().equals("REQUIRED")) {
            if(!chosenStep.equals("END")) {
                if (model.setStepChoice(chosenStep)){
                    checkHasLost();
                }

            }
            else {
                model.setNextPlayer();
                model.startTurn();

                if (model.getCurrStep().equals("REQUIRED"))
                    viewManager.askStep(model.getCurrPlayer(), model.getCurrStateList());
                else{
                    checkHasLost();
                }
            }
        }
    }

    /**
     * Function to support build and move requests
     * @param nickname Nickname of the user that has moved/built
     */
    private void manageNextState(String nickname) {
        if (model.getCurrStep().equals("END")) {
            model.setNextPlayer();
            model.startTurn();

            if (model.getCurrStep().equals("REQUIRED"))
                viewManager.askStep(model.getCurrPlayer(), model.getCurrStateList());
            else{
                checkHasLost();
            }
        }
        else if (model.getCurrStep().equals("REQUIRED"))
            viewManager.askStep(nickname, model.getCurrStateList());
        else{
            checkHasLost();
        }
    }


    /**
     * If a player has lost it gets removed and the next player is picked. After that another check is called
     * recursively
     */
    private void checkHasLost(){
        model.findPossibleDestinations();
        if (!model.hasNotLostDuringMove() || !model.hasNotLostDuringBuild()){
            String playerToRemove = model.getCurrPlayer();
            model.setNextPlayer();
            model.notifyLoss(playerToRemove);
            model.deletePlayer(playerToRemove);
            if (model.endGame()) {
                viewManager.removeAndDisconnectAll();
                this.model = new Model();
                setModelObservers();
            }
            else {
                model.startTurn();
                if (model.getCurrStep().equals("REQUIRED"))
                    viewManager.askStep(model.getCurrPlayer(),model.getCurrStateList());
                else{
                    checkHasLost();
                }
            }
        }
    }


    @Override
    public synchronized void onDisconnection(String nickname) {
        viewManager.remove(nickname);
        viewManager.notifyDisconnection(nickname);
            if(model.getPlayersNickname().contains(nickname)) {
            viewManager.removeAndDisconnectAll();
            model = new Model();
            setModelObservers();
        }
    }

    @Override
    public synchronized void onEarlyDisconnection(VirtualView view) {
        if(viewManager.contains(view)) {
            viewManager.remove(view);
            viewManager.removeAndDisconnectAll();
            model = new Model();
            setModelObservers();
        }
    }

    @Override
    public synchronized void onConnection(VirtualView view) {

        switch (model.getCurrState()) {
            case SETUP_NUMOFPLAYERS:
                if(viewManager.getNumberOfViews() == 0) {
                    viewManager.add(view);
                    viewManager.askNumberOfPlayers();
                } else {
                    view.send(Messages.errorMessage("Someone is setting up the game."));
                    view.send(Messages.disconnect());
                    view.disconnect();
                }
                break;
            case SETUP_PLAYERS:
                if(viewManager.getNumberOfViews() < model.getNumberOfPlayers()) {
                    viewManager.add(view);
                    view.send(Messages.stepUpdate(Model.State.SETUP_PLAYERS));
                    if(viewManager.getNumberOfViews() == model.getNumberOfPlayers())
                        viewManager.askNickAndDate();
                } else {
                    view.send(Messages.errorMessage("Too many players connected"));
                    view.send(Messages.disconnect());
                    view.disconnect();
                }
                break;
            default:
                view.send(Messages.errorMessage("Game in progress... Retry Later"));
                view.send(Messages.disconnect());
                view.disconnect();
                break;
        }
    }

    @Override
    public synchronized void onSetStartPlayer(String nickname, String startPlayer) {
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