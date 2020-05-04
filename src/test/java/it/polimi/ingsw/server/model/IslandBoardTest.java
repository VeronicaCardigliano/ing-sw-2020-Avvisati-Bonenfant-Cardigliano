package it.polimi.ingsw.server.model;
import it.polimi.ingsw.server.model.gameMap.Cell;
import it.polimi.ingsw.server.model.gameMap.IslandBoard;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IslandBoardTest {

    //getCell has valid arguments, in case it's not, raise an exception
    //IslandBoard should not have special cases
    //Need to check that for each "x" Cell x.getIcoordinate and x.getjcoordinate is equal to indexOf(x)

    @Test
    public void validArgument(){
        IslandBoard board = new IslandBoard();
        Cell cell = board.getCell(2,3);

//        assertEquals(2, cell.getI());
//        assertEquals(3, cell.getJ());

        assertThrows(RuntimeException.class, () -> board.getCell(7,6));
        assertThrows(RuntimeException.class, () -> board.getCell(-7,6));
        assertThrows(RuntimeException.class, () -> board.getCell(7,-6));
        assertThrows(RuntimeException.class, () -> board.getCell(1,6));
        assertThrows(RuntimeException.class, () -> board.getCell(7,1));
        assertThrows(RuntimeException.class, () -> board.getCell(1,-6));
        assertThrows(RuntimeException.class, () -> board.getCell(-7,1));

    }

    @Test
    public void congruentCoordinates(){
        IslandBoard board = new IslandBoard();
        for (int i = 0; i < IslandBoard.dimension; i++){
            for (int j = 0; j < IslandBoard.dimension; j++){
                assertEquals(board.getCell(i,j).getI(),i);
                assertEquals(board.getCell(i,j).getJ(),j);
            }
        }
    }

    @Test
    public void distanceOneTest() {
        assertTrue(IslandBoard.distanceOne(0,0,1,0));
        assertFalse(IslandBoard.distanceOne(0,0,2,0));
        assertTrue(IslandBoard.distanceOne(0,0,0,1));
        assertFalse(IslandBoard.distanceOne(0,0,0,2));
        assertTrue(IslandBoard.distanceOne(2,2,3,3));
        assertFalse(IslandBoard.distanceOne(2,2,4,4));
        assertFalse(IslandBoard.distanceOne(0,0,0,0));

        Cell cell = new Cell(2,2);
        Cell cellIJ;

        IslandBoard map = new IslandBoard();
        for(int i = 0; i < IslandBoard.dimension; i++)
            for(int j = 0; j < IslandBoard.dimension; j++) {
                cellIJ = map.getCell(i, j);
                if(IslandBoard.distanceOne(cellIJ.getI(), cellIJ.getJ(), cell.getI(), cell.getJ()))
                    assertTrue(IslandBoard.distanceOne(cell, cellIJ));
                else
                    assertFalse(IslandBoard.distanceOne(cell, cellIJ));
            }

    }

}
