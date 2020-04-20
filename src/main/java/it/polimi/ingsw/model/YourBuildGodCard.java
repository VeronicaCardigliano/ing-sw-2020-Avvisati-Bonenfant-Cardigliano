package it.polimi.ingsw.model;

import org.json.JSONObject;

public class YourBuildGodCard extends GodCard {

    private boolean canBuildDomeEverywhere;
    private boolean secondBuildDiffDest;
    private int numberOfBuilds;
    private boolean secondBuildNotDome;
    private Player player;
    private Cell firstBuildDst;

    /**
     * @author veronica
     *
     * @param player     whose card is
     * @param jsonObject constructor parses JSON
     */
    public YourBuildGodCard(Player player, JSONObject jsonObject) {
        super(player, jsonObject);

        if (jsonObject.opt("canBuildDomeEverywhere") != null) {
            this.canBuildDomeEverywhere = jsonObject.getBoolean("canBuildDomeEverywhere");
        } else this.canBuildDomeEverywhere = false;

        if (jsonObject.opt("secondBuildDiffDest") != null) {
            this.secondBuildDiffDest = jsonObject.getBoolean("secondBuildDiffDest");
        } else this.secondBuildDiffDest = false;

        if (jsonObject.opt("secondBuildNotDome") != null) {
            this.secondBuildNotDome = jsonObject.getBoolean("secondBuildNotDome");
        } else this.secondBuildNotDome = false;

        if (jsonObject.opt("numberOfBuilds") != null) {
            this.numberOfBuilds = jsonObject.getInt("numberOfBuilds");
        } else this.numberOfBuilds = 1;

        this.player = player;
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
