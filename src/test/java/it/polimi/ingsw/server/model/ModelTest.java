package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.gameMap.Coordinates;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {
    static Model testModel;
    static ArrayList<Player> playersListTest;

    @BeforeAll
    public static void setup() {
        System.out.println("\nRUN METHOD TESTS");
    }

    @BeforeEach
    public void clean() {
        testModel = new Model();
        playersListTest = new ArrayList<>();
    }

    @Test
    public void inOrderOfBirthday () {
        assertTrue(testModel.setNumberOfPlayers(3));

        assertFalse(testModel.addPlayer("Wrong", "2050.01.01"));

        assertTrue(testModel.addPlayer("Paolo", "2000.07.01"));
        assertTrue(testModel.addPlayer("Anna","2006.10.11"));
        assertTrue(testModel.addPlayer("Marco","2000.06.12"));
        assertFalse(testModel.addPlayer("Marco","2000.06.12"));
        assertFalse(testModel.addPlayer("Wrong", "2050.01.01"));

        playersListTest = testModel.getPlayers();
        System.out.println("\nTesting if Players are in order of birthday...");
        assertEquals("Anna", playersListTest.get(0).getNickname());
        assertEquals("Paolo", playersListTest.get(1).getNickname());
        assertEquals("Marco", playersListTest.get(2).getNickname());
    }

    @Test
    public void correctlyDeleted () {
        testModel.setNumberOfPlayers(2);

        assertTrue(testModel.addPlayer("Paolo", "2000.07.01"));
        assertTrue(testModel.addPlayer("Anna","2006.10.11"));
        playersListTest = testModel.getPlayers();
        assertEquals("Anna", playersListTest.get(0).getNickname());

        testModel.deletePlayer (playersListTest.get(0).getNickname());

        System.out.println("\nTesting if player has been correctly deleted...");
        playersListTest = testModel.getPlayers();
        assertEquals("Paolo", playersListTest.get(0).getNickname());

    }

    @Test
    public void expectedExceptionIfPlayerToDeleteNotFound () {
        testModel.addPlayer("Paolo", "2000.07.01");
        Player samplePlayer = new Player("Beatrice");
        Assertions.assertThrows(IllegalArgumentException.class, () -> testModel.deletePlayer(samplePlayer.getNickname()));
    }

    @Test
    public void cardCorrectlyAssigned () {
        testModel.setNumberOfPlayers(3);

        testModel.addPlayer("Carlo", "1998.04.20");
        assertTrue(testModel.addPlayer("Paolo", "2000.07.01"));
        assertTrue(testModel.addPlayer("Anna","2006.10.11"));
        playersListTest = testModel.getPlayers();

        testModel.setNextPlayer();
        assertTrue(testModel.assignCard("Zeus"));
        //System.out.println("\nExpected error for GodCard name already used...");
        testModel.setNextPlayer();
        assertFalse(testModel.assignCard( "Zeus"));
        assertTrue(testModel.assignCard("Atlas"));
        //System.out.println("\nExpected error for non-existent GodCard name...");
        testModel.setNextPlayer();
        assertFalse(testModel.assignCard("Ciao"));
        assertTrue(testModel.assignCard("Limus"));
    }

    @Test
    public void colorsCorrectlyAssigned () {
        testModel.setNumberOfPlayers(2);

        testModel.addPlayer("Carlo", "1998.04.20");
        testModel.addPlayer("Luigi", "1999.02.01");
        playersListTest = testModel.getPlayers();

        testModel.setNextPlayer();
        assertTrue(testModel.assignColor("MAGENTA"));
        //System.out.println("\nExpected error for color already used...");
        testModel.setNextPlayer();
        assertFalse(testModel.assignColor( "MAGENTA"));
        //System.out.println("\nExpected error for non-existent color name...");
        assertFalse(testModel.assignColor("YELLOW"));
        assertTrue(testModel.assignColor("LIGHT_BLUE"));
    }


    @Test
    public void athenaVSPrometheus() {

        System.out.println("SETUP_NUMOFPLAYERS");

        assertTrue(testModel.setNumberOfPlayers(2));
        testModel.setNextState();

        System.out.println("SETUP_PLAYERS");

        assertTrue(testModel.addPlayer("player2", "1999.02.07"));
        assertTrue(testModel.addPlayer("player1", "1999.02.08"));
        testModel.setNextPlayer();
        testModel.setNextState();

        System.out.println("SETUP_CARDS");

        assertTrue(testModel.assignCard("Athena"));
        testModel.setNextPlayer();
        assertTrue(testModel.assignCard("Prometheus"));
        testModel.setNextPlayer();
        testModel.setNextState();

        System.out.println("SETUP_BUILDERS");

        assertTrue(testModel.assignColor("WHITE"));
        assertTrue(testModel.setCurrPlayerBuilders(new Coordinates(0,0), new Coordinates(0,1)));
        testModel.setNextPlayer();
        assertTrue(testModel.assignColor("MAGENTA"));
        assertTrue(testModel.setCurrPlayerBuilders(new Coordinates(4,4), new Coordinates(4,3)));


        testModel.setNextState();

        System.out.println("GAME");

        testModel.setNextPlayer();
        testModel.getCurrPlayer().startTurn();
        //player1
        assertEquals("MOVE", testModel.getCurrStep(testModel.getCurrPlayer()));
        testModel.findPossibleDestinations();
        assertTrue(testModel.possibleDstBuilder1.contains(new Coordinates(1,1)));
        testModel.effectiveMove(new Coordinates(0,0), new Coordinates(1,1));
        assertEquals("BUILD", testModel.getCurrStep(testModel.getCurrPlayer()));
        testModel.effectiveBuild(new Coordinates(1,1), new Coordinates(2,2), false);
        assertEquals("END", testModel.getCurrStep(testModel.getCurrPlayer()));
        testModel.setNextPlayer();


        //player2
        testModel.getCurrPlayer().startTurn();
        assertEquals("BOTH", testModel.getCurrStep(testModel.getCurrPlayer()));
        testModel.setStepChoice("BUILD");
        testModel.findPossibleDestinations();
        assertTrue(testModel.possibleDstBuilder1.contains(new Coordinates(3,3)));
        testModel.effectiveBuild(new Coordinates(4,4), new Coordinates(3,3), false);
        assertEquals("MOVE", testModel.getCurrStep(testModel.getCurrPlayer()));
        testModel.findPossibleDestinations();
        assertTrue(testModel.possibleDstBuilder1.contains(new Coordinates(3,4)));
        testModel.effectiveMove(new Coordinates(4,4), new Coordinates(3,4));
        assertEquals("BUILD", testModel.getCurrStep(testModel.getCurrPlayer()));
        testModel.findPossibleDestinations();
        assertTrue(testModel.possibleDstBuilder1.contains(new Coordinates(3,3)));
        testModel.effectiveBuild(new Coordinates(3,4), new Coordinates(3,3), false);
        assertEquals("END", testModel.getCurrStep(testModel.getCurrPlayer()));
        testModel.setNextPlayer();

        //player1
        testModel.getCurrPlayer().startTurn();
        testModel.findPossibleDestinations();
        assertTrue(testModel.possibleDstBuilder1.contains(new Coordinates(2,2)));
        testModel.effectiveMove(new Coordinates(1,1), new Coordinates(2,2)); //move (2,2)
        assertTrue(testModel.possibleDstBuilder1.contains(new Coordinates(3,3)));
        testModel.effectiveBuild(new Coordinates(2,2), new Coordinates(3,3), false); //build (3,3)
        assertEquals("END", testModel.getCurrStep(testModel.getCurrPlayer()));
        testModel.setNextPlayer();

        //player2
        testModel.getCurrPlayer().startTurn();
        testModel.setStepChoice("MOVE");
        testModel.findPossibleDestinations(); //in questo caso deve avvenire dopo la setStepChoice()

        assertTrue(testModel.possibleDstBuilder1.contains(new Coordinates(2,4)));
        testModel.effectiveMove(new Coordinates(3,4), new Coordinates(2,4));
        assertTrue(testModel.possibleDstBuilder1.contains(new Coordinates(2,3)));
        testModel.effectiveBuild(new Coordinates(2,4), new Coordinates(2,3), false);
        assertEquals("END", testModel.getCurrStep(testModel.getCurrPlayer()));
        testModel.setNextPlayer();

        //player1
        testModel.getCurrPlayer().startTurn();
        testModel.findPossibleDestinations();
        assertTrue(testModel.possibleDstBuilder1.contains(new Coordinates(2,3)));
        testModel.effectiveMove(new Coordinates(2,2), new Coordinates(2,3));
        assertTrue(testModel.possibleDstBuilder1.contains(new Coordinates(2,2)));
        testModel.effectiveBuild(new Coordinates(2,3), new Coordinates(2,2), false);
        assertEquals("END", testModel.getCurrStep(testModel.getCurrPlayer()));
        testModel.setNextPlayer();

        //player2
        testModel.getCurrPlayer().startTurn();
        testModel.setStepChoice("MOVE");
        testModel.findPossibleDestinations();

        assertTrue(testModel.possibleDstBuilder2.contains(new Coordinates(4,2)));
        testModel.effectiveMove(new Coordinates(4,3), new Coordinates(4,2));
        assertTrue(testModel.possibleDstBuilder2.contains(new Coordinates(4,3)));
        testModel.effectiveBuild(new Coordinates(4,2), new Coordinates(4,3), false);
        assertEquals("END", testModel.getCurrStep(testModel.getCurrPlayer()));
        testModel.setNextPlayer();

        //player 1
        testModel.getCurrPlayer().startTurn();
        testModel.findPossibleDestinations();
        assertTrue(testModel.possibleDstBuilder1.contains(new Coordinates(2,2)));
        testModel.effectiveMove(new Coordinates(2,3), new Coordinates(2,2));
        assertTrue(testModel.possibleDstBuilder1.contains(new Coordinates(2,3)));
        testModel.effectiveBuild(new Coordinates(2,2), new Coordinates(2,3), false);
        assertEquals("END", testModel.getCurrStep(testModel.getCurrPlayer()));
        testModel.setNextPlayer();

        //player2
        testModel.getCurrPlayer().startTurn();
        testModel.setStepChoice("MOVE");
        testModel.findPossibleDestinations();
        assertTrue(testModel.possibleDstBuilder2.contains(new Coordinates(4,1)));
        testModel.effectiveMove(new Coordinates(4,2), new Coordinates(4,1));
        assertTrue(testModel.possibleDstBuilder2.contains(new Coordinates(4,2)));
        testModel.effectiveBuild(new Coordinates(4,1), new Coordinates(4,2), false);
        assertEquals("END", testModel.getCurrStep(testModel.getCurrPlayer()));
        testModel.setNextPlayer();

        //player 1
        testModel.getCurrPlayer().startTurn();
        testModel.findPossibleDestinations();
        assertTrue(testModel.possibleDstBuilder1.contains(new Coordinates(3,3)));
        testModel.effectiveMove(new Coordinates(2,2), new Coordinates(3,3));
        assertTrue(testModel.endGame());

    }
}
