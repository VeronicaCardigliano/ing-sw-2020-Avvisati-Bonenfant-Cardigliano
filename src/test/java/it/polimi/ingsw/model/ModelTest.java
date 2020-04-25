package it.polimi.ingsw.model;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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

    /*
    @Test
    public void runMethodCorrectlyWorking () {
        File inputTextFile = new File ("src/test/java/it/polimi/ingsw/model/modelTestFiles/test1Generic");
        System.out.println(System.getProperty("user.dir"));
        try {
            InputStream inputTextStream = new FileInputStream(inputTextFile);

            playersListTest = testModel.getPlayers();
            //ensure that the match starts from the state SETUP_PLAYERS
            assertEquals(testModel.getState(), Model.State.SETUP_PLAYERS);
            assertEquals(0, playersListTest.size());

            testModel.run (inputTextStream);

            System.out.println("\nTesting if the cards have been properly added...");
            assertEquals(3, playersListTest.size());
            assertEquals("NickSample3", playersListTest.get(0).getNickname());
            assertEquals("NickSample1", playersListTest.get(1).getNickname());
            assertEquals("NickSample2", playersListTest.get(2).getNickname());

            System.out.println("\nTesting if the cards have been correctly added...");
            assertEquals("Atlas", playersListTest.get(0).getGodCard().getName());
            assertEquals("Artemis", playersListTest.get(1).getGodCard().getName());
            assertEquals("Pan", playersListTest.get(2).getGodCard().getName());

            System.out.println("\nTesting if the builder have been correctly added...");
            assertEquals("WHITE", playersListTest.get(0).getBuilders().get(0).getColor().toString());
            assertEquals("WHITE", playersListTest.get(0).getBuilders().get(1).getColor().toString());
            assertEquals("GREY", playersListTest.get(1).getBuilders().get(0).getColor().toString());
            assertEquals("GREY", playersListTest.get(1).getBuilders().get(1).getColor().toString());
            assertEquals("LIGHT_BLUE", playersListTest.get(2).getBuilders().get(0).getColor().toString());
            assertEquals("LIGHT_BLUE", playersListTest.get(2).getBuilders().get(1).getColor().toString());

        } catch (FileNotFoundException e) {
            System.out.println("Test 1 input file error!");
        }
    } */

}
