package it.polimi.ingsw.server.model.godCards;
import it.polimi.ingsw.server.model.gameMap.Builder;
import it.polimi.ingsw.server.model.gameMap.IslandBoard;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.parser.GodCardParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.json.*;

public class  YourMoveGodCardTest {
    //Test that number of moves works correctly (example 3 move from file)
    //Test that push power pushes enemy with pushForce value
    //Test not getting back to same Cell when secondMoveDiffDst=true
    //GENERAL Test move won't get out of board
    //SPECIFIC Test move won't overwrite builder if pushPower is off
    //SPECIFIC Test secondMoveDiffDst



    IslandBoard testBoard = new IslandBoard();

    Player mePlayer = new Player("Me");
    Player enemyPlayer = new Player("Enemy");

    Builder myBuilder1 = new Builder(mePlayer);
    Builder enemyBuilder1 = new Builder(enemyPlayer);
    Builder enemyBuilder2 = new Builder(enemyPlayer);
    Builder enemyBuilder3 = new Builder(enemyPlayer);


    GodCardParser parser;


    @BeforeEach
    public void setup(){

        testBoard = new IslandBoard();

        testBoard.getCell(1,1).setOccupant(myBuilder1);

        testBoard.getCell(0,0).setOccupant(enemyBuilder1);
        testBoard.getCell(2,2).setOccupant(enemyBuilder2);

        testBoard.getCell(2, 1).addBlock();
        testBoard.getCell(2, 1).setOccupant(enemyBuilder3);

        testBoard.getCell(3, 3).addDome();


        parser = new GodCardParser("src/main/java/it/polimi/ingsw/server/parser/cards.json");

    }

    @Test
    public void domeCheck() {

        GodCard god = parser.createCard(mePlayer, "Minotaur");

        god.setGameMap(testBoard);
        god.startTurn();

        assertFalse(god.move(1, 1, 2, 2)); //in 3,3 there is a dome

    }

    @Test
    public void pushAndMove(){

        GodCard god = parser.createCard(mePlayer, "Minotaur");

        god.setGameMap(testBoard);
        god.startTurn();

        assertTrue(god.move(1,1,2,1)); //this pushes and height difference is ok

        // Cell(3,1) is occupied by enemyBuilder3 and Cell(2,1) is occupied by myBuilder1
        assertFalse(testBoard.getCell(1,1).isOccupied());
        assertSame(testBoard.getCell(3,1).getBuilder(), enemyBuilder3);
        assertSame(testBoard.getCell(2,1).getBuilder(), myBuilder1);

    }

    @Test
    public void pushAtCorner(){
        GodCard god = parser.createCard(mePlayer, "Minotaur");

        god.setGameMap(testBoard);
        god.startTurn();

        assertFalse(god.move(1,1,0,0));
        //nothing has changed
        assertSame(testBoard.getCell(0,0).getBuilder(), enemyBuilder1);
        assertSame(testBoard.getCell(1,1).getBuilder(), myBuilder1);
    }


    @Test
    public void secondMoveDiffDist(){

        GodCard god = parser.createCard(mePlayer, "Artemis");

        god.setGameMap(testBoard);
        god.startTurn();

        assertTrue(god.move(1,1,1,0));
        assertFalse(god.move(1,0,1,1));
        assertTrue(god.move(1,0,2,0));

    }

}
