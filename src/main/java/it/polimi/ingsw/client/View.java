package it.polimi.ingsw.client;

import it.polimi.ingsw.server.view.*;

import java.util.Map;
import java.util.Set;

//this Interface contains all View's methods, which are just defined here and implemented in CLI/GUI
public interface View {

    void askNumberOfPlayers ();
    void askForNewPlayer ();
    void chooseGodCard (Map<String, String> godDescriptions, Set<String> chosenGodCards);
    void chooseBuilderColor (Map<String, String> chosenColors);
    void placeBuilders (String nickname);
    void chooseNextStep ();

}
