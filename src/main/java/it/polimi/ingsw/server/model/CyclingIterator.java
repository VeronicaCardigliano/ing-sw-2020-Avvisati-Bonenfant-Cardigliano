package it.polimi.ingsw.server.model;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Iterator use to manage turns into the game
 */
public class CyclingIterator<T> implements Iterator<T> {
    private final ArrayList<T> data;
    private int index;

    public CyclingIterator (ArrayList<T> data){
        this.index = -1;
        this.data = data;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public T next() {
        index ++;
        if (index >= data.size())
            index = 0;
        return data.get(index);
    }

}