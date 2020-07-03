package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.interfaces.controller.*;

public abstract class AbstractController implements BuilderBuildObserver, BuilderMoveObserver, NewPlayerObserver, NumberOfPlayersObserver,
        GodCardChoiceObserver, ColorChoiceObserver, StepChoiceObserver, BuilderSetupObserver, StartPlayerObserver {
}
