package it.polimi.ingsw.model.godCards;

import it.polimi.ingsw.model.Builder;
import it.polimi.ingsw.model.IslandBoard;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.parser.GodCardParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class YourTurnGodCardTest {
    static GodCard prometheusCard;
    static IslandBoard gameMap;
    static Player player;
    static Builder builder1;
    static Builder builder2;



    @BeforeAll
    public static void setup() {
        player = new Player("player");
        builder1 = new Builder(player);
        builder2 = new Builder(player);

        prometheusCard = (new GodCardParser("src/main/java/it/polimi/ingsw/parser/cards.json")).createCard(player, "Prometheus");
    }

    @BeforeEach
    public void clean() {
        gameMap = new IslandBoard();
        prometheusCard.setGameMap(gameMap);

        gameMap.getCell(2,2).setOccupant(builder1);
        gameMap.getCell(4,4).setOccupant(builder2);


    }

    @Test
    public void prometheus() {
        prometheusCard.startTurn();

        assertEquals("BOTH", prometheusCard.currState);
        assertTrue(prometheusCard.build(2,2,2,3));
        assertEquals("MOVE", prometheusCard.currState);
        assertFalse(prometheusCard.askMove(2,2,2,3));
        assertTrue(prometheusCard.move(2,2,3,2));
        assertEquals("BUILD", prometheusCard.currState);
        assertTrue(prometheusCard.build(3,2,2,2));

    }
}
