package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.gameMap.Builder;
import it.polimi.ingsw.server.model.godCards.GodCard;

import java.util.ArrayList;
import java.util.List;


/**
 * Player is one of the players of the game with a list of two builders and a unique nickname
 */

public class Player {
    private GodCard godCard;
    private final List<Builder> builders = new ArrayList<>();
    private final String nickname;
    private final long birthday;

    /**
     * @param nickname : unique identifier of each player of the game
     * @throws IllegalArgumentException if nickname null
     */
    public Player(String nickname) throws IllegalArgumentException {
        this (nickname, 0);
    }

    public Player(String nickname, long birthday) {
        if (nickname == null)
            throw new IllegalArgumentException ("Nickname can't be null");
        this.nickname = nickname;
        this.birthday = birthday;
    }

    /**
     * Shortcut for move in godCard
     */
    public boolean move(int i_src, int j_src, int i_dst, int j_dst) {
        return godCard.move(i_src, j_src, i_dst, j_dst);
    }

    /**
     * Shortcut for build in godCard
     */
    public boolean build(int i_src, int j_src, int i_dst, int j_dst, boolean buildDome) {
        return godCard.build(i_src, j_src, i_dst, j_dst, buildDome);
    }

    /**
     * Set the builders for the current player
     * @throws IllegalArgumentException in case nickname is NULL or builder1 and builder2 are equals
     */
    public void setBuilders (Builder builder1, Builder builder2) {
        if (builder1 == null || builder2 == null)
            throw new IllegalArgumentException ("Nickname can't be null");
        if (builder1 == builder2)
            throw new IllegalArgumentException ("Builders of the same player have to be different");
        builders.add(builder1);
        builders.add(builder2);
    }

    public String getNickname (){
        return nickname;
    }

    public void setGodCard (GodCard godCard) throws IllegalArgumentException {
        if (godCard == null)
            throw new IllegalArgumentException ("GodCard can't be null");
        this.godCard = godCard;
    }

    public List<Builder> getBuilders () {
        return builders;
    }

    public GodCard getGodCard () {
        return godCard;
    }

    public long getBirthday () {
        return birthday;
    }

    /**
     * Shortcut for godCard startTurn
     */
    public void startTurn() {
        godCard.startTurn();
    }

    /**
     * Shortcut for godCard forceState
     */
    public void forceStep(String step) {
        godCard.forceState(step);
    }
}