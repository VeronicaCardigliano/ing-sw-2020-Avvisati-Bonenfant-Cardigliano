package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.gameMap.Cell;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EventTest {
    Event event;
    Cell cell1;
    Cell cell2;

    @BeforeEach
    public void setup() {
        cell1 = new Cell(0,0);
        cell2 = new Cell(0,1);
    }

    @Test
    public void height() {
        cell2.addBlock();
        event = new Event(Event.EventType.BUILD, cell1, cell2);
        assertEquals(1, event.getDstBlockHeight());
        assertEquals(1, event.heightDifference());
    }

    @Test
    public void incompleteEventTest() {
        Assertions.assertThrows(NullPointerException.class, () -> new Event(null, cell1, cell2));
        Assertions.assertThrows(NullPointerException.class, () -> new Event(Event.EventType.BUILD, cell1, null));
        Assertions.assertThrows(NullPointerException.class, () -> new Event(Event.EventType.BUILD, null, cell2));
    }

}
