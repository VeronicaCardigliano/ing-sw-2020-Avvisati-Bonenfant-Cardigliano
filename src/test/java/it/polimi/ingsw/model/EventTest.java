package it.polimi.ingsw.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;



public class EventTest {
    Event event;
    Cell cell = new Cell(1,1);

    @Test
    public void moveEventDoesNotBuild() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            event = new Event(Event.EventType.MOVE, cell, cell, cell, false);
        });
    }

    @Test
    public void moveEventDoesNotBuildDome() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            event = new Event(Event.EventType.MOVE, cell, cell, null, true);
        });
    }

    @Test
    public void buildEventDoesNotMove() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            event = new Event(Event.EventType.BUILD, cell, cell, cell, false);
        });
    }

}
