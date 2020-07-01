package it.polimi.ingsw.server.model.godCards;

import it.polimi.ingsw.server.model.gameMap.Cell;
import it.polimi.ingsw.server.model.gameMap.IslandBoard;
import it.polimi.ingsw.server.model.Player;
import java.util.ArrayList;
import java.util.Map;

/**
 * Specific Card associated to a god whom effect activates during his turn. This kind of god implements his own
 * build depending on three different parameters read from the json file.
 */
public class YourBuildGodCard extends GodCard {

    private boolean canBuildDomeEverywhere;
    private boolean secondBuildDiffDest;
    private boolean secondBuildNotDome;
    private boolean blockUnderItself;
    private boolean extraBuildNotPerimeter;
    private int numberOfBuilds;
    private Cell firstBuildDst;

    public YourBuildGodCard (Player player, String name, String description, ArrayList<ArrayList<String>> states,
                            Map<String, Boolean> flagParameters, Map<String, Integer> intParameters) {
        super(player, name, description, states);

        this.numberOfBuilds = intParameters.get("numberOfBuilds");
        this.canBuildDomeEverywhere = flagParameters.get("canBuildDomeEverywhere");
        this.secondBuildDiffDest = flagParameters.get("secondBuildDiffDest");
        this.secondBuildNotDome = flagParameters.get("secondBuildNotDome");
        this.blockUnderItself = flagParameters.get("blockUnderItself");
        this.extraBuildNotPerimeter = flagParameters.get("extraBuildNotPerimeter");

    }

    /**
     * This override of askBuild considers extraConditions of the gods which can build a second time in a different or same
     * space or not in the perimeter. Considers also cards that can build a dome everywhere and a block under themselves.
     * @param buildDome true if the builder wants to build a dome
     */
    @Override
    public boolean askBuild(int i_src, int j_src, int i_dst, int j_dst, boolean buildDome) {

        boolean extraConditions = true;
        Cell src = gameMap.getCell(i_src, j_src);
        Cell dst = gameMap.getCell(i_dst, j_dst);

        boolean buildHeightCondition = (dst.getHeight() < IslandBoard.maxHeight && !buildDome) ||
                (dst.getHeight() == IslandBoard.maxHeight && buildDome);

        if (super.step == 2){
            if (!secondBuildDiffDest && secondBuildNotDome)
                extraConditions = !buildDome && dst == firstBuildDst;
            else if (secondBuildDiffDest && !secondBuildNotDome)
                extraConditions = dst != firstBuildDst;

            if (!secondBuildDiffDest && !secondBuildNotDome && extraBuildNotPerimeter) {
                    extraConditions = (i_dst != 0 && i_dst != IslandBoard.dimension - 1 &&
                                       j_dst != 0 && j_dst != IslandBoard.dimension - 1);
            }
        }
        return (super.askBuild(i_src, j_src, i_dst, j_dst, buildDome) || (src.getBuilder() != null &&
                src.getBuilder().getPlayer().equals(player) && !dst.isDomePresent() && !dst.isOccupied() && IslandBoard.distanceOne(src, dst) &&
                canBuildDomeEverywhere && (dst.getHeight() < IslandBoard.maxHeight || buildDome)) ||
                (src.getBuilder() != null && src.getBuilder().getPlayer().equals(player) && !dst.isDomePresent() &&
                 buildHeightCondition && blockUnderItself && !buildDome && src.getHeight() < IslandBoard.maxHeight &&
                        src.equals(dst))) && extraConditions;
    }


    /**
     * This override of build saves the firstBuildDst to do the necessary checks if there's a possible second build
     */
    @Override
    public boolean build (int i_src, int j_src, int i_dst, int j_dst, boolean buildDome) {
        if (numberOfBuilds == 2 && super.step == 1)
            firstBuildDst = gameMap.getCell(i_dst, j_dst);
        return super.build(i_src, j_src, i_dst, j_dst, buildDome);
    }

}
