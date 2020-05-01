package it.polimi.ingsw.model.gameMap;

public class Coordinates {
    private final int i;
    private final int j;

    public Coordinates(int i, int j) {
        if (!(i >= 0 && i < IslandBoard.dimension && j >= 0 && j < IslandBoard.dimension)) throw new IllegalArgumentException("Invalid coordinates");
        else {
            this.i = i;
            this.j = j;
        }
    }


    public Coordinates(Coordinates c) {
        this.i = c.getI();
        this.j = c.getJ();
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public static boolean equals(Coordinates c1, Coordinates c2) {
        return c1.getI() == c2.getI() && c1.getJ() == c2.getJ();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinates that = (Coordinates) o;

        if (i != that.i) return false;
        return j == that.j;
    }

    @Override
    public int hashCode() {
        int result = i;
        result = 31 * result + j;
        return result;
    }
}
