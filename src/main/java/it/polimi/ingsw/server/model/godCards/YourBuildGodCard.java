package it.polimi.ingsw.server.model.godCards;

import it.polimi.ingsw.server.model.gameMap.Cell;
import it.polimi.ingsw.server.model.gameMap.IslandBoard;
import it.polimi.ingsw.server.model.Player;

import java.util.ArrayList;
import java.util.Map;

public class YourBuildGodCard extends GodCard {

    private boolean canBuildDomeEverywhere;
    private boolean secondBuildDiffDest;
    private boolean secondBuildNotDome;
    private boolean blockUnderItself;
    private boolean extraBuildNotPerimeter;
    private int numberOfBuilds;
    private Cell firstBuildDst;

    /**
     * @author veronica
     * @param player whose card is
     */

    public YourBuildGodCard(Player player, String name, String description, ArrayList<ArrayList<String>> states,
                            Map<String, Boolean> flagParameters, Map<String, Integer> intParameters) {
        super(player, name, description, states);

        this.numberOfBuilds = intParameters.get("numberOfBuilds");
        this.canBuildDomeEverywhere = flagParameters.get("canBuildDomeEverywhere");
        this.secondBuildDiffDest = flagParameters.get("secondBuildDiffDest");
        this.secondBuildNotDome = flagParameters.get("secondBuildNotDome");
        this.blockUnderItself = flagParameters.get("blockUnderItself");
        this.extraBuildNotPerimeter = flagParameters.get("extraBuildNotPerimeter");

    }

    protected Cell getFirstBuildDst() {
        return firstBuildDst;
    }

    /**
     * This override adds the powers of Hephaestus and Demeter, which can build two times but in a specific way
     * (the first one in the same space, the second one in a new destination)
     * and the power of Atlas that can build domes at any level
     */
    @Override
    public boolean askBuild(int i_src, int j_src, int i_dst, int j_dst, boolean buildDome) {

        boolean extraConditions = true;
        Cell src = gameMap.getCell(i_src, j_src);
        Cell dst = gameMap.getCell(i_dst, j_dst);

        boolean buildHeightCondition = (dst.getHeight() < 3 && !buildDome) || (dst.getHeight() == 3 && buildDome);

        if (super.step == 2){
            if (!secondBuildDiffDest && secondBuildNotDome)
                //Hephaestus GodCard effect
                extraConditions = !buildDome && dst == firstBuildDst;
            else if (secondBuildDiffDest && !secondBuildNotDome)
                //Demeter GodCard effect
                extraConditions = dst != firstBuildDst;
            else if (!secondBuildDiffDest && extraBuildNotPerimeter) {
                //Hestia GodCard effect

                    extraConditions = (i_dst != 0 && i_dst != IslandBoard.dimension - 1 &&
                                       j_dst != 0 && j_dst != IslandBoard.dimension - 1);

            }
        }

        return (super.askBuild(i_src, j_src, i_dst, j_dst,buildDome) || (src.getBuilder() != null &&
                src.getBuilder().getPlayer().equals(player) && !dst.isDomePresent() && !dst.isOccupied() && IslandBoard.distanceOne(src, dst) &&
                //adding Atlas possibility to the normal return value, removing stdBuildHeightCondition flag
                canBuildDomeEverywhere && (dst.getHeight() < 3 || buildDome)) ||
                (src.getBuilder() != null && src.getBuilder().getPlayer().equals(player) && !dst.isDomePresent() &&
                //adding Zeus possibility to the normal return value, removing distanceOne = true and !isOccupied() flags
                 buildHeightCondition && (blockUnderItself && !buildDome && src.getHeight() < 3) && src.equals(dst))) && extraConditions;
    }

    /**
     * This override saves the firstBuildDst if the Card's step is 1 (it's the first Build)
     */

    @Override
    public boolean build (int i_src, int j_src, int i_dst, int j_dst, boolean buildDome) {

        if (numberOfBuilds == 2 && super.step == 1)

            firstBuildDst = gameMap.getCell(i_dst, j_dst);

        return super.build(i_src, j_src, i_dst, j_dst, buildDome);
    }

}
