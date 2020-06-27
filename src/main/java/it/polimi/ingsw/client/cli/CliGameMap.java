package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.GameMap;
import it.polimi.ingsw.client.View;
import it.polimi.ingsw.server.model.gameMap.Coordinates;
import it.polimi.ingsw.server.model.gameMap.IslandBoard;

import java.util.Set;

public class CliGameMap extends GameMap {

    private final String green = Color.ANSI_GREEN.escape(), yellow = Color.ANSI_YELLOW.escape(), blue = Color.ANSI_BLUE.escape();
    private String builderColor;
    private final String reset = Color.RESET;




    private static final String dottedCircle = "\u25CC", filledCircle = "\u25CF", hammerAndPick = "\u2692",
            boxDrawingsVerticalLine = "\u2502", boxDrawingsVerticalAndRight = "\u2502", boxDrawingsVerticalAndLeft = "\u2524",
            boxDrawingsDownAndHorizontal = "\u252C", boxDrawingsUpAndHorizontal = "\u2534", boxDrawingsHorizontal = "\u2500",
            boxDrawingsDownAndRight = "\u250C", boxDrawingsDownAndLeft = "\u2510", boxDrawingsUpAndRight = "\u2514",
            boxDrawingsUpAndLeft = "\u2518", boxDrawingsVerticalAndHorizontal = "\u253C";

    private final String horizontalLine = green + boxDrawingsHorizontal + boxDrawingsHorizontal + boxDrawingsHorizontal + boxDrawingsHorizontal;
    private final String possibleDstSymbol = yellow + " " + dottedCircle + reset;
    private final String dome = blue + filledCircle + reset;
    private final String builder =  " " + hammerAndPick + reset;


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        int i;
        result.append(printHorizontalIndexes());
        result.append(printFirstLine());
        for (i=0; i < IslandBoard.dimension - 1; i++) {
            result.append(printContents(i, getPossibleDst().get(0), getPossibleDst().get(1), getChosenBuilderNum()));
            result.append(printCentralLine());
        }
        result.append(printContents(i, getPossibleDst().get(0), getPossibleDst().get(1), getChosenBuilderNum()));
        result.append(printLastLine());

        return result.toString();
    }


    //utility function to print map
    private String printHorizontalIndexes () {
        StringBuilder indexes = new StringBuilder("   ");
        for (int i = 0; i< IslandBoard.dimension; i++) {
            indexes.append("  ").append(i).append("  ");
        }
        return indexes.toString() + "\n";
    }

    private String printFirstLine() {
        return("   " + green + boxDrawingsDownAndRight + horizontalLine + boxDrawingsDownAndHorizontal + horizontalLine +
                boxDrawingsDownAndHorizontal + horizontalLine + boxDrawingsDownAndHorizontal + horizontalLine + boxDrawingsDownAndHorizontal +
                horizontalLine + boxDrawingsDownAndLeft + reset + "\n");
    }

    private String printCentralLine() {
        return("   " + green + boxDrawingsVerticalAndRight + horizontalLine + boxDrawingsVerticalAndHorizontal +
                horizontalLine + boxDrawingsVerticalAndHorizontal + horizontalLine + boxDrawingsVerticalAndHorizontal +
                horizontalLine + boxDrawingsVerticalAndHorizontal + horizontalLine + boxDrawingsVerticalAndLeft + reset + "\n");
    }



    private boolean controlIfPossibleDst (Set<Coordinates> possibleDstSet, Coordinates coordinates) {
        boolean result = false;
        for (Coordinates coord : possibleDstSet) {
            if (Coordinates.equals(coordinates, coord)){
                result = true;
            }
        }
        return result;
    }


    public String printContents(int i, Set<Coordinates> possibleDstBuilder1,
                               Set<Coordinates> possibleDstBuilder2, int chosenBuilderNumber) {
        StringBuilder line = new StringBuilder(" " + i + " ");
        int height;
        boolean printed;
        for (int j=0; j < IslandBoard.dimension; j++) {
            printed = false;
            Coordinates coordinates = new Coordinates(i,j);
            //if I have a builder -> builder, if I have possibleDst -> possibleDst, else empty
            for (String player : getOccupiedCells().keySet()) {

                if (Coordinates.equals(coordinates, getOccupiedCells().get(player).get(0)) ||
                        Coordinates.equals(coordinates, getOccupiedCells().get(player).get(1))) {

                    //I could want to print the possible destinations of just one builder or both, or none of them
                    //With the following 'if' I control whether the builder is also one of the possible destinations
                    if ((possibleDstBuilder1 != null && (chosenBuilderNumber == 0 || chosenBuilderNumber == 1) &&
                            controlIfPossibleDst(possibleDstBuilder1, coordinates)) || (possibleDstBuilder2 != null &&
                            (chosenBuilderNumber == 0 || chosenBuilderNumber == 2) && controlIfPossibleDst(possibleDstBuilder2, coordinates))) {
                        builderColor = Color.ANSI_YELLOW.escape();
                    }
                    else
                        builderColor = Color.returnColor(View.getColor(player).toUpperCase());
                    line.append(green).append(boxDrawingsVerticalLine).append(reset).append(builderColor).append(builder);
                    printed = true;
                }
            }
            //possibleDstBuilder sets are null if the match is not still in the Game state
            //if the player has chosen the builder to use, it'll show just the chosenBuilder possible destinations

            if (!printed && ((possibleDstBuilder1 != null && (chosenBuilderNumber == 0 || chosenBuilderNumber == 1) &&
                    controlIfPossibleDst(possibleDstBuilder1, coordinates)) || (possibleDstBuilder2 != null &&
                    (chosenBuilderNumber == 0 || chosenBuilderNumber == 2) && controlIfPossibleDst(possibleDstBuilder2, coordinates)))) {

                line.append(green).append(boxDrawingsVerticalLine).append(reset).append(possibleDstSymbol);
                printed = true;
            }

            if (!printed)
                line.append(green).append(boxDrawingsVerticalLine).append(reset).append("  ");

            height = getHeights().get(coordinates);
            if (height == -1)
                line.append(dome).append(" ");
            else if (height == 0)
                line.append("  ");
            else
                line.append(height).append(" ");
        }
        return line + green + boxDrawingsVerticalLine + reset + "\n";
    }

    private String printLastLine() {
        return ("   " + green + boxDrawingsUpAndRight + horizontalLine + boxDrawingsUpAndHorizontal + horizontalLine +
                boxDrawingsUpAndHorizontal + horizontalLine + boxDrawingsUpAndHorizontal + horizontalLine +
                boxDrawingsUpAndHorizontal + horizontalLine + boxDrawingsUpAndLeft + reset + "\n");
    }



}
