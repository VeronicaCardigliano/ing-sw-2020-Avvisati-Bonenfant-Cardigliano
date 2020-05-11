package it.polimi.ingsw.client;

import it.polimi.ingsw.server.model.gameMap.Builder;
import it.polimi.ingsw.server.model.gameMap.Coordinates;
import it.polimi.ingsw.server.view.*;

import java.io.InputStream;
import java.util.*;

public class Cli extends ViewObservable implements View, BuilderPossibleMoveObserver, BuilderPossibleBuildObserver,
        ColorAssignmentObserver, ErrorsObserver, BuildersPlacedObserver, PlayerLoseObserver, EndGameObserver,
        BuilderBuiltObserver, BuilderMovementObserver, GodChoiceObserver, PlayerAddedObserver, PlayerTurnObserver {

    public final static int mapDimension = 5;
    private static CliGameMap cliGameMap;
    private Scanner input;

    //these attributes are passed to the NetworkHandler through notifies
    protected static int numPlayers;
    //Set of GodCards already chosen by a player
    private static Map<String,String> chosenGodCards = new HashMap<>();
    //Map of each player and the color he has chosen
    private static Map<String,String> chosenColorsForPlayer = new HashMap<>();
    //Map of each player and the coordinates of his builders
    private static Map<String, ArrayList<Coordinates>> occupiedCells = new HashMap<>();
    private static Set<String> chosenColors = new HashSet<>();

    //not static attributes which change for each player/client
    private String nickname;

    //Sets of Coordinates to be saved in case you need to ask again for a build/move after a failed one
    Set<Coordinates> possibleDstBuilder1;
    Set<Coordinates> possibleDstBuilder2;
    Set<Coordinates> possibleDstBuilder1forDome;
    Set<Coordinates> possibleDstBuilder2forDome;

    //The destination of the move step is saved since the build'll have that source
    private int chosenBuilderNum = 0, x_dstMove, y_dstMove;


    public Cli(InputStream source) {
        this.input = new Scanner(source);
        cliGameMap = new CliGameMap();
    }

    /*
    //method created just for testing purpose
    public void main () {
        askNumberOfPlayers();
        askForNewPlayer();
        askForNewPlayer();
        askForNewPlayer();
        chooseGodCard ((new Model()).getGodDescriptions(), chosenGodCards);
        chooseBuilderColor(chosenColors);
        chooseNextStep();
    }
    */

    /**
     * This list of set methods is used by tests
     */

    //protected void setChosenCard (String chosenGodCard) {
    //    chosenGodCards.add(chosenGodCard);}

    //protected void setNumPlayers (int num) {
    //   numPlayers = num;}

    protected void setChosenColor (String player, String chosenColor) {
        chosenColorsForPlayer.put(player, chosenColor);
    }

    /**
     * Used by cliGameMap to print the builders in the correct color
     * @param player the player whose color you want to have
     * @return String of the color
     */
    protected static String getColor (String player) {
        return chosenColorsForPlayer.get(player);
    }

    /**
     * The method put will replace the value of an existing key and will create it if doesn't exist
     * In this case the keySet is the list of players and values are their builders' coordinates
     */
    protected void setOccupiedCells (String player, ArrayList<Coordinates> cells) {
        occupiedCells.put(player, cells);
    }

    protected Map<String, ArrayList<Coordinates>> getOccupiedCells () {
        return occupiedCells;
    }

    /**
     * Asks for the number of players of the match, just the first player has to set it
     */
    @Override
    public void askNumberOfPlayers() {
        System.out.println("Insert the number of players ");
        numPlayers = Integer.parseInt(input.nextLine());
        notifyNumberOfPlayers(Integer.parseInt(input.nextLine()));
    }

    /**
     * Asks to insert player's name and birthday and notifies it to the Controller
     */
    @Override
    public void askNickAndDate() {
        System.out.println("Insert player name: ");
        nickname = input.nextLine();
        System.out.println("Insert birthday date in the form \"yyyy.MM.dd\" ");
        String birthday = input.nextLine();

        notifyNewPlayer(nickname, birthday);
    }

    /**
     * Asks to choose a GodCard from the still available ones
     * @param godDescriptions is a Map with God Names as key and descriptions as values
     * @param chosenGodCards is a Set of the godCards name already chosen
     */
    @Override
    public void askGodCard(Map<String, String> godDescriptions, Set<String> chosenGodCards) {
        //this.chosenGodCards = chosenGodCards;
        for (String s : godDescriptions.keySet()) {
            boolean alreadyUsed = false;
            for (String x : chosenGodCards) {
                if (s.equals(x)) {
                    alreadyUsed = true;
                    break;
                }
            }
            if (!alreadyUsed) {
                System.out.println(s);
                //the second element of godDescriptions is the description, the key is the godName
                System.out.println(godDescriptions.get(s));
            }
        }
        System.out.println("Select your GodCard from the available ones");
        String chosenCard = input.nextLine();
        notifyGodCardChoice(nickname, chosenCard);
    }

    /**
     * Asks for the color of builders
     * @param chosenColors Set of the already chosen Colors
     */
    @Override
    public void askBuilderColor(Set<String> chosenColors) {
        System.out.println("Available builder colors: ");
        //prints the colors only if they're still available
        for (Builder.BuilderColor color : Builder.BuilderColor.values()) {
            boolean alreadyUsed = false;
            for (String alreadyChosen : chosenColors) {
                if (alreadyChosen.equals(color.toString()))
                    alreadyUsed = true;
            }
            if (!alreadyUsed)
                System.out.println(color.name().toUpperCase() + " ");
        }
        System.out.println("Select a color for your Builders");
        String chosenColor = input.nextLine();
        notifyColorChoice(nickname, chosenColor.toUpperCase());
    }

    @Override
    public void placeBuilders(String nickname) {
        int x,y;
        System.out.println("Insert the coordinates of cells in which you want to place your builders \n X1: ");
        x = Integer.parseInt(input.nextLine());
        System.out.println("Y1: ");
        y = Integer.parseInt(input.nextLine());
        Coordinates selectedCellBuilder1 = new Coordinates(x,y);

        System.out.println("X2: ");
        x = Integer.parseInt(input.nextLine());
        System.out.println("Y2: ");
        y = Integer.parseInt(input.nextLine());
        Coordinates selectedCellBuilder2 = new Coordinates(x,y);

        notifySetupBuilders(nickname, selectedCellBuilder1, selectedCellBuilder2);
    }

    /**
     * Asks the player to decide the next step if he can both move or build
     */
    @Override
    public void chooseNextStep() {
        System.out.println("Insert MOVE to move or BUILD to build ");
        String step = input.nextLine().toUpperCase();

        while (!(step.equals("MOVE") || step.equals("BUILD"))) {
            System.out.println("Wrong insertion. Insert \"MOVE\" to move or \"BUILD\" to build ");
            step = input.nextLine().toUpperCase();
        }

        notifyStepChoice(nickname, step);
    }

    /**
     * This method asks the player to decide where and what to build choosing from the possible destinations
     */
    @Override
    public void build() {
        String buildType;
        int x_dst, y_dst;
        Set<Coordinates> possibleDstBuilder;
        boolean buildDome = false;

        System.out.println("Select what yuo want to build: insert 'D' for dome or 'B' for building ");
        buildType = input.nextLine().toUpperCase();

        while (!(buildType.equals("D") || buildType.equals("B"))) {
            System.out.println("Invalid insertion. Select what yuo want to build: insert 'D' for dome or 'B' for building ");
            buildType = input.nextLine().toUpperCase();
        }

        if (buildType.equals("D")) {
            buildDome = true;
            if (chosenBuilderNum == '1')
                possibleDstBuilder = possibleDstBuilder1forDome;
            else
                possibleDstBuilder = possibleDstBuilder2forDome;
        }
        else {
            if (chosenBuilderNum == '1')
                possibleDstBuilder = possibleDstBuilder1;
            else
                possibleDstBuilder = possibleDstBuilder2;
        }

        if (chosenBuilderNum == '1')
            cliGameMap.print(occupiedCells, possibleDstBuilder, null, chosenBuilderNum);
        else
            cliGameMap.print(occupiedCells, null, possibleDstBuilder, chosenBuilderNum);

        System.out.println("Insert the coordinates of where you want to build ");
        System.out.println("X: ");
        x_dst = Integer.parseInt(input.nextLine());
        System.out.println("Y: ");
        y_dst = Integer.parseInt(input.nextLine());

        while (!possibleDstBuilder.contains(new Coordinates(x_dst, y_dst))){
            System.out.println("Invalid coordinates. Select a cell with a valid builder");
            System.out.println("X: ");
            x_dst = Integer.parseInt(input.nextLine());
            System.out.println("Y: ");
            y_dst = Integer.parseInt(input.nextLine());
        }

        notifyBuild(nickname, new Coordinates(x_dstMove,y_dstMove), new Coordinates(x_dst, y_dst), buildDome);
    }

    /**
     * This method asks the player to decide how to move choosing from the possible destinations
     */
    @Override
    public void move() {
        int x_src,y_src;
        Set<Coordinates> possibleDstBuilder;

        cliGameMap.print(occupiedCells, possibleDstBuilder1, possibleDstBuilder2, chosenBuilderNum);

        System.out.println("Insert the coordinates of the builder you want to use ");
        System.out.println("X: ");
        x_src = Integer.parseInt(input.nextLine());
        System.out.println("Y: ");
        y_src = Integer.parseInt(input.nextLine());

        //verifies that the selected cell contains a valid builder
        while (!((occupiedCells.get(nickname).get(0).getI() == x_src && occupiedCells.get(nickname).get(0).getJ() == y_src) ||
                (occupiedCells.get(nickname).get(1).getI() == x_src && occupiedCells.get(nickname).get(1).getJ() == y_src))){
            System.out.println("Invalid coordinates, select a cell with a valid builder");
            System.out.println("X: ");
            x_src = Integer.parseInt(input.nextLine());
            System.out.println("Y: ");
            y_src = Integer.parseInt(input.nextLine());
        }

        if (occupiedCells.get(nickname).get(0).getI() == x_src && occupiedCells.get(nickname).get(0).getJ() == y_src) {
            chosenBuilderNum = 1;
            possibleDstBuilder = possibleDstBuilder1;
        }
        else {
            chosenBuilderNum = 2;
            possibleDstBuilder = possibleDstBuilder2;
        }

        if (chosenBuilderNum == 1)
            cliGameMap.print(occupiedCells, possibleDstBuilder, null, chosenBuilderNum);
        else
            cliGameMap.print(occupiedCells, null, possibleDstBuilder, chosenBuilderNum);

        System.out.println("Insert the coordinates of where you want to move ");
        System.out.println("X: ");
        x_dstMove = Integer.parseInt(input.nextLine());
        System.out.println("Y: ");
        y_dstMove = Integer.parseInt(input.nextLine());

        //verifies that the selected cell contains a valid builder
        while (!possibleDstBuilder.contains(new Coordinates(x_dstMove, y_dstMove))){
            System.out.println("Invalid coordinates, select the coordinates with a valid builder");
            System.out.println("X: ");
            x_dstMove = Integer.parseInt(input.nextLine());
            System.out.println("Y: ");
            y_dstMove = Integer.parseInt(input.nextLine());
        }

        notifyMove(nickname, new Coordinates(x_src,y_src), new Coordinates(x_dstMove,y_dstMove));
    }

    /**
     * This update is called when builders have been correctly placed
     * @param positionBuilder1 coordinates of the builder 1
     * @param positionBuilder2 coordinates of the builder 2
     */
    @Override
    public void onBuildersPlacedUpdate(String nickname, Coordinates positionBuilder1, Coordinates positionBuilder2, boolean result) {
        if (!result) {
            System.out.println("Invalid builders placement.");
            placeBuilders(nickname);
        }
        else {
            ArrayList<Coordinates> selectedCells = new ArrayList<>();
            System.out.println("Builders positioned correctly.");
            selectedCells.add(positionBuilder1);
            selectedCells.add(positionBuilder2);

            //at key nickname, the builders are in order of insertion: builder1 is in position 0 and builder2 in pos 1,
            //in this way I can distinguish the two
            setOccupiedCells(nickname, selectedCells);
            cliGameMap.print(occupiedCells, null, null, chosenBuilderNum);
        }
    }

    @Override
    public void onEndGameUpdate(String winnerNickname) {
        System.out.println ("Player " + winnerNickname + " wins!!");
    }

    @Override
    public void onPlayerAdded(String nickname, boolean result) {
        if (!result) {
            System.out.println("Invalid insertion of player.");
            askNickAndDate();
        }
        else
            System.out.println("New player added: " +nickname);
    }

    @Override
    public void onWrongInsertionUpdate(String error) {
        System.out.println(error);
    }

    @Override
    public void onWrongNumberInsertion() {
        System.out.println("Invalid number insertion.");
        askNumberOfPlayers();
    }

    //Every player has to add to the static Map of colors, the new entry
    @Override
    public void onColorAssigned(String nickname, String color, boolean result) {
        if (!result) {
            System.out.println("Invalid insertion of color.");
            askBuilderColor(chosenColors);
        }
        else {
            System.out.println ("Color assigned correctly.");
            chosenColorsForPlayer.put(nickname, color);
        }
    }

    //Every player has to add to the static Map of colors, the new entry
    @Override
    public void onGodCardAssigned(String nickname, String godCard, boolean result) {
        System.out.println ("GodCard assigned correctly.");
        chosenGodCards.put(nickname, godCard);
    }

    @Override
    public void onLossUpdate(String currPlayer) {
        occupiedCells.remove(currPlayer);
        if (this.nickname.equals(currPlayer))
            System.out.println("YOU LOSE!");
        else
            System.out.println("Player " + currPlayer + " lost the game");
    }

    @Override
    public void updatePossibleBuildDst(Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2,
                                       Set<Coordinates> possibleDstBuilder1forDome, Set<Coordinates> possibleDstBuilder2forDome) {
        this.possibleDstBuilder1 = possibleDstBuilder1;
        this.possibleDstBuilder2 = possibleDstBuilder2;
        this.possibleDstBuilder1forDome = possibleDstBuilder1forDome;
        this.possibleDstBuilder2forDome = possibleDstBuilder2forDome;
        build();
    }

    @Override
    public void updatePossibleMoveDst(Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2) {
        this.possibleDstBuilder1 = possibleDstBuilder1;
        this.possibleDstBuilder2 = possibleDstBuilder2;
        move();
    }

    @Override
    public void onBuilderBuild(String nickname, Coordinates src, Coordinates dst, boolean dome, boolean result) {
        if (!result)
          build();
        else
          cliGameMap.modifyHeight(dst, dome);
    }

    @Override
    public void onBuilderMovement(String nickname, Coordinates src, Coordinates dst, boolean result) {
        if (!result)
            move();
        else {
            ArrayList<Coordinates> selectedCells = new ArrayList<>();
            if (Coordinates.equals(occupiedCells.get(nickname).get(0), dst)) {
                selectedCells.add(0, dst);
                selectedCells.add(1, occupiedCells.get(nickname).get(1));
            }
            else if (Coordinates.equals(occupiedCells.get(nickname).get(1), dst)) {
                selectedCells.add(0, occupiedCells.get(nickname).get(0));
                selectedCells.add(1, dst);
            }
            occupiedCells.put(nickname, selectedCells);
        }
    }

    @Override
    public void onPlayerTurn(String nickname) {
        System.out.println("Turn ended. Now playing: " + nickname);
    }

}
