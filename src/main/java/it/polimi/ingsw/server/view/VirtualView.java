package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.model.gameMap.Coordinates;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;

/**
 * view class notifies Controller (as an Observable) and is notified by Model (as an observer)
 */
public class VirtualView extends ViewObservable implements Runnable, BuilderPossibleMoveObserver, BuilderPossibleBuildObserver,
                            ErrorsObserver, BuildersPlacementObserver, PlayerLoseObserver, EndGameObserver {

    private final ViewManager viewManager;
    private final Socket socket;
    private String nickname;

    public VirtualView(Socket socket, ViewManager viewManager) {
        this.viewManager = viewManager;
        this.socket = socket;
    }

    public String getNickname() {
        return nickname;
    }

    private void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void run() {
        try {
            Scanner in = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void updatePossibleBuildDst(String nickname, Set possibleDstBuilder1, Set possibleDstBuilder2, Set possibleDstBuilder1forDome, Set possibleDstBuilder2forDome) {
        //show the possible destinations received, and take the choice notifying it to controller
        //notifyBuildChoice(src, dst, buildDome);
    }

    @Override
    public void onWrongInsertionUpdate(String nickname, String error) {
    }

    @Override
    public void onLossUpdate(String nickname, String currPlayer) {
    }

    @Override
    public void onEndGameUpdate(String winnerNickname) {
    }

    @Override
    public void onBuildersPlacementUpdate(Coordinates positionBuilder1, Coordinates positionBuilder2) {
    }


    @Override
    public void updatePossibleMoveDst(String nickname, Set possibleDstBuilder1, Set possibleDstBuilder2) {

    }
}
