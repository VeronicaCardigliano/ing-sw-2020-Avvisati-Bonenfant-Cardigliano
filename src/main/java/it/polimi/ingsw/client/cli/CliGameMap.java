package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.server.model.gameMap.Coordinates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class is the GameMap Object
 */
public class CliGameMap {
    private String green = Color.ANSI_GREEN.escape(), yellow = Color.ANSI_YELLOW.escape(), blue = Color.ANSI_BLUE.escape();
    private String builderColor;
    private String reset = Color.RESET;

    private static final String dottedCircle = "\u25CC", filledCircle = "\u25CF", hammerAndPick = "\u2692",
            boxDrawingsVerticalLine = "\u2502", boxDrawingsVerticalAndRight = "\u2502", boxDrawingsVerticalAndLeft = "\u2524",
            boxDrawingsDownAndHorizontal = "\u252C", boxDrawingsUpAndHorizontal = "\u2534", boxDrawingsHorizontal = "\u2500",
            boxDrawingsDownAndRight = "\u250C", boxDrawingsDownAndLeft = "\u2510", boxDrawingsUpAndRight = "\u2514",
            boxDrawingsUpAndLeft = "\u2518", boxDrawingsVerticalAndHorizontal = "\u253C";

    private String horizontalLine = green + boxDrawingsHorizontal + boxDrawingsHorizontal + boxDrawingsHorizontal + boxDrawingsHorizontal;
    private String possibleDstSymbol = yellow + " " + dottedCircle + reset;
    private String dome = blue + filledCircle + reset;
    private String builder =  " " + hammerAndPick + reset;

    private Map<Coordinates, Integer> heights = new HashMap<>();

    //initialize the heights to zero for each cell
    public CliGameMap() {
        for (int i=0; i < Cli.mapDimension; i++)
            for (int j=0; j < Cli.mapDimension; j++)
                heights.put(new Coordinates(i,j), 0);
    }

    public void modifyHeight(Coordinates coord, boolean dome) {
        if (dome)
            heights.put(coord, -1);
        else
            heights.put(coord, heights.get(coord) + 1);
    }

    private void printHorizontalIndexes () {
        StringBuilder indexes = new StringBuilder("   ");
        for (int i=0; i<Cli.mapDimension; i++) {
            indexes.append("  ").append(i).append("  ");
        }
        System.out.println(indexes);
    }

    private void printFirstLine() {
        System.out.println("   " + green + boxDrawingsDownAndRight + horizontalLine + boxDrawingsDownAndHorizontal + horizontalLine +
                boxDrawingsDownAndHorizontal + horizontalLine + boxDrawingsDownAndHorizontal + horizontalLine + boxDrawingsDownAndHorizontal +
                horizontalLine + boxDrawingsDownAndLeft + reset);
    }

    private void printCentralLine() {
        System.out.println("   " + green + boxDrawingsVerticalAndRight + horizontalLine + boxDrawingsVerticalAndHorizontal +
                horizontalLine + boxDrawingsVerticalAndHorizontal + horizontalLine + boxDrawingsVerticalAndHorizontal +
                horizontalLine + boxDrawingsVerticalAndHorizontal + horizontalLine + boxDrawingsVerticalAndLeft + reset);
    }

    private String returnColor (String player) {
        String color = "";
        switch (player) {
            case "MAGENTA":
                color = Color.ANSI_MAGENTA.escape();
                break;
            case "WHITE":
                color = "";
                break;
            case "LIGHT_BLUE":
                color =  Color.ANSI_LIGHTBLUE.escape();
                break;
        }
        return color;
    }

    //i is the row and j is the column
    private void printContents(int i, Map<String, ArrayList<Coordinates>> occupiedCells, Set<Coordinates> possibleDstBuilder1,
                               Set<Coordinates> possibleDstBuilder2, int chosenBuilderNumber) {
        StringBuilder line = new StringBuilder(" " + i + " ");
        int height;
        boolean printed;
        for (int j=0; j < Cli.mapDimension; j++) {
            printed = false;
            Coordinates coordinates = new Coordinates(i,j);
            //if I have a builder -> builder, if I have possibleDst -> possibleDst, else empty
            for (String player : occupiedCells.keySet()) {

                if (Coordinates.equals(coordinates, occupiedCells.get(player).get(0)) ||
                        Coordinates.equals(coordinates, occupiedCells.get(player).get(1))) {

                    //I could want to print the possible destinations of just one builder or both, or none of them
                    //With the following 'if' I control whether the builder is also one of the possible destinations
                    if ((possibleDstBuilder1 != null && (chosenBuilderNumber == 0 || chosenBuilderNumber == 1) &&
                            controlIfPossibleDst(possibleDstBuilder1, coordinates)) || (possibleDstBuilder2 != null &&
                            (chosenBuilderNumber == 0 || chosenBuilderNumber == 2) && controlIfPossibleDst(possibleDstBuilder2, coordinates))) {
                        builderColor = Color.ANSI_YELLOW.escape();
                    }
                    else
                        builderColor = returnColor(Cli.getColor(player).toUpperCase());
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

            height = heights.get(coordinates);
            if (height == -1)
                line.append(dome).append(" ");
            else if (height == 0)
                line.append("  ");
            else
                line.append(height).append(" ");
        }
        System.out.print(line + green + boxDrawingsVerticalLine + reset + "\n");
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

    private void printLastLine() {
        System.out.println("   " + green + boxDrawingsUpAndRight + horizontalLine + boxDrawingsUpAndHorizontal + horizontalLine +
                boxDrawingsUpAndHorizontal + horizontalLine + boxDrawingsUpAndHorizontal + horizontalLine +
                boxDrawingsUpAndHorizontal + horizontalLine + boxDrawingsUpAndLeft + reset);
    }

    private void printChosenGodCards () {
        Map<String,String> chosenGodCards = Cli.getChosenGodCards();
        for (String player : chosenGodCards.keySet()) {

            String playerColor = returnColor(Cli.getColor(player).toUpperCase());
            System.out.println("    " + playerColor + player + reset + " : " + chosenGodCards.get(player));

        }
    }

    public void print (Map<String, ArrayList<Coordinates>> occupiedCells, Set<Coordinates> possibleDstBuilder1,
                       Set<Coordinates> possibleDstBuilder2, int chosenBuilderNumber) {
        int i;
        printHorizontalIndexes();
        printFirstLine();
        for (i=0; i < Cli.mapDimension - 1; i++) {
            printContents(i, occupiedCells, possibleDstBuilder1, possibleDstBuilder2, chosenBuilderNumber);
            printCentralLine();
        }
        printContents(i, occupiedCells, possibleDstBuilder1, possibleDstBuilder2, chosenBuilderNumber);
        printLastLine();
        printChosenGodCards();
    }

}