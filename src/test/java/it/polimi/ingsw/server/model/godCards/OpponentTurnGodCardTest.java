package it.polimi.ingsw.server.model.godCards;

import it.polimi.ingsw.server.model.gameMap.Builder;
import it.polimi.ingsw.server.model.gameMap.IslandBoard;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.parser.GodCardParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OpponentTurnGodCardTest {

    static GodCard athena;
    static GodCard defaultCard;

    static IslandBoard gameMap;
    static Player player1;
    static Player player2;

    static Builder athena1;
    static Builder athena2;
    static Builder default1;
    static Builder default2;

    @BeforeAll
    public static void setup() {
        player1 = new Player("player1");
        player2 = new Player("player2");

        GodCardParser parser = new GodCardParser("src/main/java/it/polimi/ingsw/server/parser/cards.json");

        athena = parser.createCard(player1, "Athena");
        defaultCard = parser.createCard(player2, "default");

        player1.setGodCard(athena);
        player2.setGodCard(defaultCard);

        athena1 = new Builder(player1);
        athena2 = new Builder(player1);

        default1 = new Builder(player2);
        default2 = new Builder(player2);


    }

    @BeforeEach
    public void cleanMap() {
        gameMap = new IslandBoard();

        athena.setGameMap(gameMap);
        defaultCard.setGameMap(gameMap);

        gameMap.getCell(2,2).setOccupant(athena1);
        gameMap.getCell(4,4).setOccupant(athena2);

        gameMap.getCell(0,0).setOccupant(default1);
        gameMap.getCell(0,4).setOccupant(default2);

    }

    @Test
    public void athenaTest() {
        System.out.println("#Testing Athena behavior...");

        //starting turn for player2
        defaultCard.startTurn();

        //default1 trying to move up
        assertTrue(gameMap.getCell(0,1).addBlock());
        assertTrue(defaultCard.move(0,0,0,1));

        //default1 building
        assertTrue(defaultCard.build(0,1,1,1, false));

        //starting turn for player1
        athena.startTurn();

        //athena god card moving up and activating its power
        assertTrue(gameMap.getCell(2,3).addBlock());
        assertTrue(athena.move(2,2,2,3));

        //adding blocks in (3,3)
        gameMap.getCell(3,3).addBlock();
        gameMap.getCell(3,3).addBlock();
        assertEquals(2, gameMap.getCell(3,3).getHeight());

        //athena shouldn't block herself
        assertTrue(athena.move(2,3,3,3));


        //starting turn for player1
        defaultCard.startTurn();

        //trying to moveUp
        assertTrue(gameMap.getCell(0,2).addBlock());
        assertTrue(gameMap.getCell(0,2).addBlock());
        assertFalse(defaultCard.askMove(0,1,0,2));




    }

}
