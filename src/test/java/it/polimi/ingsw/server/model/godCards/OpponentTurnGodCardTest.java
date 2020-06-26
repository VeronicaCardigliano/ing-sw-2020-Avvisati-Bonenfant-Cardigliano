package it.polimi.ingsw.server.model.godCards;

import it.polimi.ingsw.server.model.Model;
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
    static GodCard limus;
    static GodCard defaultCard;

    static IslandBoard gameMap;
    static Player player1;
    static Player player2;
    static Player player3;

    static Builder athena1;
    static Builder athena2;
    static Builder default1;
    static Builder default2;
    static Builder limus1;
    static Builder limus2;

    @BeforeAll
    public static void setup() {
        player1 = new Player("player1");
        player2 = new Player("player2");
        player3 = new Player("player3");

        GodCardParser parser = new GodCardParser(Model.jsonPath);

        athena = parser.createCard(player1, "Athena");
        defaultCard = parser.createCard(player2, "default");
        limus = parser.createCard(player3, "Limus");

        player1.setGodCard(athena);
        player2.setGodCard(defaultCard);
        player3.setGodCard(limus);

        athena1 = new Builder(player1);
        athena2 = new Builder(player1);

        default1 = new Builder(player2);
        default2 = new Builder(player2);

        limus1 = new Builder(player3);
        limus2 = new Builder(player3);

        player2.setBuilders(default1, default2);

    }

    @BeforeEach
    public void cleanMap() {
        gameMap = new IslandBoard();

        athena.setGameMap(gameMap);
        defaultCard.setGameMap(gameMap);
        limus.setGameMap(gameMap);

        gameMap.getCell(0,0).setOccupant(default1);
        gameMap.getCell(0,4).setOccupant(default2);

    }

    @Test
    public void athenaTest() {
        System.out.println("#Testing Athena behavior...");

        gameMap.getCell(2,2).setOccupant(athena1);
        gameMap.getCell(4,4).setOccupant(athena2);

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

    @Test
    public void limusTest(){

        System.out.println("#Testing Limus behavior...");

        player3.setBuilders(limus1, limus2);

        gameMap.getCell(2,2).setOccupant(limus1);
        gameMap.getCell(4,4).setOccupant(limus2);

        //Stop a build on neighbourn space, allow only if it is
        //a dome to complete a tower

        //starting turn for player2
        defaultCard.startTurn();

        assertTrue(defaultCard.move(0,4, 1, 3));
        //cannot build near limus
        assertFalse(defaultCard.askBuild(1,3,2,3, false));
        assertFalse(defaultCard.askBuild(1,3,1,2, false));

        //now we add 3 blocks in Cell(1,2) to check the build dome
        gameMap.getCell(1,2).addBlock();
        gameMap.getCell(1,2).addBlock();
        gameMap.getCell(1,2).addBlock();

        //now it should build the dome
        assertTrue(defaultCard.askBuild(1,3, 1,2,true));
        assertTrue(defaultCard.build(1,3, 1,2,true));

        //now it's limus turn (player 3)
        limus.startTurn();

        assertTrue(limus.askMove(2,2,1,1));
        assertTrue(limus.move(2,2,1,1));
        assertTrue(limus.build(1,1,0,1, false));

        //back to player 2
        defaultCard.startTurn();

        assertTrue(defaultCard.move(0,0,0,1));
        assertFalse(defaultCard.build(1,0,1,2,false));
        //now what? the builder can't do anything?
        //It should end the game and be removed
    }

}
