package it.polimi.ingsw.server.model.godCards;

import it.polimi.ingsw.server.model.gameMap.Builder;
import it.polimi.ingsw.server.model.gameMap.Cell;
import it.polimi.ingsw.server.model.gameMap.IslandBoard;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.parser.GodCardParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class YourBuildGodCardTest {

    static GodCard godCardAtlas;
    static GodCard godCardDemeter;
    static GodCard godCardHephaestus;
    static GodCard godCardZeus;
    static GodCard godCardHestia;
    static Player player;
    static Player player2;
    static IslandBoard gameMap;
    static Builder builder1;
    static Builder builder2;
    static Builder builder3;
    static Builder builder4;
    static int maxCoordinate = IslandBoard.dimension - 1;

    @BeforeAll
    public static void setup() {
        System.out.println("\nYOURBUILD_GODCARD TESTS");

        System.out.println("creating test player 1...");
        player = new Player("player1");
        builder1 = new Builder(player);
        builder2 = new Builder(player);
        player.setBuilders(builder1, builder2);

        player2 = new Player("player2");
        builder3 = new Builder(player2);
        builder4 = new Builder(player2);
        player2.setBuilders(builder3, builder4);

        System.out.println("giving player1 Atlas card...");

        /*godCardAtlas = new YourBuildGodCard(player, "", "", states, 1,
                true, false, false,false );
        godCardDemeter = new YourBuildGodCard(player, "", "", states, 2,
                false, true, false, false );
        godCardHephaestus = new YourBuildGodCard(player2, "", "", states, 2,
                false, false, true, false );
        godCardZeus = new YourBuildGodCard(player2, "", "", states, 1,
                false, false, false, true );*/

        GodCardParser parser = new GodCardParser("src/main/java/it/polimi/ingsw/server/parser/cards.json");

        godCardAtlas = parser.createCard(player, "Atlas");
        godCardDemeter = parser.createCard(player, "Demeter");
        godCardHephaestus = parser.createCard(player2, "Hephaestus");
        godCardZeus = parser.createCard(player2, "Zeus");
        godCardHestia = parser.createCard(player2, "Hestia");
    }

    @BeforeEach
    public void cleanMap() {

        gameMap = new IslandBoard();
        godCardAtlas.setGameMap(gameMap);
        godCardDemeter.setGameMap(gameMap);
        godCardHephaestus.setGameMap(gameMap);
        godCardZeus.setGameMap(gameMap);
        godCardHestia.setGameMap(gameMap);
        godCardAtlas.startTurn();
        godCardHestia.startTurn();

        Cell cell1 = gameMap.getCell(maxCoordinate - 1,maxCoordinate - 3);
        Cell cell2 = gameMap.getCell(maxCoordinate,maxCoordinate);

        System.out.println("repositioning one builder in a sample center position and one at bottom right corner...\n");
        cell1.setOccupant(builder1);
        cell2.setOccupant(builder2);

        Cell cell3 = gameMap.getCell(maxCoordinate - 4,maxCoordinate - 1);
        Cell cell4 = gameMap.getCell(maxCoordinate - 3,maxCoordinate - 4);

        System.out.println("repositioning another pair of builders in samples center positions far from others ...\n");
        cell3.setOccupant(builder3);
        cell4.setOccupant(builder4);
    }

    /**
     * Verify that askBuild gives a positive result if Atlas wants to build a dome, at any height
     */
    @Test
    public void atlasTest() {
        System.out.println("\nTesting Atlas behaviour ");
        int i_src = builder1.getCell().getI();
        int j_src = builder1.getCell().getJ();
        assertTrue(godCardAtlas.askBuild(i_src, j_src, i_src +1, j_src +1, true));
        gameMap.getCell(i_src+1,j_src+1).addBlock();
        assertTrue(godCardAtlas.askBuild(i_src, j_src, i_src +1, j_src +1, true));
        gameMap.getCell(i_src+1,j_src+1).addBlock();
        assertTrue(godCardAtlas.askBuild(i_src, j_src, i_src +1, j_src +1, true));
        gameMap.getCell(i_src+1,j_src+1).addBlock();
        assertTrue(godCardAtlas.askBuild(i_src, j_src, i_src +1, j_src +1, true));
        assertFalse(godCardAtlas.askBuild(i_src, j_src, i_src +1, j_src +1, false));
    }

    /**
     * Verify that askBuild gives a positive result if Zeus wants to build under itself
     */
    @Test
    public void zeusTest() {
        System.out.println("\nTesting Zeus behaviour ");
        int i_src = builder3.getCell().getI();
        int j_src = builder3.getCell().getJ();
        assertTrue(godCardZeus.askBuild(i_src, j_src, i_src +1, j_src +1, false));
        gameMap.getCell(i_src+1,j_src+1).addBlock();
        gameMap.getCell(i_src+1,j_src+1).addBlock();
        gameMap.getCell(i_src+1,j_src+1).addBlock();
        assertFalse(godCardZeus.askBuild(i_src, j_src, i_src +1, j_src +1, false));
        assertTrue(godCardZeus.askBuild(i_src, j_src, i_src +1, j_src +1, true));

        //Build under itself (Zeus power, not dome)
        assertTrue(godCardZeus.askBuild(i_src, j_src, i_src, j_src, false));
        assertFalse(godCardZeus.askBuild(i_src, j_src, i_src, j_src, true));

        gameMap.getCell(i_src,j_src).addBlock();
        gameMap.getCell(i_src,j_src).addBlock();
        //gameMap.getCell(i_src,j_src).addBlock();
        assertTrue(godCardZeus.build(i_src, j_src, i_src, j_src, false));

        //Zeus should not win build under himself
        assertFalse(godCardZeus.winCondition());

        //Cannot build a dome under himself
        assertFalse(godCardZeus.askBuild(i_src, j_src, i_src, j_src, true));
    }

    /**
     * Ensures that askBuild gives a negative result if Demeter wants to build two times in the same space
     */
    @Test
    public void demeterTest() {
        System.out.println("\nTesting Demeter behaviour ");

        int i_src = builder1.getCell().getI();
        int j_src = builder1.getCell().getJ();

        gameMap.getCell(i_src+1,j_src+1).addBlock();
        godCardDemeter.step = 1;

        godCardDemeter.build(i_src, j_src, i_src +1, j_src+1);


        godCardDemeter.step = 2; // it means it's at the second build

        assertFalse(godCardDemeter.askBuild(i_src, j_src, i_src +1, j_src +1, false));

    }

    /**
     * Ensures that askBuild gives a negative result if Hephaestus wants to build two times in different spaces or
     * if he wants to build a dome during the second build step
     */
    @Test
    public void hephaestusTest() {
        System.out.println("\nTesting Hephaestus behaviour ");

        int i_src = builder3.getCell().getI();
        int j_src = builder3.getCell().getJ();

        godCardHephaestus.step = 1;

        godCardHephaestus.build(i_src, j_src, i_src +1, j_src+1);

        godCardHephaestus.step = 2; // it means it's at the second build
        /*
        assertEquals(i_src+1, godCardHephaestus.getFirstBuildDst().getI());
        assertEquals(j_src+1, godCardHephaestus.getFirstBuildDst().getJ());*/

        gameMap.getCell(i_src+1,j_src).addBlock();
        gameMap.getCell(i_src+1,j_src).addBlock();
        gameMap.getCell(i_src+1,j_src).addBlock();

        assertFalse(godCardHephaestus.askBuild(i_src, j_src, i_src, j_src +1, false));
        assertFalse(godCardHephaestus.askBuild(i_src, j_src, i_src +1, j_src, true));
        assertTrue(godCardHephaestus.askBuild(i_src, j_src, i_src +1, j_src+1, false));

        gameMap.getCell(i_src+1,j_src+1).addBlock();
        gameMap.getCell(i_src+1,j_src+1).addBlock();

        assertFalse(godCardHephaestus.askBuild(i_src, j_src, i_src +1, j_src+1, false));
        assertFalse(godCardHephaestus.askBuild(i_src, j_src, i_src +1, j_src+1, true));
    }

    @Test
    public void hestiaTest(){

        int i_src = builder3.getCell().getI();
        int j_src = builder3.getCell().getJ();

        assertTrue(godCardHestia.askMove(i_src,j_src,i_src+1, j_src+1));
        godCardHestia.move(i_src,j_src,i_src+1, j_src+1);

        assertTrue(godCardHestia.askBuild(i_src+1, j_src+1, i_src,j_src,false));
        godCardHestia.build(i_src+1, j_src+1, i_src,j_src);

        assertFalse(godCardHestia.askBuild(i_src+1, j_src+1, i_src, j_src+1,false));

        assertTrue(godCardHestia.askBuild(i_src+1, j_src+1, i_src+1, j_src,false));


    }

}