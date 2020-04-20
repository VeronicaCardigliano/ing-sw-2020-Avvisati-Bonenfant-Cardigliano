package it.polimi.ingsw.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PlayerTest {
    Player playerTester = new Player ("sampleNick");

    @Test
    public void  expectedExceptionIfGodCardNull (){
        Assertions.assertThrows(IllegalArgumentException.class, () -> playerTester.setGodCard(null));
    }

    @Test
    public void  expectedExceptionIfPlayerNull (){
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Player(null));
    }

    @Test
    public void notNullBuilders () {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                playerTester.setBuilders(null, new Builder(playerTester, Builder.BuilderColor.WHITE)));
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                playerTester.setBuilders(new Builder (playerTester, Builder.BuilderColor.WHITE), null));
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                playerTester.setBuilders(null, null));
    }

    @Test
    public void differentBuilders (){
        Builder builderTester = new Builder(playerTester, Builder.BuilderColor.WHITE);
        Assertions.assertThrows(IllegalArgumentException.class, () -> playerTester.setBuilders(builderTester, builderTester));
    }

}

