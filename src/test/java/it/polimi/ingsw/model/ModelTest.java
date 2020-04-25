package it.polimi.ingsw.model;

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

        assertTrue(testModel.addPlayer("Paolo", "2000.07.01"));
        assertTrue(testModel.addPlayer("Anna","2006.10.11"));
        assertTrue(testModel.addPlayer("Marco","2000.06.12"));

        playersListTest = testModel.getPlayers();
        System.out.println("\nTesting if Players are in order of birthday...");
        assertEquals("Anna", playersListTest.get(0).getNickname());
        assertEquals("Paolo", playersListTest.get(1).getNickname());
        assertEquals("Marco", playersListTest.get(2).getNickname());
    }

    @Test
    public void correctlyDeleted () {

        assertTrue(testModel.addPlayer("Paolo", "2000.07.01"));
        assertTrue(testModel.addPlayer("Anna","2006.10.11"));
        playersListTest = testModel.getPlayers();
        assertEquals("Anna", playersListTest.get(0).getNickname());

        testModel.deletePlayer (playersListTest.get(0));

        System.out.println("\nTesting if player has been correctly deleted...");
        playersListTest = testModel.getPlayers();
        assertEquals("Paolo", playersListTest.get(0).getNickname());

    }

    @Test
    public void expectedExceptionIfPlayerToDeleteNotFound () {
        testModel.addPlayer("Paolo", "2000.07.01");
        Player samplePlayer = new Player("Beatrice");
        Assertions.assertThrows(IllegalArgumentException.class, () -> testModel.deletePlayer(samplePlayer));
    }

    @Test
    public void cardCorrectlyAssigned () {
        testModel.addPlayer("Carlo", "1998.04.20");
        assertTrue(testModel.addPlayer("Paolo", "2000.07.01"));
        assertTrue(testModel.addPlayer("Anna","2006.10.11"));
        playersListTest = testModel.getPlayers();

        assertTrue(testModel.assignCard(playersListTest.get(0), "Zeus"));
        System.out.println("\nExpected error for GodCard name already used...");
        assertFalse(testModel.assignCard(playersListTest.get(1), "Zeus"));
        assertTrue(testModel.assignCard(playersListTest.get(1), "Atlas"));
        System.out.println("\nExpected error for non-existent GodCard name...");
        assertFalse(testModel.assignCard(playersListTest.get(2), "Ciao"));
        assertTrue(testModel.assignCard(playersListTest.get(2), "Limus"));
    }

    @Test
    public void colorsCorrectlyAssigned () {
        testModel.addPlayer("Carlo", "1998.04.20");
        testModel.addPlayer("Luigi", "1999.02.01");
        playersListTest = testModel.getPlayers();

        assertTrue(testModel.assignColor(playersListTest.get(0), "GREY"));
        System.out.println("\nExpected error for color already used...");
        assertFalse(testModel.assignColor(playersListTest.get(1), ("GREY")));
        System.out.println("\nExpected error for non-existent color name...");
        assertFalse(testModel.assignColor(playersListTest.get(1), ("YELLOW")));
        assertTrue(testModel.assignColor(playersListTest.get(1), ("LIGHT_BLUE")));
    }

    @Test
    public void correctlyDeletedIfLoser() {
        testModel.addPlayer("Luigi", "1999.02.01");
        playersListTest = testModel.getPlayers();
        ArrayList<Cell> possibleDstBuilder1 = new ArrayList<>();
        ArrayList<Cell> possibleDstBuilder2 = new ArrayList<>();
        ArrayList<Cell> possibleDstBuilder1forDome = new ArrayList<>();
        ArrayList<Cell> possibleDstBuilder2forDome = new ArrayList<>();

        Assertions.assertThrows(IllegalArgumentException.class, () -> testModel.hasLostAfterMove(playersListTest.get(0), null, null));

        testModel.hasLostAfterMove(playersListTest.get(0), possibleDstBuilder1, possibleDstBuilder2);
        playersListTest = testModel.getPlayers();

        System.out.println("\nAssuring that players are correctly deleted after they've lost..");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> playersListTest.get(0));

        testModel.addPlayer("Carlo", "1998.04.20");
        playersListTest = testModel.getPlayers();
        testModel.hasLostAfterBuild(playersListTest.get(0), possibleDstBuilder1, possibleDstBuilder2, possibleDstBuilder1forDome, possibleDstBuilder2forDome);
        playersListTest = testModel.getPlayers();
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> playersListTest.get(0));

    }

}
