/*package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.model.gameMap.Coordinates;
import it.polimi.ingsw.server.view.ViewManager;
import it.polimi.ingsw.server.view.VirtualView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ControllerTest {
    static Model testModel;
    static ViewManager testViewManager;
    static Controller testController = new Controller(testModel, testViewManager);


    Model setters working

    BeforeAll
    public static void setup(){
        Socket socket1 = new Socket();
        Socket socket2 = new Socket();
        socket1.
        VirtualView player1view = new VirtualView(socket1, testController);
        VirtualView player2view = new VirtualView(socket2, testController);
        testViewManager.add(player1view);
        testViewManager.add(player2view);
        //How can i set nicknames?
    }

    @Test
    public static void gameTestAthenaVsPrometheus(){
        System.out.println("SETUP_NUMOFPLAYERS");

        testViewManager.
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
}*/