package it.polimi.ingsw.server.model.godCards;

import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.gameMap.Builder;
import it.polimi.ingsw.server.model.gameMap.Cell;
import it.polimi.ingsw.server.model.gameMap.IslandBoard;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.parser.GodCardParser;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

//TODO: end JavaDoc (also for event)
class GodCardTest {

    static GodCard godCard;
    static GodCard opponentGodCard;
    static GodCard opponentGodCard2;
    static IslandBoard gameMap;
    static int maxCoordinate = IslandBoard.dimension - 1;

    static Player player;
    static Builder builder1;
    static Builder builder2;

    static Player opponent;
    static Builder builder3;
    static Builder builder4;

    static Player opponent2;
    static Builder builder5;
    static Builder builder6;

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

        opponent = new Player("player2");
        builder3 = new Builder(opponent, Builder.BuilderColor.MAGENTA);
        builder4 = new Builder(opponent, Builder.BuilderColor.MAGENTA);
        opponent.setBuilders(builder3, builder4);

        opponent2 = new Player("player3");
        builder5 = new Builder(opponent2, Builder.BuilderColor.LIGHT_BLUE);
        builder6 = new Builder(opponent2, Builder.BuilderColor.LIGHT_BLUE);
        opponent2.setBuilders(builder5, builder6);

        System.out.println("giving player1 a default card...");
        GodCardParser parser = new GodCardParser(Model.jsonPath);
        godCard = parser.createCard(player, "default");
        player.setGodCard(godCard);
        assertEquals(godCard.getPlayer(), player);

        //Demeter is used to check the choice after REQUIRED state
        opponentGodCard = parser.createCard(opponent, "Demeter");
        opponent.setGodCard(opponentGodCard);

        //Prometheus is used to check the choice before REQUIRED state (startTurn)
        opponentGodCard2 = parser.createCard(opponent2, "Prometheus");
        opponent2.setGodCard(opponentGodCard2);

    }

    /*
        position builder1 at (2,2) and builder2 at (4,4)
     */
    @BeforeEach
    public void cleanMap() {

        gameMap = new IslandBoard();
        godCard.setGameMap(gameMap);
        godCard.startTurn();

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

    @Test
    public void startTurnRequiredCondition(){
        System.out.println("\nChecking steps choices, not showing MOVE option at first for Prometheus...");
        opponentGodCard2.setGameMap(gameMap);

        Cell cell3 = gameMap.getCell(maxCoordinate - 4,maxCoordinate - 4);
        Cell cell4 = gameMap.getCell(maxCoordinate -3 ,maxCoordinate - 2);

        cell3.setOccupant(builder5);
        cell4.setOccupant(builder6);

        //should not display MOVE
        gameMap.getCell(1,0).addDome();
        gameMap.getCell(0,1).addDome();
        gameMap.getCell(1,1).addBlock();
        gameMap.getCell(1,1).addBlock();
        //builder 5 can only perform a build
        opponentGodCard2.startTurn();

        assertEquals(1, opponentGodCard2.currStateList.size());
        assertTrue(opponentGodCard2.currStateList.contains("BUILD"));
        assertEquals("BUILD", opponentGodCard2.currState);
        assertFalse(opponentGodCard2.currStateList.contains("MOVE"));
        assertNotEquals("END", opponentGodCard2.currState);

    }

    @Test
    public void requiredConditionLockingFeature(){
        System.out.println("\nChecking steps choices, not showing BUILD option after the first build of Demeter...");
        opponentGodCard.setGameMap(gameMap);
        opponentGodCard.startTurn();

        Cell cell3 = gameMap.getCell(maxCoordinate - 4,maxCoordinate - 4);
        Cell cell4 = gameMap.getCell(maxCoordinate -3 ,maxCoordinate - 2);

        cell3.setOccupant(builder3);
        cell4.setOccupant(builder4);
        //check non locking feature of setNextState

        assertNotEquals("REQUIRED", opponentGodCard.currState);
        assertTrue(opponentGodCard.askMove(0,0, 1, 1));
        assertTrue(opponentGodCard.move(0,0, 1, 1));

        gameMap.getCell(0,1).addDome();
        gameMap.getCell(0,2).addDome();
        gameMap.getCell(1,0).addDome();
        gameMap.getCell(2,0).addDome();
        gameMap.getCell(2,1).addDome();
        gameMap.getCell(3,1).addDome();
        gameMap.getCell(0,0).addBlock();
        gameMap.getCell(0,0).addBlock();
        gameMap.getCell(0,0).addBlock();
        //All cells nearby have dome except the one in 0,0 which has height 3. Only one build will be allowed

        assertEquals(3, gameMap.getCell(0, 0).getHeight());
        assertTrue(opponentGodCard.askBuild(1,1, 0, 0, true));
        assertTrue(opponentGodCard.build(1,1, 0, 0, true));
        assertNotEquals("REQUIRED", opponentGodCard.currState);
        assertTrue(opponentGodCard.currStateList.contains("END"));

    }

    @Test
    public void requiredCondition(){
        System.out.println("\nChecking steps choices...");
        opponentGodCard.setGameMap(gameMap);
        opponentGodCard.startTurn();

        Cell cell3 = gameMap.getCell(maxCoordinate - 4,maxCoordinate - 4);
        Cell cell4 = gameMap.getCell(maxCoordinate -3 ,maxCoordinate - 2);

        cell3.setOccupant(builder3);
        cell4.setOccupant(builder4);

        assertNotEquals("REQUIRED", opponentGodCard.currState);
        assertTrue(opponentGodCard.askMove(0,0, 1, 1));
        assertTrue(opponentGodCard.move(0,0, 1, 1));
        assertTrue(opponentGodCard.askBuild(1,1, 0, 0, false));
        assertTrue(opponentGodCard.build(1,1, 0, 0, false));
        assertEquals("REQUIRED", opponentGodCard.currState);
        assertTrue(opponentGodCard.currStateList.contains("END"));
        assertTrue(opponentGodCard.currStateList.contains("BUILD"));

    }

    @Test
    public void expectedExceptionIfPlayerNull() {
        ArrayList<ArrayList<String>> states = new ArrayList<>();
        Assertions.assertThrows(RuntimeException.class, () -> new GodCard(null, "Zesu", "best god", states));
    }

    @Test
    public void expectedExceptionIfWrongState() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> godCard.forceState("HELLO"));
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
