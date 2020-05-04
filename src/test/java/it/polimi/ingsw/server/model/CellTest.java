package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.gameMap.Builder;
import it.polimi.ingsw.server.model.gameMap.Cell;
import it.polimi.ingsw.server.model.gameMap.IslandBoard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class CellTest {

    /**
     * Dimension of IslandBoard is 5x5, so the limits of the coordinates are dimension - 1 and dimension - 1
     */
    @Test
    public void raiseExceptionIfWrongCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> new Cell(-1,4));
        assertThrows(IllegalArgumentException.class, () -> new Cell(1,-3));
        assertThrows(IllegalArgumentException.class, () -> new Cell(IslandBoard.dimension,4));
        assertThrows(IllegalArgumentException.class, () -> new Cell(1,IslandBoard.dimension));
    }

    @Test
    public void correctValuesAtConstruction(){
        Cell cell = new Cell(3,4);

        //Initial values
        assertEquals(0, cell.getHeight());
        assertFalse(cell.isDomePresent());
        assertNull(cell.getBuilder());
        assertEquals(3, cell.getI());
        assertEquals(4, cell.getJ());

    }

    @Test
    public void canSetNewOccupantOnlyIfNotBusy() {
        Cell cell = new Cell(3, 4);
        Builder builder1 = new Builder(new Player("thomas"), Builder.BuilderColor.WHITE);
        Builder builder2 = new Builder(new Player("giulio"), Builder.BuilderColor.GREY);

        assertTrue(cell.setOccupant(builder1));
        assertTrue(cell.isOccupied());

        assertEquals(builder1, cell.getBuilder());

        assertFalse(cell.setOccupant(builder2));

        assertEquals(builder1, cell.getBuilder());
    }




    @Test
    public void canBuildOnly3Blocks() {
        Cell cell = new Cell(2, 3);
        int height = cell.getHeight();

        for(int i = 0; i < 3; i++)
        {
            assertTrue(cell.addBlock());
            assertEquals(height + 1,cell.getHeight());
            height = cell.getHeight();
        }

        assertFalse(cell.addBlock());
        assertEquals(height, cell.getHeight());
    }

    @Test
    public void cannotBuildUponDome() {
        Cell cell = new Cell(2,3);
        assertTrue(cell.addDome());
        assertTrue(cell.isDomePresent());

        assertFalse(cell.addBlock());

    }

    @Test
    public void canRemoveOccupantOnlyIfPresent() {
        Cell cell = new Cell(2,3);
        assertFalse(cell.removeOccupant());
        assertFalse(cell.isOccupied());

        cell.setOccupant(new Builder(new Player("thomas"), Builder.BuilderColor.WHITE));
        assertTrue(cell.isOccupied());
        assertTrue(cell.removeOccupant());
        assertFalse(cell.isOccupied());
    }

    @Test
    public void incrementHeightWhenAddingBlock() {
        Cell cell = new Cell(0,0);
        assertEquals(0, cell.getHeight());

        assertTrue(cell.addBlock());
        assertEquals(1, cell.getHeight());
    }


}
