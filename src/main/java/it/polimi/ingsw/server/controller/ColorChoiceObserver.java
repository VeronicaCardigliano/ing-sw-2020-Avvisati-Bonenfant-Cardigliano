package it.polimi.ingsw.server.controller;

/**
 * This interface is implemented to be informed about a new builders color choice by a player.
 */
public interface ColorChoiceObserver {
    void onColorChoice (String nickname, String chosenColor);
}
