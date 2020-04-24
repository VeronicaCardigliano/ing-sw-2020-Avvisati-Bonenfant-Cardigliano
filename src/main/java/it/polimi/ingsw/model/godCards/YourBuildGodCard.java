package it.polimi.ingsw.model.godCards;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Player;

import java.util.ArrayList;

public class YourBuildGodCard extends GodCard {

    private boolean canBuildDomeEverywhere;
    private boolean secondBuildDiffDest;
    private int numberOfBuilds;
    private boolean secondBuildNotDome;
    private Cell firstBuildDst;

    /**
     * @author veronica
     *
     * @param player     whose card is
     */
    public YourBuildGodCard(Player player, String name, String description, ArrayList<ArrayList<String>> states,
                            int numberOfBuilds, boolean canBuildDomeEverywhere, boolean secondBuildDiffDest, boolean secondBuildNotDome) {
        super(player, name, description, states);

        this.numberOfBuilds = numberOfBuilds;
        this.canBuildDomeEverywhere = canBuildDomeEverywhere;
        this.secondBuildDiffDest = secondBuildDiffDest;
        this.secondBuildNotDome = secondBuildNotDome;

    }

    public Cell getFirstBuildDst() {
        return firstBuildDst;
    }

    @Override
    public boolean askBuild(int i_src, int j_src, int i_dst, int j_dst, boolean buildDome) {
        Cell src;
        Cell dst;
        boolean buildHeightCondition;
        boolean extraConditions = true;

        src = gameMap.getCell(i_src, j_src);
        dst = gameMap.getCell(i_dst, j_dst);

        if (canBuildDomeEverywhere)
            // if dst height is 3, the player can build only a Dome
            buildHeightCondition = dst.getHeight() < 3 || buildDome;
        else
            buildHeightCondition = (dst.getHeight() < 3 && !buildDome) || (dst.getHeight() == 3 && buildDome);

        if (super.step == 3){
            if (!secondBuildDiffDest && secondBuildNotDome)
                //Hephaestus GodCard effect
                extraConditions = !buildDome && dst == firstBuildDst;
            else if (secondBuildDiffDest && !secondBuildNotDome)
                //Demeter GodCard effect
                extraConditions = dst != firstBuildDst;
        }


        return src.getBuilder() != null && src.getBuilder().getPlayer().equals(player) &&
                !dst.isDomePresent() && !dst.isOccupied() && buildHeightCondition && extraConditions;
    }

    @Override
    public boolean build (int i_src, int j_src, int i_dst, int j_dst, boolean buildDome) {

        if (numberOfBuilds == 2 && super.step == 2)

            firstBuildDst = gameMap.getCell(i_dst, j_dst);

        return super.build(i_src, j_src, i_dst, j_dst, buildDome);
    }

}
