package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.gameMap.Coordinates;
import org.junit.jupiter.api.*;

import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
    public void checkDateTest() {
        assertFalse(Model.checkDate("test"));
        assertFalse(Model.checkDate("1.1.1"));
        assertTrue(Model.checkDate("1980.02.10"));
        assertFalse(Model.checkDate("3000.01.01"));
        assertFalse(Model.checkDate("1980.02.30"));

        assertFalse(testModel.addPlayer("thomas", "3000.01.01"));
        assertFalse(testModel.addPlayer("thomas1", "1980.02.30"));
        //Assertions.assertThrows(ParseException.class, ()-> testModel.addPlayer("veronica", "2010 02 03"));
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
    public void placeBuildersTest() {
        testModel.setNumberOfPlayers(2);
        testModel.addPlayer("Carlo", "1998.04.20");
        testModel.addPlayer("Luigi", "1999.02.01");

        testModel.setNextPlayer();
        testModel.assignColor("MAGENTA");
        testModel.setNextPlayer();
        testModel.assignColor("WHITE");
        testModel.setNextPlayer();

        assertFalse(testModel.setCurrPlayerBuilders(new Coordinates(1,1), new Coordinates(1,1)));
        assertTrue(testModel.setCurrPlayerBuilders(new Coordinates(1,1), new Coordinates(1,2)));

        testModel.setNextPlayer();
        assertFalse(testModel.setCurrPlayerBuilders(new Coordinates(1,1), new Coordinates(2,2)));
        assertTrue(testModel.setCurrPlayerBuilders(new Coordinates(2,1), new Coordinates(2,2)));

    }

    @Test
    public void instantLoseTest() {

        //game with 3 builders where one of them is completely blocked at start

        testModel.setNumberOfPlayers(3);
        testModel.setNextState();

        testModel.addPlayer("p3", "1980.01.01");
        testModel.addPlayer("p2", "1990.01.01");
        testModel.addPlayer("p1", "2000.01.01");

        testModel.setNextPlayer();
        testModel.setNextState();

        testModel.assignCard("Atlas");

        testModel.setNextPlayer();
        testModel.assignCard("Zeus");

        testModel.setNextPlayer();
        testModel.assignCard("Athena");

        testModel.setNextPlayer();
        testModel.setNextState();

        testModel.assignColor("WHITE");
        testModel.setNextPlayer();

        testModel.assignColor("MAGENTA");
        testModel.setNextPlayer();

        testModel.assignColor("LIGHT_BLUE");
        testModel.setNextPlayer();

        testModel.setNextState();

        testModel.setCurrPlayerBuilders(new Coordinates(0,0), new Coordinates(0,1));
        testModel.setNextPlayer();

        testModel.setCurrPlayerBuilders(new Coordinates(1,0), new Coordinates(1,1));
        testModel.setNextPlayer();

        testModel.setCurrPlayerBuilders(new Coordinates(0,2), new Coordinates(1,2));
        testModel.setNextPlayer();

        testModel.setNextState();

        testModel.startTurn();

        testModel.findPossibleDestinations();
        assertFalse(testModel.hasNotLostDuringMove());

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

        System.out.println("SETUP_COLOR");

        assertTrue(testModel.assignColor("WHITE"));
        testModel.setNextPlayer();
        assertTrue(testModel.assignColor("MAGENTA"));
        testModel.setNextPlayer();
        testModel.setNextState();

        System.out.println("SETUP_BUILDERS");

        assertTrue(testModel.setCurrPlayerBuilders(new Coordinates(0,0), new Coordinates(0,1)));
        testModel.setNextPlayer();
        assertTrue(testModel.setCurrPlayerBuilders(new Coordinates(4,4), new Coordinates(4,3)));
        testModel.setNextPlayer();
        testModel.setNextState();

        System.out.println("GAME");

        testModel.startTurn();
        //player1
        assertEquals("MOVE", testModel.getCurrStep());
        testModel.findPossibleDestinations();
        assertTrue(testModel.possibleDstBuilder1.contains(new Coordinates(1,1)));
        testModel.effectiveMove(new Coordinates(0,0), new Coordinates(1,1));
        assertEquals("BUILD", testModel.getCurrStep());
        testModel.effectiveBuild(new Coordinates(1,1), new Coordinates(2,2), false);
        assertEquals("END", testModel.getCurrStep());
        testModel.setNextPlayer();


        //player2
        testModel.startTurn();
        assertEquals("REQUIRED", testModel.getCurrStep());
        testModel.setStepChoice("BUILD");
        testModel.findPossibleDestinations();
        assertTrue(testModel.possibleDstBuilder1.contains(new Coordinates(3,3)));
        testModel.effectiveBuild(new Coordinates(4,4), new Coordinates(3,3), false);
        assertEquals("MOVE", testModel.getCurrStep());
        testModel.findPossibleDestinations();
        assertTrue(testModel.possibleDstBuilder1.contains(new Coordinates(3,4)));
        assertFalse(testModel.effectiveMove(new Coordinates(4,3), new Coordinates(4,2)));
        testModel.effectiveMove(new Coordinates(4,4), new Coordinates(3,4));
        assertEquals("BUILD", testModel.getCurrStep());
        testModel.findPossibleDestinations();
        assertTrue(testModel.possibleDstBuilder1.contains(new Coordinates(3,3)));
        testModel.effectiveBuild(new Coordinates(3,4), new Coordinates(3,3), false);
        assertEquals("END", testModel.getCurrStep());
        testModel.setNextPlayer();

        //player1
        testModel.startTurn();
        testModel.findPossibleDestinations();
        assertTrue(testModel.possibleDstBuilder1.contains(new Coordinates(2,2)));
        testModel.effectiveMove(new Coordinates(1,1), new Coordinates(2,2)); //move (2,2)
        testModel.findPossibleDestinations();
        assertTrue(testModel.possibleDstBuilder1.contains(new Coordinates(3,3)));
        testModel.effectiveBuild(new Coordinates(2,2), new Coordinates(3,3), false); //build (3,3)
        assertEquals("END", testModel.getCurrStep());
        testModel.setNextPlayer();

        //player2
        testModel.startTurn();
        assertFalse(testModel.setStepChoice("HELLO"));
        testModel.setStepChoice("MOVE");
        testModel.findPossibleDestinations();

        assertTrue(testModel.possibleDstBuilder1.contains(new Coordinates(2,4)));
        testModel.effectiveMove(new Coordinates(3,4), new Coordinates(2,4));
        testModel.findPossibleDestinations();
        assertTrue(testModel.possibleDstBuilder1.contains(new Coordinates(2,3)));
        testModel.effectiveBuild(new Coordinates(2,4), new Coordinates(2,3), false);
        assertEquals("END", testModel.getCurrStep());
        testModel.setNextPlayer();

        //player1
        testModel.startTurn();
        testModel.findPossibleDestinations();
        assertTrue(testModel.possibleDstBuilder1.contains(new Coordinates(2,3)));
        testModel.effectiveMove(new Coordinates(2,2), new Coordinates(2,3));
        testModel.findPossibleDestinations();
        assertTrue(testModel.possibleDstBuilder1.contains(new Coordinates(2,2)));
        testModel.effectiveBuild(new Coordinates(2,3), new Coordinates(2,2), false);
        assertEquals("END", testModel.getCurrStep());
        testModel.setNextPlayer();

        //player2
        testModel.startTurn();
        testModel.setStepChoice("MOVE");
        testModel.findPossibleDestinations();

        assertTrue(testModel.possibleDstBuilder2.contains(new Coordinates(4,2)));
        testModel.effectiveMove(new Coordinates(4,3), new Coordinates(4,2));
        testModel.findPossibleDestinations();
        assertTrue(testModel.possibleDstBuilder2.contains(new Coordinates(4,3)));
        testModel.effectiveBuild(new Coordinates(4,2), new Coordinates(4,3), false);
        assertEquals("END", testModel.getCurrStep());
        testModel.setNextPlayer();

        //player 1
        testModel.startTurn();
        testModel.findPossibleDestinations();
        assertTrue(testModel.possibleDstBuilder1.contains(new Coordinates(2,2)));
        testModel.effectiveMove(new Coordinates(2,3), new Coordinates(2,2));
        testModel.findPossibleDestinations();
        assertTrue(testModel.possibleDstBuilder1.contains(new Coordinates(2,3)));
        testModel.effectiveBuild(new Coordinates(2,2), new Coordinates(2,3), false);
        assertEquals("END", testModel.getCurrStep());
        testModel.setNextPlayer();

        //player2
        testModel.startTurn();
        testModel.setStepChoice("MOVE");
        testModel.findPossibleDestinations();
        assertTrue(testModel.possibleDstBuilder2.contains(new Coordinates(4,1)));
        testModel.effectiveMove(new Coordinates(4,2), new Coordinates(4,1));
        testModel.findPossibleDestinations();
        assertTrue(testModel.possibleDstBuilder2.contains(new Coordinates(4,2)));
        testModel.effectiveBuild(new Coordinates(4,1), new Coordinates(4,2), false);
        assertEquals("END", testModel.getCurrStep());
        testModel.setNextPlayer();

        //player 1
        testModel.startTurn();
        testModel.findPossibleDestinations();
        assertTrue(testModel.possibleDstBuilder1.contains(new Coordinates(3,3)));
        testModel.effectiveMove(new Coordinates(2,2), new Coordinates(3,3));
        assertTrue(testModel.endGame());

    }


    @Test
    public void apolloVSLimus() {

        Assertions.assertThrows(RuntimeException.class, () -> testModel.setNextPlayer());

        System.out.println("SETUP_NUMOFPLAYERS");
        assertEquals(testModel.getCurrState().toString(),"SETUP_NUMOFPLAYERS");

        assertFalse(testModel.setNumberOfPlayers(100));
        assertTrue(testModel.setNumberOfPlayers(2));
        testModel.setNextState();

        System.out.println("SETUP_PLAYERS");
        assertEquals(testModel.getCurrState().toString(),"SETUP_PLAYERS");

        assertTrue(testModel.addPlayer("sampleNick1", "2000.02.08"));
        assertTrue(testModel.addPlayer("sampleNick2", "1999.02.08"));
        testModel.setNextPlayer();
        testModel.setNextState();

        System.out.println("SETUP_CARDS");
        assertEquals(testModel.getCurrState().toString(),"SETUP_CARDS");

        testModel.setChallenger("sampleNick1");
        assertEquals(testModel.getChallenger(), "sampleNick1");
        Set<String> matchGodCards = new HashSet<>();
        matchGodCards.add("Apollo");
        assertFalse(testModel.setMatchCards(matchGodCards));
        matchGodCards.add("Hello");
        assertFalse(testModel.setMatchCards(matchGodCards));
        matchGodCards.remove("Hello");
        matchGodCards.add("Limus");
        assertTrue(testModel.setMatchCards(matchGodCards));

        assertEquals(testModel.getCurrPlayer(), "sampleNick1");
        assertTrue(testModel.assignCard("Apollo"));
        testModel.setNextPlayer();
        assertTrue(testModel.assignCard("Limus"));
        testModel.setNextPlayer();
        assertFalse(testModel.setStartPlayer("sampleNick1","Minnie"));
        assertTrue(testModel.setStartPlayer("sampleNick1","sampleNick1"));
        testModel.setStartPlayerNickname("sampleNick1");
        assertEquals(testModel.getStartPlayerNickname(), "sampleNick1");
        testModel.setNextState();

        System.out.println("SETUP_COLOR");
        assertEquals(testModel.getCurrState().toString(),"SETUP_COLOR");

        assertTrue(testModel.assignColor("LIGHT_BLUE"));
        testModel.setNextPlayer();
        assertTrue(testModel.assignColor("MAGENTA"));
        testModel.setNextPlayer();
        testModel.setNextState();

        System.out.println("SETUP_BUILDERS");
        assertEquals(testModel.getCurrState().toString(),"SETUP_BUILDERS");

        assertTrue(testModel.setCurrPlayerBuilders(new Coordinates(1,1), new Coordinates(2,2)));
        testModel.setNextPlayer();
        assertTrue(testModel.setCurrPlayerBuilders(new Coordinates(0,0), new Coordinates(0,3)));
        testModel.setNextPlayer();
        testModel.setNextState();

        System.out.println("GAME");
        assertEquals(testModel.getCurrState().toString(),"GAME");

        testModel.startTurn();
        assertEquals(testModel.getCurrPlayer(), "sampleNick1");
        //player1
        assertEquals("MOVE", testModel.getCurrStep());
        testModel.findPossibleDestinations();
        assertTrue(testModel.possibleDstBuilder1.contains(new Coordinates(0,0)));
        testModel.effectiveMove(new Coordinates(1,1), new Coordinates(0,0));
        assertEquals("BUILD", testModel.getCurrStep());
        testModel.findPossibleDestinations();
        assertFalse(testModel.effectiveBuild(new Coordinates(1,1), new Coordinates(0,1), false));
        //Limus effect
        assertFalse(testModel.effectiveBuild(new Coordinates(0,0), new Coordinates(0,1), false));

        assertFalse(testModel.hasNotLostDuringBuild());
        testModel.deletePlayer("sampleNick1");
        assertTrue(testModel.endGame());
    }
}
