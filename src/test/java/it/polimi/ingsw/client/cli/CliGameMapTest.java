package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.server.model.gameMap.Coordinates;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

class CliGameMapTest {

    @AfterEach
    public void resetStaticAttributes() {
        Cli.occupiedCells = new HashMap<>();
        Cli.chosenColorsForPlayer = new HashMap<>();
        Cli.chosenGodCards = new HashMap<>();
        Cli.chosenColors = new HashSet<>();
    }

    @Test
    public void genericTest() {
        File inputTextFile = new File("src/test/java/it/polimi/ingsw/client/cli/cliTestFiles/cliGameMapGenericTest");
        try {
            InputStream inputTextStream = new FileInputStream(inputTextFile);
            Cli cli = new Cli(inputTextStream);
            CliGameMap map = new CliGameMap();
            ArrayList<Coordinates> occupiedCells1 = new ArrayList<>();
            Set<Coordinates> possibleBuilder1 = new HashSet<>();
            possibleBuilder1.add(new Coordinates(0,1));
            possibleBuilder1.add(new Coordinates(1,1));
            possibleBuilder1.add(new Coordinates(1,2));
            possibleBuilder1.add(new Coordinates(1,3));
            possibleBuilder1.add(new Coordinates(0,3));

            occupiedCells1.add(new Coordinates(0,2));
            occupiedCells1.add(new Coordinates(2,2));
            cli.setOccupiedCells("Carlo", occupiedCells1);

            ArrayList<Coordinates> occupiedCells2 = new ArrayList<>();
            occupiedCells2.add(new Coordinates(0,0));
            occupiedCells2.add(new Coordinates(3,4));
            cli.setOccupiedCells("Sara", occupiedCells2);

            cli.setChosenColor("Carlo", "LIGHT_BLUE");
            cli.setChosenColor("Sara", "MAGENTA");
            cli.setChosenGodCard("Carlo", "Athena");
            cli.setChosenGodCard("Sara", "Apollo");
            map.print(Cli.getOccupiedCells(), possibleBuilder1, null, 1);

        } catch (FileNotFoundException e) {
            System.out.println("Input file error during testing!");
        }
    }

}