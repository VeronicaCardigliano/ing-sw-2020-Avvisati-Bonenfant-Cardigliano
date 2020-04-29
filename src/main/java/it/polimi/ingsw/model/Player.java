package it.polimi.ingsw.model;

import it.polimi.ingsw.model.godCards.GodCard;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * @author veronica
 * Player is one of the players of the game with a list of two builders and an unique nickname
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
        this (nickname, "1970.02.14");
    }

    public Player(String nickname, String birthday) throws IllegalArgumentException {
        long epoch = 0;

        if (nickname == null)
            throw new IllegalArgumentException ("Nickname can't be null");
        this.nickname = nickname;

        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
        try {
            epoch = df.parse(birthday).getTime();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        this.birthday = epoch;
    }

    public boolean askMove(int i_src, int j_src, int i_dst, int j_dst) {
        return godCard.askMove(i_src, j_src, i_dst, j_dst);
    }

    public boolean askBuild(int i_src, int j_src, int i_dst, int j_dst, boolean buildDome) {
        return godCard.askBuild(i_src, j_src, i_dst, j_dst, buildDome);
    }

    public boolean move(int i_src, int j_src, int i_dst, int j_dst) {
        return godCard.move(i_src, j_src, i_dst, j_dst);
    }

    public boolean build(int i_src, int j_src, int i_dst, int j_dst, boolean buildDome) {
        return godCard.build(i_src, j_src, i_dst, j_dst);
    }

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

    public void startTurn() {
        godCard.startTurn();
    }
}