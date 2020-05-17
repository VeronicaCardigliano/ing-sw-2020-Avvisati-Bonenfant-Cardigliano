package it.polimi.ingsw.client;

import java.util.Map;
import java.util.Set;

//this Interface contains all View's methods, which are just defined here and implemented in CLI/GUI
public interface View {

    void askNumberOfPlayers ();
    void askNickAndDate ();
    void chooseMatchGodCards(int numOfPlayers, Map<String, String> godDescriptionsParam);
    void askGodCard (Map<String, String> godDescriptions, Set<String> chosenGodCards);
    void chooseStartPlayer (Set<String> players);
    void askBuilderColor (Set<String> chosenColors);
    void placeBuilders ();
    void chooseNextStep ();
    void build();
    void move();
}
