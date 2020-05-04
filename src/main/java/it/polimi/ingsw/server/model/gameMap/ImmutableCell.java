package it.polimi.ingsw.server.model.gameMap;

public class ImmutableCell extends Coordinates{
    private final int height;
    private final boolean domePresent;

    public ImmutableCell(Cell cell) {
        super(cell.getI(), cell.getJ());

        this.height = cell.getHeight();
        this.domePresent = cell.isDomePresent();
    }

    public int getHeight() {
        return height;
    }

    public boolean isDomePresent() {
        return domePresent;
    }
}
