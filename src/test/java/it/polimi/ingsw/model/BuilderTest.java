package it.polimi.ingsw.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.InvalidParameterException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BuilderTest {
    Player player = new Player ("sampleNick");
    Builder builderTester = new Builder(player);

    @Test
    public void  expectedExceptionIfPlayerNull (){
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Builder(null);
        });
    }

    @Test
    public void  expectedExceptionIfCellNull (){
        Builder nullBuilderTester = new Builder(null);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            builderTester.setCell(null);
        });
    }


}