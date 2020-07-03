package it.polimi.ingsw.server.view;


import it.polimi.ingsw.interfaces.view.*;
import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.gameMap.Coordinates;
import it.polimi.ingsw.network.Messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author thomas
 * Container for Virtual Views. Supposed to multiplex notifies coming from ModelObservable.
 */
public class ViewManager implements BuilderPossibleBuildObserver, BuilderPossibleMoveObserver, BuildersPlacedObserver,
        EndGameObserver, ErrorsObserver, PlayerLoseObserver, StateObserver, GodChoiceObserver ,
        PlayerTurnObserver, ColorAssignmentObserver, ViewSelectObserver, BuilderMovementObserver, BuilderBuiltObserver, PlayerAddedObserver, ChosenStepObserver, StartPlayerSetObserver{

    List<VirtualView> views;

    VirtualView selectedView;

    public int getNumberOfViews() {
        return views.size();
    }



    public ViewManager() {

        views = new ArrayList<>();
    }

    public void add(VirtualView view) {
        views.add(view);
    }

    /**
     * Remove VirtualView bound to nickname
     * @param nickname String bound to the VirtualView
     */
    public void remove(String nickname) {
        views.removeIf(view -> view.getNickname() != null && view.getNickname().equals(nickname));

    }

    public void remove(VirtualView view) {
        views.remove(view);
    }

    /**
     * Disconnects and remove all VirtualViews
     */
    public void removeAndDisconnectAll() {
        for(VirtualView view : views) {
            view.send(Messages.disconnect());
            view.disconnect();

        }

        views = new ArrayList<>();
    }

    /**
     * Notifies all views that a player disconnected
     * @param nickname player who disconnected
     */
    public void notifyDisconnection(String nickname) {
        for(VirtualView view : views) {
            view.send(Messages.playerDisconnected(nickname));
        }
    }

    public boolean contains(VirtualView view) {
        return views.contains(view);
    }


    //---------------------------------------REQUESTS-------------------------------------------------------------------

    /**
     * Sends askStep request over the network to VirtualView bound to nickname
     * @param nickname player to send request
     * @param stateList list to choose from
     */
    public void askStep(String nickname, ArrayList<String> stateList) {
        for (VirtualView view : views)
            if (view.getNickname().equals(nickname))
                view.send(Messages.askStep(stateList));
    }

    /**
     * Sends askNumberOfPlayers to the unique VirtualView contained
     */
    public void askNumberOfPlayers() {
        if(views.size() > 1)
            System.err.println("Error: only one connection allowed");
        views.get(0).send(Messages.askNumberOfPlayers());
    }

    /**
     * Sends to all virtual views askNickAndDate request
     */
    public void askNickAndDate() {
        for(VirtualView view : views)
            if(view.getNickname() == null)
                view.send(Messages.askNickAndDate());

    }

    /**
     * Sends to view bound to nickname the godDescriptions map to choose from
     * @param nickname player bound to virtual view
     * @param numOfPlayers represents number of players for this particular game
     * @param godDescriptions map having gods' name as keys and descriptions as values
     */
    public void chooseMatchGodCards(String nickname, int numOfPlayers, Map<String, String> godDescriptions) {
        for(VirtualView view : views)
            if(view.getNickname().equals(nickname))
                view.send(Messages.chooseMatchGodCards(numOfPlayers, godDescriptions));
    }

    /**
     * Sends to view bound to player String the askColor request over the network
     * @param player string representing player's nickname to ask to
     * @param chosenColors colors already chosen
     */
    public void askColor(String player, Set<String> chosenColors) {
        for(VirtualView view : views)
            if(view.getNickname().equals(player))
                view.send(Messages.askColor(chosenColors));
    }

    /**
     * Sends to view bound to player string builder positioning request over the network
     * @param player player bound to the view to notify
     */
    public void askBuilders(String player){
        for(VirtualView view : views)
            if(view.getNickname().equals(player))
                view.send(Messages.askBuilders());
    }

    /**
     * Sends to view bound to nickname god card choice request over the network
     * @param nickname player bound to the view to notify
     * @param godDescriptions map having gods' name as keys and descriptions as values
     * @param chosenGodCards Set of already chosen cards
     */
    public void askGod(String nickname, Map<String, String> godDescriptions, Set<String> chosenGodCards) {
        for(VirtualView view : views)
            if(view.getNickname().equals(nickname))
                view.send(Messages.askGod(godDescriptions, chosenGodCards));
    }

    /**
     * Sends to view bound to nickname starting player choice request over the network
     * @param nickname player bound to the view to notify
     * @param players Set of registered players to the game
     */
    public void chooseStartPlayer(String nickname, Set<String> players) {
        for(VirtualView view : views)
            if(view.getNickname().equals(nickname))
                view.send(Messages.chooseStartPlayer(players));
    }
    //----------------------------------------------------------------------------------------------------------------
    //Observer Methods are multiplexed to the right VirtualViews


    /**
     * Sends to selected view possible build destinations for its builders
     * @param possibleDstBuilder1 build destinations for builder 1
     * @param possibleDstBuilder2 build destinations for builder 2
     * @param possibleDstBuilder1forDome dome build destinations for builder 1
     * @param possibleDstBuilder2forDome dome build destinations for builder 2
     */
    @Override
    public void updatePossibleBuildDst(Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2, Set<Coordinates> possibleDstBuilder1forDome, Set<Coordinates> possibleDstBuilder2forDome) {

        selectedView.send(Messages.possibleBuildDestinations(possibleDstBuilder1, possibleDstBuilder2, possibleDstBuilder1forDome, possibleDstBuilder2forDome));

    }

    /**
     * Sends to selected view possible move destinations for its builders
     * @param possibleDstBuilder1 move destinations for builder 1
     * @param possibleDstBuilder2 move destinations for builder 2
     */
    @Override
    public void updatePossibleMoveDst(Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2) {
        selectedView.send(Messages.possibleMoveDestinations(possibleDstBuilder1, possibleDstBuilder2));

    }

    /**
     * Sends Model answer to builders positioning request. If the answer (result) is positive (true) then it broadcast
     * this message to everyone, otherwise it sends the message to the view who requested.
     * @param nickname player who requested builder positioning
     * @param positionBuilder1 position for builder 1
     * @param positionBuilder2 position for builder 2
     * @param result outcome of the request
     */
    @Override
    public void onBuildersPlacedUpdate(String nickname, Coordinates positionBuilder1, Coordinates positionBuilder2, boolean result) {
        if(result)
            for(VirtualView view : views)
                view.send(Messages.buildersPlacement(nickname, positionBuilder1, positionBuilder2, true));
        else
            selectedView.send(Messages.buildersPlacement(nickname, positionBuilder1, positionBuilder2, false));

    }

    /**
     * Sends to all views a game over notification with winner over the network
     * @param winnerNickname player who won
     */
    @Override
    public void onEndGameUpdate(String winnerNickname) {
        for(VirtualView view : views)
            view.send(Messages.endGame(winnerNickname));
    }

    /**
     * Sends to selected view an error message over the network
     * @param error String describing the error
     */
    @Override
    public void onWrongInsertionUpdate(String error) {
        for(VirtualView view : views)
            if (view.getNickname() != null && view.notRegistered())
                selectedView = view;

        selectedView.send(Messages.errorMessage(error));
    }

    /**
     * Sends to the unique view (first view) a negative outcome for his number of players setup request over the network
     */
    @Override
    public void onWrongNumberInsertion() {
        views.get(0).send(Messages.errorNumber());

    }

    /**
     * Sends a loss notification about a player to every view over the network
     * @param nickname player who lost
     */
    @Override
    public void onLossUpdate(String nickname) {
        for(VirtualView view : views)
            view.send(Messages.lostGame(nickname));
    }

    /**
     * Sends to every view a Model State update over the network
     * @param currState new Model state
     */
    @Override
    public void onStateUpdate(Model.State currState) {
        for(VirtualView view : views)
            view.send(Messages.stepUpdate(currState));
    }

    /**
     * Notifies every view that nickname is now playing
     * @param nickname player that is now playing
     */
    @Override
    public void onPlayerTurn(String nickname) {
        for (VirtualView view : views)
            view.send(Messages.turnUpdate(nickname));
    }

    /**
     * Sends Model answer to color setup request. Notifies every view if the outcome (result) is positive (true), otherwise
     * it only notifies the selected view
     * @param nickname player who requested
     * @param color color chosen
     * @param result outcome of the request
     */
    @Override
    public void onColorAssigned(String nickname, String color, boolean result) {
        if(result)
            for (VirtualView view : views)
                view.send(Messages.colorUpdate(nickname, color, true));
        else
            selectedView.send(Messages.colorUpdate(nickname, color, false));

    }

    /**
     * Select a specific view to which send future notifications
     * @param nickname player bound to the view to notify
     */
    @Override
    public void onViewSelect(String nickname) {
        for(VirtualView view : views)
            if(view.getNickname() != null && view.getNickname().equals(nickname))
                selectedView = view;
    }


    /**
     * Sends Model answer to build request. Notifies everyone if the outcome is positive, otherwise it only notifies the
     * selected view
     * @param nickname player who did the request
     * @param src position of the player's builder that is trying to build
     * @param dst destination of the build
     * @param dome true if trying to build a dome
     * @param result outcome of the request
     */
    @Override
    public void onBuilderBuild(String nickname, Coordinates src, Coordinates dst, boolean dome, boolean result) {
        if(result)
            for(VirtualView view : views)
                view.send(Messages.build(nickname, src, dst, dome, true));
        else
            selectedView.send(Messages.build(nickname, src, dst, dome, false));

    }

    /**
     * Sends Model answer to move request. Notifies everyone if the outcome is positive, otherwise it only notifies the
     * selected view
     * @param nickname player who did the request
     * @param src position of the player's builder that is trying to move
     * @param dst builder's destination
     * @param result outcome of the request
     */
    @Override
    public void onBuilderMovement(String nickname, Coordinates src, Coordinates dst, boolean result) {
        if(result)
            for(VirtualView view : views)
                view.send(Messages.move(nickname, src, dst, true));
        else
            selectedView.send(Messages.move(nickname, src, dst, false));

    }

    /**
     * Sends to everyone a notification about a builder that was pushed
     * @param nickname player owning the pushed builder
     * @param src builder position from which it was pushed
     * @param dst destination of the pushed builder
     */
    @Override
    public void onBuilderPushed(String nickname, Coordinates src, Coordinates dst) {
        for(VirtualView view : views)
            view.send(Messages.builderPushed(nickname, src, dst));
    }

    /**
     * Sends Model answer to nickname registration request. Notifies everyone if the outcome is positive, otherwise it only notifies the
     * selected view (be sure to select the correct view)
     * @param nickname nickname sent by the view for registration
     * @param result outcome of the request
     */
    @Override
    public void onPlayerAdded(String nickname, boolean result) {
        for(VirtualView view : views) {
            if(view.getNickname() != null && view.getNickname().equals(nickname) && view.notRegistered())
                selectedView = view;
        }

        if(result) {
            selectedView.register();

            for (VirtualView view : views)
                view.send(Messages.playerAdded(nickname, true));
        }
        else {
            selectedView.setNickname(null);
            selectedView.send(Messages.playerAdded(nickname, false));
        }

    }

    /**
     * Sends Model answer to card setup request. Notifies everyone if the outcome is positive, otherwise it only notifies the
     * selected view
     * @param nickname player who did the request
     * @param card card chosen
     * @param result outcome of the request
     */
    @Override
    public void onGodCardAssigned(String nickname, String card, boolean result) {
        if (result)
            for (VirtualView view : views)
                view.send(Messages.godCardAssigned(nickname, card, true));
        else
            selectedView.send(Messages.godCardAssigned(nickname, card, false));
    }

    /**
     * Sends Model answer to game cards setup request. Notifies everyone if the outcome is positive, otherwise it only notifies the
     * selected view
     * @param godCardsToUse chosen cards for the entire game
     * @param result outcome of the request
     */
    @Override
    public void onMatchGodCardsAssigned(Set<String> godCardsToUse, boolean result) {
        if(result)
            for(VirtualView view : views)
                view.send(Messages.setGodCardsToUse(godCardsToUse, true));
        else
            selectedView.send(Messages.setGodCardsToUse(godCardsToUse, false));
    }

    /**
     * Sends Model answer to move request to the selected view.
     * @param nickname player who did the request
     * @param step chosen step
     * @param result outcome of the request
     */
    @Override
    public void onChosenStep(String nickname, String step, boolean result) {
        selectedView.send(Messages.stepChoice(nickname, step, result));
    }


    /**
     * Sends Model answer to starting player setup request. Notifies everyone if the outcome is positive, otherwise it only notifies the
     * selected view
     * @param nickname player who did the request
     * @param result outcome of the request
     */
    @Override
    public void onStartPlayerSet(String nickname, boolean result) {
        if(result)
            for(VirtualView view : views)
                view.send(Messages.setStartPlayer(nickname, true));
        else
            selectedView.send(Messages.setStartPlayer(nickname, false));
    }
}
