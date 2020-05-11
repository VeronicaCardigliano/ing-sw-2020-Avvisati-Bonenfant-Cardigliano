package it.polimi.ingsw.server.view;

public interface ColorAssignmentObserver {
    void onColorAssigned (String nickname, String color, boolean result);
}
