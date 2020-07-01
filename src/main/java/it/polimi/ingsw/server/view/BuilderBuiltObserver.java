package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.model.gameMap.Coordinates;

/** This interface is implemented to be informed about the effective build if the result is true,
 * the failure of a build attempt if the result is false
 */
public interface BuilderBuiltObserver {
    void onBuilderBuild(String nickname, Coordinates src, Coordinates dst, boolean dome, boolean result);
}
