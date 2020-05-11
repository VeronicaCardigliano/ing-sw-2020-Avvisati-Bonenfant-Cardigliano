package it.polimi.ingsw.client;

import it.polimi.ingsw.server.model.gameMap.Coordinates;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

class GameMapTest {

    @Test
    public void genericTest() {
        File inputTextFile = new File("src/test/java/it/polimi/ingsw/server/model/modelTestFiles/test1Generic");
        try {
            InputStream inputTextStream = new FileInputStream(inputTextFile);
            Cli cli = new Cli(inputTextStream);
            GameMap map = new GameMap();
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
            cli.setChosenColor("Sara", "GREY");
            map.print(cli.getOccupiedCells(), possibleBuilder1, null, 1);

        } catch (FileNotFoundException e) {
            System.out.println("Input file error during testing!");
        }
    }

}