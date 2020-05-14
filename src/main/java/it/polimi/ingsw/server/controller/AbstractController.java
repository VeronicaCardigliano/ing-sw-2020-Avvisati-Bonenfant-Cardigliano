package it.polimi.ingsw.server.controller;

public abstract class AbstractController implements BuilderBuildObserver, BuilderMoveObserver, NewPlayerObserver, NumberOfPlayersObserver,
        GodCardChoiceObserver, ColorChoiceObserver, StepChoiceObserver, DisconnectionObserver, BuilderSetupObserver{
}
