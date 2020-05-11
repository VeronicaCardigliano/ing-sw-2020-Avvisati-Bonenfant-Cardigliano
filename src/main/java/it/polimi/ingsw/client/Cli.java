package it.polimi.ingsw.client;

import it.polimi.ingsw.server.model.gameMap.Builder;
import it.polimi.ingsw.server.model.gameMap.Coordinates;
import it.polimi.ingsw.server.view.*;

import java.io.InputStream;
import java.util.*;

public class Cli extends ViewObservable implements View, BuilderPossibleMoveObserver, BuilderPossibleBuildObserver,
        ErrorsObserver, BuildersPlacedObserver, PlayerLoseObserver, EndGameObserver, BuilderBuildObserver, BuilderMovementObserver {

    public final static int mapDimension = 5;
    private static GameMap gameMap;
    private Scanner input;

    //these attributes are passed to the NetworkHandler through notifies
    protected static int numPlayers;
    //Set of GodCards already chosen by a player
    private static Set<String> chosenGodCards = new HashSet<>();
    //Map of each player and the color he has chosen
    private static Map<String,String> chosenColors = new HashMap<>();
    //Map of each player and the coordinates of his builders
    private static Map<String, ArrayList<Coordinates>> occupiedCells = new HashMap<>();

    //not static attributes which change for each player/client
    private String nickname, birthday;
    private String step, chosenColor, chosenCard;

    //The destination of the move step is saved since the build'll have that source
    private int chosenBuilderNum = 0, x_dstMove, y_dstMove;


    public Cli(InputStream source) {
        this.input = new Scanner(source);
        gameMap = new GameMap();
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

    protected void setChosenCard (String chosenGodCard) {
        chosenGodCards.add(chosenGodCard);
    }

    protected void setNumPlayers (int num) {
        numPlayers = num;
    }

    protected void set (int num) {
        numPlayers = num;
    }

    protected void setChosenColor (String player, String chosenColor) {
        chosenColors.put(player, chosenColor);
    }

    protected static String getColor (String player) {
        return chosenColors.get(player);
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

    @Override
    public void askNumberOfPlayers() {
        System.out.println("Insert the number of players ");
        numPlayers = Integer.parseInt(input.nextLine());
        notifyNumberOfPlayers(Integer.parseInt(input.nextLine()));
    }

    @Override
    public void askForNewPlayer() {
        System.out.println("Insert Player name: ");
        nickname = input.nextLine();
        System.out.println("Insert Birthday date in the form \"yyyy.MM.dd\" ");
        birthday = input.nextLine();

        notifyNewPlayer(nickname, birthday);
    }

    @Override
    public void chooseGodCard(Map<String, String> godDescriptions, Set<String> chosenGodCards) {
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
        chosenCard = input.nextLine();
        //notifyGodCardChoice(nickname, input.nextLine());
    }

    @Override
    public void chooseBuilderColor(Map<String,String> chosenColors) {
        System.out.println("Available builder colors: ");
        //prints the colors only if they're still available
        for (Builder.BuilderColor color : Builder.BuilderColor.values()) {
            boolean alreadyUsed = false;
            for (String alreadyChosen : chosenColors.values()) {
                if (alreadyChosen.equals(color.toString()))
                    alreadyUsed = true;
            }
            if (!alreadyUsed)
                System.out.println(color.name().toUpperCase() + " ");
        }
        System.out.println("Select a color for your Builders");
        chosenColor = input.nextLine();
        notifyColorChoice(nickname, input.nextLine().toUpperCase());
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

    @Override
    public void chooseNextStep() {
        System.out.println("Insert M to move or B to build ");
        step = input.nextLine().toUpperCase();
        //notifyStepChoice(input.nextLine().toUpperCase());
    }

    @Override
    public void onBuildersPlacedUpdate(String nickname, Coordinates positionBuilder1, Coordinates positionBuilder2) {
        ArrayList<Coordinates> selectedCells = new ArrayList<>();
        System.out.println ("Builders positioned correctly");
        selectedCells.add(positionBuilder1);
        selectedCells.add(positionBuilder2);

        //at key nickname, the builders are in order of insertion: builder1 is in position 0 and builder2 in pos 1,
        //in this way I can distinguish the two
        setOccupiedCells(nickname, selectedCells);
        gameMap.print(occupiedCells, null, null, chosenBuilderNum);
    }

    @Override
    public void onEndGameUpdate(String winnerNickname) {
        System.out.println ("Player " + winnerNickname + " wins!!");
    }

    @Override
    public void onWrongInsertionUpdate(String player, String error) {
        if (nickname.equals(player))
            System.out.println(error);
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
    public void updatePossibleBuildDst(String nickname, Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2,
                                       Set<Coordinates> possibleDstBuilder1forDome, Set<Coordinates> possibleDstBuilder2forDome) {
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
            if (chosenBuilderNum == '0')
                possibleDstBuilder = possibleDstBuilder1forDome;
            else
                possibleDstBuilder = possibleDstBuilder2forDome;
        }
        else {
            if (chosenBuilderNum == '0')
                possibleDstBuilder = possibleDstBuilder1;
            else
                possibleDstBuilder = possibleDstBuilder2;
        }

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

        //notifyBuildChoice(nickname, new Coordinates(x_dstMove,y_dstMove), new Coordinates(x_dst, y_dst), buildDome);
    }

    @Override
    public void updatePossibleMoveDst(String nickname, Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2) {
        int x_src,y_src;
        Set<Coordinates> possibleDstBuilder;

        gameMap.print(occupiedCells, possibleDstBuilder1, possibleDstBuilder2, chosenBuilderNum);

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

        gameMap.print(occupiedCells, possibleDstBuilder1, possibleDstBuilder2, chosenBuilderNum);

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

        //notifyMoveChoice(nickname, new Coordinates(x_src,y_src), new Coordinates(x_dstMove,y_dstMove));
    }

    @Override
    public void onBuilderBuild(String nickname, Coordinates src, Coordinates dst, Boolean dome) {
        gameMap.modifyHeight(dst, dome);
        //TODO: refresh Map
    }

    @Override
    public void onBuilderMovement(String nickname, Coordinates src, Coordinates dst) {
        //TODO: refresh Map
    }
}
