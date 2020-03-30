package it.polimi.ingsw.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class CellTest {
    Cell tested = new Cell(3, 4);
    Builder testBuilder = new Builder(new Player("A"));


    @Test
    public void basicTest(){

        //Initial values
        assertEquals(tested.getHeight(), 0);
        assertEquals(tested.isDomePresent(), false);
        assertEquals(tested.getBuilder(), null);
        assertEquals(tested.getIcoordinate(),3);
        assertEquals(tested.getJcoordinate(),4);

    }

    @Test
    public void occupantTest(){

        Cell tested = new Cell(3, 4);
        Builder testBuilder = new Builder(new Player("A"));
        Builder test2Builder = new Builder(new Player("B"));

        try {
            tested = tested.setOccupant(testBuilder);
        } catch (AlreadyOccupiedException e){
            fail("Shouldn't be occupied at this stage");
        }
        catch (InvalidOccupationException e){
            fail("Cannot occupy a Cell with a doom");
        }
        assertEquals(tested.isOccupied(), true);
        assertEquals(tested.getBuilder(), testBuilder);

        Cell testedTmp = tested;
        assertThrows(AlreadyOccupiedException.class,()->testedTmp.setOccupant(test2Builder));

        try {
            tested = tested.setOccupant(null);
        } catch (AlreadyOccupiedException e){
            fail("Set occupant should not care about AlreadyOccupiedException");
        } catch (InvalidOccupationException e){
            fail("Cannot occupy a Cell with a doom");
        }

        assertEquals(tested.isOccupied(), false);
    }


    @Test
    public void commonAddBlockTest(){
        Cell tested2 = new Cell(2,3);
        try {
        tested2 = tested2.addBlock(false);
        assertEquals(tested2.getHeight(), 1);
        tested2 = tested2.addBlock(false);
        assertEquals(tested2.getHeight(), 2);
        tested2 = tested2.addBlock(false);
        tested2 = tested2.addBlock(false);
    } catch (MaxHeightReachedException e) {
        fail("Shouldn't have reached max height");
    }
        Cell tested3 = tested2;
        assertThrows(MaxHeightReachedException.class,()->tested3.addBlock(false));

    }

    @Test
    public void domeAddBlockException(){

        tested = new Cell(3, 4);

        try {
            tested = tested.addBlock(true);
        } catch (MaxHeightReachedException e){
            fail("Shouldn't have reached max height");
        }
        assertTrue(tested.isDomePresent());
        assertThrows(MaxHeightReachedException.class,()->tested.addBlock(false));

    }

    @Test
    public void validCoordinates(){
        assertThrows(IllegalArgumentException.class, ()->new Cell(6,7));
        assertThrows(IllegalArgumentException.class, ()->new Cell(-6,-7));
    }
}
