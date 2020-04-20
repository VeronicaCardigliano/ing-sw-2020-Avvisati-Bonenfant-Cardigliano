package it.polimi.ingsw.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {
    Model modelTester = new Model(2);

    @Test
    public void alreadyPresentPlayer () {
        modelTester.addPlayer("SampleNickname");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            modelTester.addPlayer ("SampleNickname");
        } );
    }

    @Test
    public void expectedExceptionIfNicknameNull () {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            modelTester.addPlayer (null);
        });
    }

}
