package it.polimi.ingsw.model;


import it.polimi.ingsw.model.gameMap.Builder;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BuilderTest {
    Player player = new Player ("sampleNick");

    @Test
    public void  expectedExceptionIfPlayerNull (){
        assertThrows(IllegalArgumentException.class, () -> new Builder(null, Builder.BuilderColor.WHITE));
    }

    @Test
    public void  cannotSetANullCell (){
        assertThrows(IllegalArgumentException.class, () -> new Builder(player, Builder.BuilderColor.WHITE).setCell(null));
    }

}