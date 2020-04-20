package it.polimi.ingsw.model;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WinConditionGodCardTest {
    static GodCard godCardPan;
    static GodCard godCardChronos;
    static Player player;
    static IslandBoard gameMap;
    static Builder builder1;
    static Builder builder2;
    static int maxCoordinate = IslandBoard.dimension - 1;

    final static String panJSONString = "{" +
            "\"type\": \"WIN\"," +
            "\"minimumDownStepsToWin\": 2 }";

    final static String chronusJSONString = "{" +
            "\"type\": \"WIN\"," +
            "\"completeTowersToWin\": 5 }";

    @BeforeAll
    public static void setup() {
        System.out.println("\nWINCONDITION_GODCARD TESTS");

        System.out.println("creating test player 1...");
        player = new Player("player1");
        builder1 = new Builder(player);
        builder2 = new Builder(player);
        player.setBuilders(builder1, builder2);

        System.out.println("giving player1 Pan card...");
        godCardPan = new WinConditionGodCard(player, new JSONObject(panJSONString));
        godCardChronos = new WinConditionGodCard(player, new JSONObject(chronusJSONString));

    }

    @BeforeEach
    public void cleanMap() {

        godCardPan.startTurn();
        gameMap = new IslandBoard();
        godCardPan.setGameMap(gameMap);

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
        System.out.println("# Testing Chronus Win Conidition...");

        godCardChronos.setGameMap(gameMap);

        assertTrue(godCardChronos.move(2,2, 2,3));
        assertFalse(godCardChronos.winCondition());

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
        assertTrue(godCardChronos.move(4,4,4,3));
        assertTrue(godCardChronos.winCondition());


    }
}
