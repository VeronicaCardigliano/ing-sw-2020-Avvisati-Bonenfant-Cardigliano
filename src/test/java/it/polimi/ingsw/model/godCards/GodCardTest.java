package it.polimi.ingsw.model.godCards;

import it.polimi.ingsw.model.Builder;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.IslandBoard;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.godCards.GodCard;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

//TODO: end JavaDoc (also for event)
class GodCardTest {

    static GodCard godCard;
    static IslandBoard gameMap;
    static Player player;
    static Builder builder1;
    static Builder builder2;
    static int maxCoordinate = IslandBoard.dimension - 1;

    /**
     * create a map with 2 builders from one player
     */
    @BeforeAll
    /*
        create 2 builders for player 1.

     */
    public static void setup() {
        System.out.println("\nGODCARD TESTS");

        System.out.println("creating test player 1...");
        player = new Player("player1");
        builder1 = new Builder(player, Builder.BuilderColor.WHITE);
        builder2 = new Builder(player, Builder.BuilderColor.WHITE);
        player.setBuilders(builder1, builder2);

        System.out.println("giving player1 a default card...");
        godCard = new GodCard(player, new JSONObject("{}"));
        player.setGodCard(godCard);

    }

    /*
        position builder1 at (2,2) and builder2 at (4,4)
     */
    @BeforeEach
    public void cleanMap() {

        godCard.startTurn();
        gameMap = new IslandBoard();
        godCard.setGameMap(gameMap);

        Cell cell1 = gameMap.getCell(maxCoordinate / 2,maxCoordinate / 2);
        Cell cell2 = gameMap.getCell(maxCoordinate,maxCoordinate);

        System.out.println("repositioning one builder at the center and one at bottom right corner...\n");
        cell1.setOccupant(builder1);
        cell2.setOccupant(builder2);
    }


    @Test
    public void cantSetGameMapNull() {
        System.out.println("# Checking we can't set a Null game map...\n");
        assertThrows(IllegalArgumentException.class, () -> godCard.setGameMap(null));
    }

    //The test below seems not required because askMove and move are designed to work only with valid coordinates
    /*@Test
    public void cantMoveOutOfMap() {
        System.out.println("Testing boundaries...");
        assertFalse(godCard.askMove(maxCoordinate, maxCoordinate, maxCoordinate + 1, maxCoordinate + 1));

        //positioning builder2 on (0,0) to test negative values
        builder2.getCell().removeOccupant();
        gameMap.getCell(0,0).setOccupant(builder2);

        assertFalse(godCard.askMove(0,0, -1, -1));


    }*/

    @Test
    public void canMoveOnlyOwnBuilder() {
        System.out.println("# Testing that player doesn't have control upon enemy's builders...");

        System.out.println("Creating enemy player");
        Player player2 = new Player("player2");

        System.out.println("Creating builders for player 2...");
        Builder enemyBuilder1 = new Builder(player2, Builder.BuilderColor.WHITE);
        Builder enemyBuilder2 = new Builder(player2, Builder.BuilderColor.WHITE);
        player2.setBuilders(enemyBuilder1, enemyBuilder2);

        System.out.println("Positioning first enemy builder 1 on (0,0)...");
        gameMap.getCell(0,0).setOccupant(enemyBuilder1);

        System.out.println("Player1 trying to move enemy builder 1...");
        assertFalse(player.getGodCard().askMove(0,0, 1,1));

        //remove enemy player's builder from game map
        player2.getBuilders().get(0).getCell().removeOccupant(); //note that builder still thinks he is occupying a cell!

        System.out.println("Player1 tring to move an empy cell...");
        assertFalse(godCard.askMove(0,0, 1,1));


    }


    @Test
    public void climbUpAndDown2Levels() {
        System.out.println("# Testing climbing limits with height 2...");

        System.out.println("Changing height level of cell adiacent to builder 1...");
        Cell adiacentCell = gameMap.getCell(maxCoordinate / 2 + 1, maxCoordinate / 2);
        adiacentCell.addBlock();
        adiacentCell.addBlock();

        System.out.println("Builder1 trying to climb 2 levels...");
        assertEquals(player, gameMap.getCell(maxCoordinate / 2, maxCoordinate / 2).getBuilder().getPlayer());
        assertFalse(godCard.askMove(maxCoordinate / 2, maxCoordinate / 2, adiacentCell.getI(), adiacentCell.getJ()));

        System.out.println("Changing height level of cell occupied by builder 2...");
        Cell builder2Cell = gameMap.getCell(maxCoordinate, maxCoordinate);
        builder2Cell.addBlock();
        builder2Cell.addBlock();

        System.out.println("Builder2 trying to climb down 2 levels...");
        assertEquals(player, gameMap.getCell(maxCoordinate, maxCoordinate).getBuilder().getPlayer());
        assertEquals(2, builder2.getCell().getHeight());
        assertTrue(godCard.askMove(maxCoordinate, maxCoordinate, maxCoordinate - 1, maxCoordinate));


    }

