package it.polimi.ingsw.view;

import it.polimi.ingsw.model.gameMap.Builder;
import it.polimi.ingsw.model.gameMap.Coordinates;

import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * view class notifies Controller (as an Observable) and is notified by Model (as an observer)
 */
public class View extends ViewObservable implements BuilderPossibleMoveObserver, BuilderPossibleBuildObserver,
                            ErrorsObserver, BuildersPlacementObserver, PlayerLoseObserver, EndGameObserver {

    private Scanner input;

    public View (InputStream source) {

        this.input = new Scanner(source);
    }

    public void askNumberOfPlayers () {
        System.out.println("Insert the number of players ");
        notifyNumberOfPlayers(Integer.parseInt(input.nextLine()));
    }

    public void askForNewPlayer () {
        System.out.println("Insert Player name: ");
        String nickname = input.nextLine();
        System.out.println("Insert Birthday date in the form \"yyyy.MM.dd\" ");
        String birthday = input.nextLine();

        notifyNewPlayer(nickname, birthday);
    }

    public void chooseGodCard (Map<String, String> godDescriptions, Set<String> chosenGodCards) {
 /*       boolean alreadyUsed = false;
        for (String s : godNames) {
            for (String x : chosenGodCards) {
                if (s.equals(x)) {
                    alreadyUsed = true;
                    break;
                }
            }
            if (!alreadyUsed) {
                System.out.println(s);
                System.out.println(Model.getGodDescription(s));
            }
        }
        System.out.println("Select your GodCard from the available ones"); */
        notifyGodCardChoice(input.nextLine());
    }

    public void chooseBuilderColor (Set<String> chosenColors) {
        boolean alreadyUsed = false;
        System.out.println("Available builder colors: ");
        //prints the colors only if they're still available
        for (Builder.BuilderColor color : Builder.BuilderColor.values()) {
            for (String alreadyChosen : chosenColors) {
                if (alreadyChosen.equals(color.toString()))
                    alreadyUsed = true;
            }

            if (!alreadyUsed)
                System.out.println(color.name().toUpperCase() + " ");
        }

        System.out.println("Select a color for your Builders ");
        notifyColorChoice(input.nextLine().toUpperCase());
    }

    public void chooseNextStep () {
        System.out.println("Insert M to move or B to build ");
        notifyStepChoice(input.nextLine().toUpperCase());
    }

    @Override
    public void updatePossibleBuildDst(Set possibleDstBuilder1, Set possibleDstBuilder2, Set possibleDstBuilder1forDome, Set possibleDstBuilder2forDome) {
        //show the possible destinations received, and take the choice notifying it to controller
        //notifyBuildChoice(src, dst, buildDome);
    }

    @Override
    public void updatePossibleMoveDst(Set possibleDstBuilder1, Set possibleDstBuilder2) {
        //show the possible destinations received
        //notifyMoveChoice(src, dst);
    }

    @Override
    public void onWrongInsertionUpdate(String error) {
        System.out.println(error);
    }

    @Override
    public void onLossUpdate(String currPlayer) {
        System.out.println("Player " + currPlayer + " lost the game");
    }

    @Override
    public void onEndGameUpdate(String winnerNickname) {
        System.out.println ("Player " + winnerNickname + " wins!!");
    }

    @Override
    public void onBuildersPlacementUpdate(Coordinates positionBuilder1, Coordinates positionBuilder2) {
        //send informations about the new placement of builders in order to modify the map
    }
}
