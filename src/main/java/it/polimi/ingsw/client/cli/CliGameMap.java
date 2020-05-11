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

    private String possibleDst = yellow + " " + "\u25CC" + reset;
    private String dome = blue + "\u25CF" + reset;
    private String builder =  " " + "\u2692" + reset;

    private String verticalLine = green + "\u2502" + reset, verticalLeftSeparator = green + "\u251C" + reset,
            verticalRightSeparator = green + "\u2524" + reset, horizontalAboveSeparator = green + "\u252C" + reset,
            horizontalUnderSeparator = green + "\u2534" + reset,
            horizontalLine = green + "\u2500" + "\u2500" + "\u2500"+ "\u2500" + reset,
            leftAboveCorner = green + "\u250C" + reset, rightAboveCorner = green + "\u2510" + reset,
            leftUnderCorner = green + "\u2514" + reset, rightUnderCorner = green + "\u2518" + reset,
            centralSeparator = green + "\u253C" + reset;

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
        System.out.println("   " + leftAboveCorner + horizontalLine + horizontalAboveSeparator + horizontalLine +
                horizontalAboveSeparator + horizontalLine + horizontalAboveSeparator + horizontalLine + horizontalAboveSeparator +
                horizontalLine + rightAboveCorner);
    }

    private void printCentralLine() {
        System.out.println("   " + verticalLeftSeparator + horizontalLine + centralSeparator + horizontalLine + centralSeparator +
                horizontalLine + centralSeparator + horizontalLine + centralSeparator + horizontalLine +verticalRightSeparator);
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
                    switch (Cli.getColor(player).toUpperCase()){
                        case "MAGENTA":
                            builderColor = Color.ANSI_MAGENTA.escape();
                            break;
                        case "WHITE":
                            builderColor = "";
                            break;
                        case "LIGHT_BLUE":
                            builderColor = Color.ANSI_LIGHTBLUE.escape();
                            break;
                    }
                    line.append(verticalLine).append(builderColor).append(builder);
                    printed = true;
                }
            }
            //possibleDstBuilder sets are null if the match is not still in the Game state
            //if the player has chosen the builder to use, it'll show just the chosenBuilder possible destinations

            if (!printed && possibleDstBuilder1 != null && (chosenBuilderNumber == 0 || chosenBuilderNumber == 1)) {
                String x = controlIfPossibleDst(possibleDstBuilder1, coordinates);
                if (!x.equals("")) {
                    line.append(x);
                    printed = true;
                }
            }

            if (!printed && possibleDstBuilder2 != null && (chosenBuilderNumber == 0 || chosenBuilderNumber == 2)) {
                String x = controlIfPossibleDst(possibleDstBuilder2, coordinates);
                if (!x.equals("")) {
                    line.append(x);
                    printed = true;
                }
            }

            if (!printed)
                line.append(verticalLine).append("  ");

            height = heights.get(coordinates);
            if (height == -1)
                line.append(dome);
            else if (height == 0)
                line.append("  ");
            else
                line.append(height).append(" ");
        }
        System.out.print(line + verticalLine + "\n");
    }

    private String controlIfPossibleDst (Set<Coordinates> possibleDstSet, Coordinates coordinates) {
        String cell = "";
        for (Coordinates coord : possibleDstSet) {
            if (Coordinates.equals(coordinates, coord)){
                cell = verticalLine + possibleDst;
            }
        }
        return cell;
    }

    private void printLastLine() {
        System.out.println("   " + leftUnderCorner + horizontalLine + horizontalUnderSeparator + horizontalLine +
                horizontalUnderSeparator + horizontalLine + horizontalUnderSeparator + horizontalLine +
                horizontalUnderSeparator + horizontalLine + rightUnderCorner);
    }

    private void printChosenGodCards () {
        Map<String,String> chosenGodCards = Cli.getChosenGodCards();
        for (String player : chosenGodCards.keySet()) {
            System.out.println("    " + player + " : " + chosenGodCards.get(player));
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
