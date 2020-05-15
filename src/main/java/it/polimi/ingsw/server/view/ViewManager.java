package it.polimi.ingsw.server.view;


import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.gameMap.Coordinates;
import it.polimi.ingsw.server.parser.Messages;

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
        PlayerTurnObserver, ColorAssignmentObserver, ViewSelectObserver, BuilderMovementObserver, BuilderBuiltObserver, PlayerAddedObserver, ChosenStepObserver{

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

    public void remove(String nickname) {
        views.removeIf(view -> view.getNickname().equals(nickname));
    }

    public void askStep(String nickname) {
        for (VirtualView view : views)
            if (view.getNickname().equals(nickname))
                view.send(Messages.askStep());
    }

    public void askNumberOfPlayers() {
        if(views.size() > 1)
            System.err.println("Error: only one connection allowed");
        views.get(0).send(Messages.askNumberOfPlayers());
    }

    public void askNickAndDate() {
        for(VirtualView view : views)
            if(view.getNickname() == null)
                view.send(Messages.askNickAndDate());

    }

    public void askColor(String player, Set<String> chosenColors) {
        for(VirtualView view : views)
            if(view.getNickname().equals(player))
                view.send(Messages.askColor(chosenColors));
    }


    public void askBuilders(String player){
        for(VirtualView view : views)
            if(view.getNickname().equals(player))
                view.send(Messages.askBuilders());
    }

    public void askGod(String nickname, Map<String, String> godDescriptions, Set<String> chosenGodCards) {
        for(VirtualView view : views)
            if(view.getNickname().equals(nickname))
                view.send(Messages.askGod(godDescriptions, chosenGodCards));
    }

    //Observer Methods are multiplexed to the right VirtualViews

    @Override
    public void updatePossibleBuildDst(Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2, Set<Coordinates> possibleDstBuilder1forDome, Set<Coordinates> possibleDstBuilder2forDome) {

        selectedView.send(Messages.possibleBuildDestinations(possibleDstBuilder1, possibleDstBuilder2, possibleDstBuilder1forDome, possibleDstBuilder2forDome));

        //cleanSelection();
    }

    @Override
    public void updatePossibleMoveDst(Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2) {
        selectedView.send(Messages.possibleMoveDestinations(possibleDstBuilder1, possibleDstBuilder2));

        //cleanSelection();
    }

    @Override
    public void onBuildersPlacedUpdate(String nickname, Coordinates positionBuilder1, Coordinates positionBuilder2, boolean result) {
        if(result)
            for(VirtualView view : views)
                view.send(Messages.buildersPlacement(nickname, positionBuilder1, positionBuilder2, true));
        else
            selectedView.send(Messages.buildersPlacement(nickname, positionBuilder1, positionBuilder2, false));

        //cleanSelection();
    }

    @Override
    public void onEndGameUpdate(String winnerNickname) {
        for(VirtualView view : views)
            view.send(Messages.endGame(winnerNickname));

        //cleanSelection();
    }

    @Override
    public void onWrongInsertionUpdate(String error) {
        selectedView.send(Messages.errorMessage(error));

        //cleanSelection();
    }

    @Override
    public void onWrongNumberInsertion() {
        views.get(0).send(Messages.errorNumber());

        //cleanSelection();
    }

    @Override
    public void onLossUpdate(String nickname) {
        selectedView.send(Messages.lostGame());

        //cleanSelection();
    }

    @Override
    public void onStateUpdate(Model.State currState) {
        for(VirtualView view : views)
            view.send(Messages.stepUpdate(currState));

        //cleanSelection();
    }

    @Override
    public void onPlayerTurn(String nickname) {
        for (VirtualView view : views)
            view.send(Messages.turnUpdate(nickname));


        //cleanSelection();
    }

    @Override
    public void onColorAssigned(String nickname, String color, boolean result) {
        if(result)
            for (VirtualView view : views)
                view.send(Messages.colorUpdate(nickname, color, true));
        else
            selectedView.send(Messages.colorUpdate(nickname, color, false));

        //cleanSelection();
    }

    @Override
    public void onViewSelect(String nickname) {
        for(VirtualView view : views)
            if(view.getNickname() != null && view.getNickname().equals(nickname))
                selectedView = view;
    }



    @Override
    public void onBuilderBuild(String nickname, Coordinates src, Coordinates dst, boolean dome, boolean result) {
        if(result)
            for(VirtualView view : views)
                view.send(Messages.build(nickname, src, dst, dome, true));
        else
            selectedView.send(Messages.build(nickname, src, dst, dome, false));

        //cleanSelection();
    }

    @Override
    public void onBuilderMovement(String nickname, Coordinates src, Coordinates dst, boolean result) {
        if(result)
            for(VirtualView view : views)
                view.send(Messages.move(nickname, src, dst, true));
        else
            selectedView.send(Messages.move(nickname, src, dst, false));

        //cleanSelection();
    }

    /**
     * reset view selected to prevent wrong notify usages from Model
     */
    private void cleanSelection() {
        selectedView = null;
    }

    @Override
    public void onPlayerAdded(String nickname, boolean result) {
        if(result)
            for(VirtualView view : views)
                view.send(Messages.playerAdded(nickname, true));
        else {
            selectedView.setNickname(null);
            selectedView.send(Messages.playerAdded(nickname, false));
        }

        //cleanSelection();
    }

    @Override
    public void onGodCardAssigned(String nickname, String card, boolean result) {
        if (result)
            for (VirtualView view : views)
                view.send(Messages.godCardAssigned(nickname, card, true));
        else
            selectedView.send(Messages.godCardAssigned(nickname, card, false));
    }

    @Override
    public void onChosenStep(String nickname, String step, boolean result) {
        selectedView.send(Messages.stepChoice(nickname, step, result));
    }


}
