package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.gameMap.Coordinates;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class CliTest {

    Cli cli;

    @AfterEach
    public void resetStaticAttributes() {
        Cli.occupiedCells = new HashMap<>();
        Cli.chosenColorsForPlayer = new HashMap<>();
        Cli.chosenGodCards = new HashMap<>();
        Cli.chosenColors = new HashSet<>();
    }

    @Test
    public void genericTest(){

        File inputTextFile = new File("src/test/java/it/polimi/ingsw/client/cli/cliTestFiles/test1Generic");
        try {

            InputStream inputTextStream = new FileInputStream(inputTextFile);
            cli = new Cli(inputTextStream);

            Set<String> chosenGodCards = new HashSet<>();
            Set<String> chosenColors = new HashSet<>();

            ArrayList<Coordinates> selectedCells1 = new ArrayList<>();
            ArrayList<Coordinates> selectedCells2 = new ArrayList<>();
            ArrayList<Coordinates> selectedCells3 = new ArrayList<>();

            Set<Coordinates> possibleDst1 = new HashSet<>();
            Set<Coordinates> possibleDst2 = new HashSet<>();

            //the network handler'll call CLI methods
            cli.setChosenGodCard("Aldo", "Atlas");
            cli.setChosenGodCard("Nic","Artemis");
            cli.setChosenGodCard("Rick7", "Pan");

            cli.askNumberOfPlayers();
            cli.askNickAndDate();
            cli.askNickAndDate();
            cli.askNickAndDate();
            cli.askGodCard ((new Model()).getGodDescriptions(), chosenGodCards);
            chosenGodCards.add("Atlas");
            cli.askGodCard ((new Model()).getGodDescriptions(), chosenGodCards);
            chosenGodCards.add("Artemis");
            cli.askGodCard ((new Model()).getGodDescriptions(), chosenGodCards);
            chosenGodCards.add("Pan");
            cli.askBuilderColor(chosenColors);
            chosenColors.add("WHITE");
            cli.setChosenColor("Aldo", "white");
            cli.askBuilderColor(chosenColors);
            chosenColors.add("MAGENTA");
            cli.setChosenColor("Nic", "Magenta");
            cli.askBuilderColor(chosenColors);
            chosenColors.add("LIGHT_BLUE");
            cli.setChosenColor("Rick7", "Light_blue");

            cli.placeBuilders("Aldo");
            selectedCells1.add(new Coordinates(0,0));
            selectedCells1.add(new Coordinates(1,2));
            cli.setOccupiedCells("Aldo",selectedCells1);

            cli.placeBuilders("Nic");
            selectedCells2.add(new Coordinates(1,4));
            selectedCells2.add(new Coordinates(1,3));
            cli.setOccupiedCells("Nic",selectedCells2);

            cli.placeBuilders("Rick7");
            selectedCells3.add(new Coordinates(4,4));
            selectedCells3.add(new Coordinates(4,3));
            cli.setOccupiedCells("Rick7",selectedCells3);

            cli.setNickname("Nic");
            possibleDst1.add(new Coordinates(0,4));
            possibleDst1.add(new Coordinates(0,3));
            possibleDst1.add(new Coordinates(2,3));
            possibleDst1.add(new Coordinates(2,4));

            possibleDst2.add(new Coordinates(0,2));
            possibleDst2.add(new Coordinates(0,3));
            possibleDst2.add(new Coordinates(0,4));
            possibleDst2.add(new Coordinates(2,2));
            possibleDst2.add(new Coordinates(2,3));
            possibleDst2.add(new Coordinates(2,4));

            cli.updatePossibleMoveDst(possibleDst1, possibleDst2);

            cli.onBuilderMovement("Nic", new Coordinates(1,3), new Coordinates(0,2), true);

        } catch (FileNotFoundException e) {
            System.out.println("Input file error during testing!");
        }
    }


}