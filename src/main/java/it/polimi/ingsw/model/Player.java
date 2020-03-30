package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author veronica
 * Player is one of the players of the game with a list of two builders and an unique nickname
 */

public class Player {
    //private GodCard godCard;
    private final List<Builder> builders = new ArrayList<>();
    private final String nickname;

    /**
     * @param nickname : unique identifier of each player of the game
     * @throws IllegalArgumentException if nickname null
     */

    public Player(String nickname) throws IllegalArgumentException {
        if (nickname == null)
            throw new IllegalArgumentException ("Nickname can't be null");
        this.nickname = nickname;
    }

    public void setBuilders (Builder builder1, Builder builder2) {
        if (builder1 == null || builder2 == null)
            throw new IllegalArgumentException ("Nickname can't be null");
        if (builder1 == builder2)
            throw new IllegalArgumentException ("Builders of the same player have to be different");
        builders.add(builder1);
        builders.add(builder2);
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
}