    @Test
    public void climbUpAndDown1Level() {
        System.out.println("# Testing climbing limits with height 1...");

        System.out.println("Changing height level of cell adiacent to builder 1...");
        Cell adiacentCell = gameMap.getCell(maxCoordinate / 2 + 1, maxCoordinate / 2);
        adiacentCell.addBlock();

        System.out.println("Builder1 trying to climb  level...");
        assertEquals(player, gameMap.getCell(maxCoordinate / 2, maxCoordinate / 2).getBuilder().getPlayer());
        assertTrue(godCard.askMove(maxCoordinate / 2, maxCoordinate / 2, adiacentCell.getI(), adiacentCell.getJ()));

        System.out.println("Changing height level of cell occupied by builder 2...");
        Cell builder2Cell = gameMap.getCell(maxCoordinate, maxCoordinate);
        builder2Cell.addBlock();

        System.out.println("Builder2 trying to climb down 1 level...");
        assertEquals(player, gameMap.getCell(maxCoordinate, maxCoordinate).getBuilder().getPlayer());
        assertEquals(1, builder2.getCell().getHeight());
        assertTrue(godCard.askMove(maxCoordinate, maxCoordinate, maxCoordinate - 1, maxCoordinate));
    }

    @Test
    public void canBuildOnlyOnUnoccupiedCells() {
        System.out.println("# Checking builder can build only on unoccupied cells...");

        System.out.println("Player1 trying to build on empty cell...");
        //cell (2,3) should be empty
        assertFalse(gameMap.getCell(2,3).isOccupied());
        assertTrue(godCard.askBuild(2,2, 2,3, false));

        System.out.println("Player1 trying to build on occupied cell...");
        gameMap.getCell(2,3).setOccupant(new Builder(new Player("player2"), Builder.BuilderColor.WHITE)); //note that player 2 doesn't know he has this builder
        assertTrue(gameMap.getCell(2,3).isOccupied());
        assertFalse(godCard.askBuild(2,2,2,3, false));


    }

    @Test
    public void canBuildOnlyOnDomeFreeCells() {
        System.out.println("# Checking builder can build only on dome-free cells...");

        System.out.println("Player1 trying to build on dome-free cell...");
        //cell (2,3) should be dome free
        assertFalse(gameMap.getCell(2,3).isDomePresent());
        assertTrue(godCard.askBuild(2,2, 2,3, false));

        System.out.println("Player1 trying to build on non dome-free cell...");
        gameMap.getCell(2,3).addDome();
        assertTrue(gameMap.getCell(2,3).isDomePresent());
        assertFalse(godCard.askBuild(2,2,2,3, false));
    }

    @Test
    public void canBuildDomeOnlyOnHeight3Cells() {
        System.out.println("# Checking builder can build a dome only on fully built cells...");

        System.out.println("Player1 trying to build dome on non fully built cell...");
        //cell (2,3) should have height 0
        assertEquals(0, gameMap.getCell(2,3).getHeight());
        assertFalse(godCard.askBuild(2,2, 2,3, true));

        System.out.println("Player1 trying to build a dome on fully built cell...");
        gameMap.getCell(2,3).addBlock();
        gameMap.getCell(2,3).addBlock();
        gameMap.getCell(2,3).addBlock();
        assertEquals(3, gameMap.getCell(2,3).getHeight());
        assertTrue(godCard.askBuild(2,2,2,3, true));
    }

    @Test
    public void mapChangesAfterMove() {
        godCard.move(2,2,2,3);
        assertFalse(gameMap.getCell(2,2).isOccupied());
        assertTrue(gameMap.getCell(2,3).isOccupied());
        assertEquals(builder1, gameMap.getCell(2,3).getBuilder());
    }

    @Test
    public void mapChangesAfterBuild() {
        godCard.build(2,2,2,3,false);
        assertEquals(1, gameMap.getCell(2,3).getHeight());

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

        assertTrue(godCard.move(2,2,2,3));
        assertTrue(godCard.winCondition());

        //set cell adiacent to builder2 to height1
        gameMap.getCell(4,3).addBlock();
        assertTrue(godCard.move(4,4,4,3));
        assertFalse(godCard.winCondition());


    }

    @AfterEach
    @Test
    public void playerStillHasTwoBuilders() {
        System.out.println("\nChecking player still has 2 builders...");
        assertEquals(2, player.getBuilders().size());
        for(Builder builder : player.getBuilders())
            assertEquals(player, builder.getPlayer());
    }


    @AfterEach
    @Test
    public void everyBuilderOccupyACell() {
        System.out.println("Checking every builder occupies a Cell...\n");
        for(Builder builder : player.getBuilders()) {
            assertNotNull(builder.getCell());
            assertEquals(builder, builder.getCell().getBuilder());
        }
    }


}
