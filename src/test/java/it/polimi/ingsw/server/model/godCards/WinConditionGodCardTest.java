package it.polimi.ingsw.server.model.godCards;

import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.gameMap.Builder;
import it.polimi.ingsw.server.model.gameMap.Cell;
import it.polimi.ingsw.server.model.gameMap.IslandBoard;
import it.polimi.ingsw.server.parser.GodCardParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WinConditionGodCardTest {
    static GodCard godCardPan;
    static GodCard godCardChronus;
    static Player player;
    static IslandBoard gameMap;
    static Builder builder1;
    static Builder builder2;
    static int maxCoordinate = IslandBoard.dimension - 1;


    @BeforeAll
    public static void setup() {
        System.out.println("\nWINCONDITION_GODCARD TESTS");

        System.out.println("creating test player 1...");
        player = new Player("player1");
        builder1 = new Builder(player);
        builder2 = new Builder(player);
        player.setBuilders(builder1, builder2);

        GodCardParser parser = new GodCardParser("src/main/java/it/polimi/ingsw/server/parser/cards.json");
        godCardPan = parser.createCard(player, "Pan");
        godCardChronus = parser.createCard(player, "Chronus");

    }

    @BeforeEach
    public void cleanMap() {

        gameMap = new IslandBoard();
        godCardPan.setGameMap(gameMap);
        godCardPan.startTurn();

        Cell cell1 = gameMap.getCell(maxCoordinate / 2,maxCoordinate / 2);
        Cell cell2 = gameMap.getCell(maxCoordinate,maxCoordinate);

        System.out.println("repositioning one builder at the center and one at bottom right corner...\n");
        cell1.setOccupant(builder1);
        cell2.setOccupant(builder2);
    }

    @Test
    public void defaultWinCondition() {
        System.out.println("# Testing the winning condition...");
        //elevating builder 1 to height 2
        builder1.getCell().addBlock();
        builder1.getCell().addBlock();

        //set cell adiacent to builder1 to height 3
        gameMap.getCell(2,3).addBlock();
        gameMap.getCell(2,3).addBlock();
        gameMap.getCell(2,3).addBlock();

        assertTrue(godCardPan.move(2,2,2,3));
        assertTrue(godCardPan.winCondition());

        //set cell adiacent to builder2 to height1
        gameMap.getCell(4,3).addBlock();
        assertTrue(godCardPan.move(4,4,4,3));
        assertFalse(godCardPan.winCondition());


    }

    @Test
    public void panWinCondition() {
        System.out.println("# Testing Pan Win Condition...");

        assertFalse(godCardPan.winCondition());

        //elevating builder1 to height 2 and then moving him down
        builder1.getCell().addBlock();
        builder1.getCell().addBlock();
        assertEquals(2, builder1.getCell().getHeight());

        assertTrue(godCardPan.move(2,2, 2,3));
        assertTrue(godCardPan.winCondition());

        cleanMap();

        //elevating builder1 to height 3 and then moving him down
        builder1.getCell().addBlock();
        builder1.getCell().addBlock();
        builder1.getCell().addBlock();
        assertEquals(3, builder1.getCell().getHeight());

        assertTrue(godCardPan.move(2,2, 2,3));
        assertTrue(godCardPan.winCondition());

        cleanMap();

        //elevating builder1 to height 3 and then moving him down
        builder1.getCell().addBlock();
        assertEquals(1, builder1.getCell().getHeight());

        assertTrue(godCardPan.move(2,2, 2,3));
        assertFalse(godCardPan.winCondition());

    }


    @Test
    public void chronusWinCondition() {
        Cell cell;
        System.out.println("# Testing Chronus Win Condition...");

        godCardChronus.setGameMap(gameMap);

        assertTrue(godCardChronus.move(2,2, 2,3));
        assertFalse(godCardChronus.winCondition());

        //building 5 complete towers
        for(int j = 0; j < IslandBoard.dimension; j++) {
            cell = gameMap.getCell(0, j);
            cell.addBlock();
            cell.addBlock();
            cell.addBlock();
            cell.addDome();

            assertEquals(3, cell.getHeight());
            assertTrue(cell.isDomePresent());

        }

        //move builder 2
        assertTrue(godCardChronus.move(4,4,4,3));
        assertTrue(godCardChronus.winCondition());


    }
}
