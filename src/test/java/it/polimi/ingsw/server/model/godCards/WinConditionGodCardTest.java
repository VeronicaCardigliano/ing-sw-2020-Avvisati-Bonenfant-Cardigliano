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
    static Player player1;
    static Player player2;
    static IslandBoard gameMap;

    //builders for player 1
    static Builder builder1;
    static Builder builder2;

    //builders for player 2
    static Builder builder3;
    static Builder builder4;



    @BeforeAll
    public static void setup() {
        System.out.println("\nWINCONDITION_GODCARD TESTS");

        System.out.println("creating test player 1...");
        player1 = new Player("player1");
        player2 = new Player("player2");

        builder1 = new Builder(player1);
        builder2 = new Builder(player1);
        builder3 = new Builder(player2);
        builder4 = new Builder(player2);

        player1.setBuilders(builder1, builder2);
        player2.setBuilders(builder3, builder4);

        GodCardParser parser = new GodCardParser("src/main/java/it/polimi/ingsw/server/parser/cards.json");
        godCardPan = parser.createCard(player1, "Pan");
        godCardChronus = parser.createCard(player2, "Chronus");

    }

    @BeforeEach
    public void cleanMap() {

        gameMap = new IslandBoard();
        godCardPan.setGameMap(gameMap);
        godCardChronus.setGameMap(gameMap);

        Cell cell1 = gameMap.getCell(2,2);
        Cell cell2 = gameMap.getCell(4,4);

        Cell cell3 = gameMap.getCell(2,3);
        Cell cell4 = gameMap.getCell(2,4);

        cell1.setOccupant(builder1);
        cell2.setOccupant(builder2);

        cell3.setOccupant(builder3);
        cell4.setOccupant(builder4);
    }

    @Test
    public void defaultWinCondition() {
        //elevating builder 1 to height 2
        builder1.getCell().addBlock();
        builder1.getCell().addBlock();

        //set cell adiacent to builder1 to height 3
        gameMap.getCell(2,1).addBlock();
        gameMap.getCell(2,1).addBlock();
        gameMap.getCell(2,1).addBlock();

        godCardPan.startTurn();

        assertTrue(godCardPan.move(2,2,2,1));
        assertTrue(godCardPan.winCondition());

        //set cell adiacent to builder2 to height1
        gameMap.getCell(4,3).addBlock();
        assertTrue(godCardPan.move(4,4,4,3));
        assertFalse(godCardPan.winCondition());


    }

    @Test
    public void panWinCondition() {

        godCardPan.startTurn();

        assertFalse(godCardPan.winCondition());

        //elevating builder1 to height 2 and then moving him down
        builder1.getCell().addBlock();
        builder1.getCell().addBlock();
        assertEquals(2, builder1.getCell().getHeight());

        assertTrue(godCardPan.move(2,2, 2,1));
        assertTrue(godCardPan.winCondition());

        cleanMap();

        //elevating builder1 to height 3 and then moving him down
        builder1.getCell().addBlock();
        builder1.getCell().addBlock();
        builder1.getCell().addBlock();
        assertEquals(3, builder1.getCell().getHeight());

        assertTrue(godCardPan.move(2,2, 2,1));
        assertTrue(godCardPan.winCondition());

        cleanMap();

        //elevating builder1 to height 1 and then moving him down
        builder1.getCell().addBlock();
        assertEquals(1, builder1.getCell().getHeight());

        assertTrue(godCardPan.move(2,2, 2,1));
        assertFalse(godCardPan.winCondition());

    }


    @Test
    public void chronusWinCondition() {
        Cell cell;
        System.out.println("# Testing Chronus Win Condition...");


        assertTrue(godCardChronus.move(2,4, 3,4));
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

        //move 4
        assertTrue(godCardChronus.move(3,4,2,4));
        assertTrue(godCardChronus.winCondition());


    }

    /*@Test
    public void chronusWinConditionDuringOpponentsTurn() {
        Cell cell;

        //building 4 complete towers
        for(int j = 0; j < IslandBoard.dimension - 1; j++) {
            cell = gameMap.getCell(0, j);
            cell.addBlock();
            cell.addBlock();
            cell.addBlock();
            cell.addDome();

            assertEquals(3, cell.getHeight());
            assertTrue(cell.isDomePresent());
        }


        //setting tower to be completed by Pan --> should make Chronus win
        Cell toBeCompleted = gameMap.getCell(4,2);
        toBeCompleted.addBlock();
        toBeCompleted.addBlock();
        toBeCompleted.addBlock();

        godCardPan.startTurn();
        assertTrue(godCardPan.move(4,4, 4,3));
        assertTrue(godCardPan.build(4,3, 4,2, true));
    }*/
}
