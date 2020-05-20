package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.View;
import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.gameMap.Builder;
import it.polimi.ingsw.server.model.gameMap.Coordinates;
import it.polimi.ingsw.server.view.*;

import java.io.InputStream;
import java.util.*;

public class Cli extends ViewObservable implements View, BuilderPossibleMoveObserver, BuilderPossibleBuildObserver,
        ColorAssignmentObserver, ErrorsObserver, BuildersPlacedObserver, PlayerLoseObserver, EndGameObserver,
        BuilderBuiltObserver, BuilderMovementObserver, GodChoiceObserver, PlayerAddedObserver, PlayerTurnObserver, StateObserver, ChosenStepObserver, StartPlayerSetObserver {

    public final static int mapDimension = 5;
    private static CliGameMap cliGameMap;
    private Scanner input;
    private String red = Color.ANSI_RED.escape();
    private String yellow = Color.ANSI_YELLOW.escape();

    //Attributes declared protected to be used by tests
    //Set of GodCards already chosen by a player
    protected static Map<String,String> chosenGodCardsForPlayer = new HashMap<>();
    //Map of each player and the color he has chosen
    protected static Map<String,String> chosenColorsForPlayer = new HashMap<>();
    //Map of each player and the coordinates of his builders
    protected static Map<String, ArrayList<Coordinates>> occupiedCells = new HashMap<>();
    private static Set<String> matchGodCards = new HashSet<>();
    private static Set<String> chosenColors = new HashSet<>();
    private static int validGodChoices;

    //not static attributes which change for each player/client
    private String nickname;

    //Sets of Coordinates to be saved in case you need to ask again for a build/move after a failed one
    private Set<Coordinates> possibleDstBuilder1;
    private Set<Coordinates> possibleDstBuilder2;
    private Set<Coordinates> possibleDstBuilder1forDome;
    private Set<Coordinates> possibleDstBuilder2forDome;

    //The destination of the move step is saved since the build'll have that source
    private Coordinates currentTurnBuilderPos;
    private int chosenBuilderNum = 0;


    public Cli(InputStream source) {
        this.input = new Scanner(source);
        cliGameMap = new CliGameMap();
        validGodChoices = 0;

        System.out.println("\n" +
                "░██████╗░█████╗░███╗░░██╗████████╗░█████╗░██████╗░██╗███╗░░██╗██╗\n" +
                "██╔════╝██╔══██╗████╗░██║╚══██╔══╝██╔══██╗██╔══██╗██║████╗░██║██║\n" +
                "╚█████╗░███████║██╔██╗██║░░░██║░░░██║░░██║██████╔╝██║██╔██╗██║██║\n" +
                "░╚═══██╗██╔══██║██║╚████║░░░██║░░░██║░░██║██╔══██╗██║██║╚████║██║\n" +
                "██████╔╝██║░░██║██║░╚███║░░░██║░░░╚█████╔╝██║░░██║██║██║░╚███║██║\n" +
                "╚═════╝░╚═╝░░╚═╝╚═╝░░╚══╝░░░╚═╝░░░░╚════╝░╚═╝░░╚═╝╚═╝╚═╝░░╚══╝╚═╝");
    }


    /**
     * This list of set methods is used by tests
     */

    protected void setNickname (String nickname) {
        this.nickname = nickname;
    }

    protected static void setChosenColor (String player, String chosenColor) {
        chosenColorsForPlayer.put(player, chosenColor.toUpperCase());
    }

    protected static void setOccupiedCells (String player, ArrayList<Coordinates> cells) {
        occupiedCells.put(player, cells);
    }

    protected static void setChosenGodCard (String player, String godCard) {
        chosenGodCardsForPlayer.put(player, godCard);
    }

    protected static Map<String, ArrayList<Coordinates>> getOccupiedCells () {
        return occupiedCells;
    }

    protected static Map<String, String> getChosenGodCards () {
        return chosenGodCardsForPlayer;
    }


    /**
     * Used by cliGameMap to print the builders in the correct color
     * @param player the player whose color you want to have
     * @return String of the color
     */
    protected static String getColor (String player) {
        return chosenColorsForPlayer.get(player);
    }

    private void checkLeaving(String string) {
        if (string.toUpperCase().equals("QUIT")) {
            notifyDisconnection(nickname);
        }
    }

    /**
     * @param input an integer expected
     * @return true if is not an integer and it has to be asked again
     */
    private boolean isNotInteger(String input) {
        try {
            Integer.parseInt(input);
            return false;
        } catch (final NumberFormatException e) {
            System.out.println(red + "ERROR:" + Color.RESET + " coordinates must be integers ");
            return true;
        }
    }

    /**
     * Asks for the number of players of the match, just the first player has to set it
     */
    @Override
    public void askNumberOfPlayers() {
        String inputString;
        System.out.println("Insert the number of players ");
        inputString = input.nextLine();

        while (isNotInteger(inputString)) {
            System.out.println("Insert the number of players ");
            inputString = input.nextLine();
        }

        notifyNumberOfPlayers(Integer.parseInt(inputString));
    }

    /**
     * Asks to insert player's name and birthday and notifies it to the Controller
     */
    @Override
    public void askNickAndDate() {
        System.out.println("Insert player name: ");
        this.nickname = input.nextLine();
        checkLeaving(nickname);

        while (this.nickname.equals("")) {
            System.out.println("Empty name not valid. Insert player name: ");
            this.nickname = input.nextLine();
            checkLeaving(nickname);
        }

        System.out.println("Insert birthday date in the form \"yyyy.MM.dd\" ");
        String birthday = input.nextLine();
        checkLeaving(birthday);

        notifyNewPlayer(nickname, birthday);
    }

    private void printAvailableGodCards(Map<String, String> godDescriptions, Set<String> chosenGodCards) {
        for (String s : godDescriptions.keySet())
            if (!chosenGodCards.contains(s)) {
                System.out.println(red + s + Color.RESET);
                //the second element of godDescriptions is the description, the key is the godName
                System.out.println(godDescriptions.get(s));
            }
    }

    @Override
    public void chooseMatchGodCards(int numOfPlayers, Map<String, String> godDescriptions) {
        String inputString;
        String crown = yellow + "\u2654" + Color.RESET;
        String chosenCard;
        System.out.println ("\n" + crown + "  You're the " + yellow + "Challenger" + Color.RESET + " of this match!  " + crown + "\n");
        System.out.println ("Choose " + numOfPlayers + " godCards for the match: ");

        while (validGodChoices < numOfPlayers) {
            printAvailableGodCards(godDescriptions, matchGodCards);

            System.out.print ("GodCard" + validGodChoices + ": ");
            inputString = input.nextLine();
            checkLeaving(inputString);
            chosenCard = inputString.substring(0,1).toUpperCase() + inputString.substring(1).toLowerCase();

            while(matchGodCards.contains(chosenCard) && !godDescriptions.containsKey(chosenCard)) {
                System.out.println("ERROR: Invalid Choice");

                inputString = input.nextLine();
                checkLeaving(inputString);
                chosenCard = inputString.substring(0,1).toUpperCase() + inputString.substring(1).toLowerCase();
            }


            matchGodCards.add(chosenCard);
            validGodChoices ++;
        }

        validGodChoices = 0;
        notifyMatchGodCardsChoice(nickname, matchGodCards);
    }

    /**
     * Asks to choose a GodCard from the still available ones
     * @param matchGodDescriptions is a Map with God Names as key and descriptions as values
     * @param chosenGodCards is a Set of the godCards name already chosen
     */
    @Override
    public void askGodCard(Map<String, String> matchGodDescriptions, Set<String> chosenGodCards) {
        String inputString;
        String chosenCard;

        System.out.println("\nSelect your GodCard from the available ones ");
        System.out.println("-------------------------------------------");

        printAvailableGodCards(matchGodDescriptions, chosenGodCards);

        inputString = input.nextLine();
        checkLeaving(inputString);
        chosenCard = inputString.substring(0,1).toUpperCase() + inputString.substring(1).toLowerCase();

        while (!matchGodDescriptions.containsKey(chosenCard) && !chosenGodCards.contains(chosenCard)) {

            System.out.println("ERROR: invalid choice");

            inputString = input.nextLine();
            checkLeaving(inputString);
            chosenCard = inputString.substring(0,1).toUpperCase() + inputString.substring(1).toLowerCase();
        }

        notifyGodCardChoice(nickname, chosenCard);
    }

    @Override
    public void chooseStartPlayer(Set<String> players) {
        String inputString;
        System.out.print ("List of players: ");
        for(String player: players) {
            System.out.print (player + " ");
        }
        System.out.println("\nAs Challenger, choose the StartPlayer of the match: ");
        inputString = input.nextLine();
        checkLeaving(inputString);
        while (!players.contains(inputString)) {
            System.out.println("ERROR: non-existent player, insert a valid one");
            inputString = input.nextLine();
            checkLeaving(inputString);
        }
        notifySetStartPlayer(nickname, inputString);
    }

    /**
     * Asks for the color of builders
     * @param chosenColors Set of the already chosen Colors
     */
    @Override
    public void askBuilderColor(Set<String> chosenColors) {
        System.out.println("\nSelect a color for your builders from the available ones: ");
        //prints the colors only if they're still available
        for (Builder.BuilderColor color : Builder.BuilderColor.values()) {
            boolean alreadyUsed = false;
            for (String alreadyChosen : chosenColors) {
                if (alreadyChosen.equals(color.toString()))
                    alreadyUsed = true;
            }
            if (!alreadyUsed) {
                String realColor = "";
                switch (color.name().toUpperCase()) {
                    case "MAGENTA":
                        realColor = Color.ANSI_MAGENTA.escape();
                        break;
                    case "LIGHT_BLUE":
                        realColor = Color.ANSI_LIGHTBLUE.escape();
                        break;
                }
                System.out.println(realColor + color.name().toUpperCase() + Color.RESET);
            }
        }
        String chosenColor = input.nextLine();
        checkLeaving(chosenColor);
        notifyColorChoice(nickname, chosenColor.toUpperCase());
    }

    @Override
    public void placeBuilders() {
        Coordinates selectedCellBuilder1, selectedCellBuilder2;
        cliGameMap.print(occupiedCells, null, null, chosenBuilderNum);

        System.out.println("\nInsert the coordinates of the cell in which you want to place your first builder: ");
        selectedCellBuilder1 = coordinatesInsertion();

        System.out.println("\nInsert the coordinates of the cell in which you want to place your second builder: ");
        selectedCellBuilder2 = coordinatesInsertion();

        notifySetupBuilders(nickname, selectedCellBuilder1, selectedCellBuilder2);
    }

    /**
     * Asks the player to decide the next step if he can both move or build
     */
    @Override
    public void chooseNextStep(Set<String> possibleSteps) {
        System.out.println("How do you want to continue?\nOptions:");

        for(String step : possibleSteps)
            System.out.println(step);

        String chosenStep = input.nextLine().toUpperCase();
        checkLeaving(chosenStep);

        while (!possibleSteps.contains(chosenStep)) {
            System.out.println("Wrong insertion.");
            chosenStep = input.nextLine().toUpperCase();
            checkLeaving(chosenStep);
        }

        notifyStepChoice(nickname, chosenStep);
    }

    private Coordinates coordinatesInsertion() {
        int x, y;
        String inputString;

        System.out.print("ROW: ");
        inputString = input.nextLine();
        checkLeaving(inputString);
        while (isNotInteger(inputString)) {
            System.out.print("ROW: ");
            inputString = input.nextLine();
            checkLeaving(inputString);
        }
        x = Integer.parseInt(inputString);

        System.out.print("COLUMN: ");
        inputString = input.nextLine();
        checkLeaving(inputString);
        while (isNotInteger(inputString)) {
            System.out.print("COLUMN: ");
            inputString = input.nextLine();
            checkLeaving(inputString);
        }
        y = Integer.parseInt(inputString);

        return new Coordinates(x,y);
    }

    private Coordinates chooseTurnBuilder () {
        Coordinates src;
        System.out.println("\nInsert the coordinates of the builder you want to use ");
        src = coordinatesInsertion();

        //verifies that the selected cell contains a valid builder
        while (!((Coordinates.equals(occupiedCells.get(nickname).get(0), src) &&
                (!possibleDstBuilder1.isEmpty() || !possibleDstBuilder1forDome.isEmpty())) ||
                (Coordinates.equals(occupiedCells.get(nickname).get(1), src) &&
                        (!possibleDstBuilder2.isEmpty() || !possibleDstBuilder2forDome.isEmpty())))) {

            System.out.println("\nInvalid coordinates, select a cell with a valid builder \n");
            src = coordinatesInsertion();
        }

        if (Coordinates.equals(occupiedCells.get(nickname).get(0), src))
            chosenBuilderNum = 1;
        else
            chosenBuilderNum = 2;
        return src;
    }

    /**
     * This method asks the player to decide where and what to build choosing from the possible destinations
     */
    @Override
    public void build() {
        String buildType = "B";
        Coordinates dst;
        Set<Coordinates> possibleDstBuilder;
        boolean buildDome = false;

        if (chosenBuilderNum == 0)
            currentTurnBuilderPos = chooseTurnBuilder();

        if ((!possibleDstBuilder1forDome.isEmpty() && chosenBuilderNum == 1) || (!possibleDstBuilder2forDome.isEmpty() &&
                chosenBuilderNum == 2)) {
            System.out.println("Select what you want to build: insert 'D' for dome or 'B' for building ");
            buildType = input.nextLine().toUpperCase();
            checkLeaving(buildType);

            while (!(buildType.equals("D") || buildType.equals("B"))) {
                System.out.println("Invalid insertion. Select what yuo want to build: insert 'D' for dome or 'B' for building ");
                buildType = input.nextLine().toUpperCase();
                checkLeaving(buildType);
            }
        }

        if (buildType.equals("D")) {
            buildDome = true;
            if (chosenBuilderNum == 1)
                possibleDstBuilder = possibleDstBuilder1forDome;
            else
                possibleDstBuilder = possibleDstBuilder2forDome;
        }
        else {
            if (chosenBuilderNum == 1)
                possibleDstBuilder = possibleDstBuilder1;
            else
                possibleDstBuilder = possibleDstBuilder2;
        }

        if (chosenBuilderNum == 1)
            cliGameMap.print(occupiedCells, possibleDstBuilder, null, chosenBuilderNum);
        else
            cliGameMap.print(occupiedCells, null, possibleDstBuilder, chosenBuilderNum);

        System.out.println("Insert the coordinates of where you want to build ");
        dst = coordinatesInsertion();

        while (!possibleDstBuilder.contains(dst)){
            System.out.println("Invalid coordinates. Select a cell from the selected ones");
            dst = coordinatesInsertion();
        }

        notifyBuild(nickname, currentTurnBuilderPos, dst, buildDome);
    }

    /**
     * This method asks the player to decide how to move choosing from the possible destinations
     */
    @Override
    public void move() {
        Set<Coordinates> possibleDstBuilder;

        cliGameMap.print(occupiedCells, possibleDstBuilder1, possibleDstBuilder2, chosenBuilderNum);

        if (chosenBuilderNum == 0)
            currentTurnBuilderPos = chooseTurnBuilder();

        if (chosenBuilderNum == 1) {
            possibleDstBuilder = possibleDstBuilder1;
            cliGameMap.print(occupiedCells, possibleDstBuilder, null, chosenBuilderNum);
        }
        else {
            possibleDstBuilder = possibleDstBuilder2;
            cliGameMap.print(occupiedCells, null, possibleDstBuilder, chosenBuilderNum);
        }

        System.out.println("Insert the coordinates of where you want to move ");
        Coordinates dstMove = coordinatesInsertion();

        //verifies that the selected cell contains a valid builder
        while (!possibleDstBuilder.contains(dstMove)){
            System.out.println("Invalid coordinates. Select a cell from the available ones");
            dstMove = coordinatesInsertion();
        }

        notifyMove(nickname, currentTurnBuilderPos, dstMove);
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
            //placeBuilders();
        }
        else {
            ArrayList<Coordinates> selectedCells = new ArrayList<>();
            if (this.nickname.equals(nickname))
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

    /**
     * Update of a generic error
     * @param error string to be printed
     */
    @Override
    public void onWrongInsertionUpdate(String error) {
        System.out.println(error);
    }

    /**
     * Update after a wrong insertion of the number of players
     */
    @Override
    public void onWrongNumberInsertion() {
        System.out.println("Invalid number insertion.");
    }

    /**
     * Update after attempted color assignment
     * @param color color String
     * @param result is true if the assignment was successful, then each player
     * has to add to the static Map of colors the new entry
     */
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

    /**
     * Update after attempted color assignment
     * @param godCard chosen godCard
     * @param result is true if the assignment was successful, then each player
     * has to add to the static Map of godCards the new entry
     */
    @Override
    public void onGodCardAssigned(String nickname, String godCard, boolean result) {
        if (!result) {
            System.out.println("Invalid insertion of godCard.");
            //askGodCard(godDescriptions, chosenGodCards);
        }
        else {
            if (this.nickname.equals(nickname))
                System.out.println ("GodCard assigned correctly.");
            setChosenGodCard(nickname, godCard);
        }
    }

    @Override
    public void onMatchGodCardsAssigned (String nickname, Set<String> godCardsToUse, boolean result) {
        if (result)
            System.out.println ("GodCards correctly chosen. Wait the other players to choose theirs.");
        else
            System.out.println (red + "\nERROR:" + Color.RESET + " wrong godCards choice.");
    }

    /**
     * Sends a message of defeat
     * @param currPlayer is the player who's currently playing and who has lost
     */
    @Override
    public void onLossUpdate(String currPlayer) {
        occupiedCells.remove(currPlayer);
        if (this.nickname.equals(currPlayer))
            System.out.println("YOU LOSE!");
        else
            System.out.println("Player " + currPlayer + " lost the game");
    }

    /**
     * Updates the sets of the possible destinations for a normal build and for the build of dome
     */
    @Override
    public void updatePossibleBuildDst(Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2,
                                       Set<Coordinates> possibleDstBuilder1forDome, Set<Coordinates> possibleDstBuilder2forDome) {
        this.possibleDstBuilder1 = possibleDstBuilder1;
        this.possibleDstBuilder2 = possibleDstBuilder2;
        this.possibleDstBuilder1forDome = possibleDstBuilder1forDome;
        this.possibleDstBuilder2forDome = possibleDstBuilder2forDome;
        build();
    }

    /**
     * Updates the sets of the possible destinations for a move
     */
    @Override
    public void updatePossibleMoveDst(Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2) {
        this.possibleDstBuilder1 = possibleDstBuilder1;
        this.possibleDstBuilder2 = possibleDstBuilder2;
        move();
    }

    /**
     * Update after attempted build
     * @param nickname of who built
     * @param src cell coordinates
     * @param dst cell coordinates
     * @param dome true if there's a dome construction
     * @param result true if build wad successful
     */
    @Override
    public void onBuilderBuild(String nickname, Coordinates src, Coordinates dst, boolean dome, boolean result) {
        if (!result) {
            System.out.println (red + "\nERROR:" + Color.RESET + " wrong Build.");
            build();
        }
        else {
            cliGameMap.modifyHeight(dst, dome);
            cliGameMap.print(occupiedCells, null, null, chosenBuilderNum);
        }
    }

    /**
     * Update after attempted move
     * @param nickname of who moved
     * @param src cell coordinates
     * @param dst cell coordinates
     * @param result if move was successful
     */
    @Override
    public void onBuilderMovement(String nickname, Coordinates src, Coordinates dst, boolean result) {
        if (!result) {
            System.out.println (red + "\nERROR:" + Color.RESET + " wrong Move.");
            move();
        }
        else {
            ArrayList<Coordinates> selectedCells = new ArrayList<>();
            if (Coordinates.equals(occupiedCells.get(nickname).get(0), src)) {
                selectedCells.add(0, dst);
                selectedCells.add(1, occupiedCells.get(nickname).get(1));
            }
            else if (Coordinates.equals(occupiedCells.get(nickname).get(1), src)) {
                selectedCells.add(0, occupiedCells.get(nickname).get(0));
                selectedCells.add(1, dst);
            }
            currentTurnBuilderPos = dst;
            occupiedCells.put(nickname, selectedCells);
            cliGameMap.print(occupiedCells, null, null, chosenBuilderNum);
        }
    }

    @Override
    public void onBuilderPushed(String nickname, Coordinates src, Coordinates dst) {
        if (occupiedCells.get(nickname).get(0).equals(src)) {
            occupiedCells.get(nickname).remove(0);
            occupiedCells.get(nickname).add(0, dst);
        }
        else if(occupiedCells.get(nickname).get(1).equals(src)) {
            occupiedCells.get(nickname).remove(1);
            occupiedCells.get(nickname).add(1, dst);
        }
    }

    /**
     * @param nickname of the new player who's playing, starting a new turn
     */
    @Override
    public void onPlayerTurn(String nickname) {
        if (!this.nickname.equals(nickname))
            System.out.println("Turn ended. Now playing: " + nickname);
        chosenBuilderNum = 0;
    }

    /**
     * Notify players after a state change
     * @param currState is the actual state of the match
     */
    @Override
    public void onStateUpdate(Model.State currState) {
        System.out.println("State changed: " + currState.toString());
    }

    /**
     * @param nickname of the player who's choosing his next step
     * @param step is the step of the turn (move or build)
     * @param result true if the choice of the step was successful
     */
    @Override
    public void onChosenStep(String nickname, String step, boolean result) {
        if(result)
            System.out.println(nickname + " chose " + step);
        else
            System.out.println(red + "\nERROR:"+ Color.RESET + " wrong step.");
    }


    @Override
    public void onStartPlayerSet(String nickname, boolean result) {
        if(result)
            System.out.println("The starting player is" + nickname);
        else
            System.out.println(red + "\nERROR:"+ Color.RESET + " could not set starting player.");
    }
}
