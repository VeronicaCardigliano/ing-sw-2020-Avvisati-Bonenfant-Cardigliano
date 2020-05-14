package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.server.model.gameMap.Coordinates;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

class CliGameMapTest {

    @AfterEach
    public void resetStaticAttributes() {
        Cli.occupiedCells = new HashMap<>();
        Cli.chosenColorsForPlayer = new HashMap<>();
        Cli.chosenGodCardsForPlayer = new HashMap<>();
    }

    @Test
    public void genericTest() {

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
            Cli.setOccupiedCells("Carlo", occupiedCells1);

            ArrayList<Coordinates> occupiedCells2 = new ArrayList<>();
            occupiedCells2.add(new Coordinates(0,0));
            occupiedCells2.add(new Coordinates(3,4));
            Cli.setOccupiedCells("Sara", occupiedCells2);

            Cli.setChosenColor("Carlo", "LIGHT_BLUE");
            Cli.setChosenColor("Sara", "MAGENTA");
            Cli.setChosenGodCard("Carlo", "Athena");
            Cli.setChosenGodCard("Sara", "Apollo");
            map.print(Cli.getOccupiedCells(), possibleBuilder1, null, 1);

    }

}