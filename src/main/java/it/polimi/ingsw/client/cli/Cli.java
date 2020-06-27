package it.polimi.ingsw.client.cli;


import it.polimi.ingsw.client.View;
import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.gameMap.Coordinates;

import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.ingsw.client.cli.Color.returnColor;


/**
 * Console based User Interface.
 */
public class Cli extends View{

    private final Printer printer = new Printer(System.out);
    private boolean quit = false;

    private ArrayList<Coordinates> chosenBuilderPositions = new ArrayList<>();
    private ArrayList<String> inputOptions;
    public static final String red = Color.ANSI_RED.escape();
    private static final String crownSymbol = "\u2654";
    private Set<String> matchGodCards = new HashSet<>();

    private Map<String, String> allGodCards;

    private Scanner in;


    public Cli() {
        super();
        in = new Scanner(System.in);
        gameMap = new CliGameMap();
        printer.print();
    }

    private void addBuilderPosition(Coordinates coord) {
        chosenBuilderPositions.add(coord);
    }

    private ArrayList<Coordinates> getChosenBuilderPositions() {
        return chosenBuilderPositions;
    }

    private int parseInteger(String input) {
        int result = 0;
        try {
            result = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            printer.setInfoMessage("You have to type a number.");
            printer.print();
            result = parseInteger();
        }

        return result;
    }

    private int parseInteger() {
        boolean ok = false;
        int result = 0;

        while(!ok) {
            try {
                result = in.nextInt();
                in.nextLine();
                ok = true;
            } catch (InputMismatchException e) {
                printer.setInfoMessage("You have to type a number.");
                printer.print();
                in.nextLine();
            }
        }

        return result;
    }

    private void addMatchGodCard(String godCard) throws Exception{
        if(!matchGodCards.contains(godCard))
            matchGodCards.add(godCard);
        else
            throw new Exception("GodCard already chosen!");
    }

    private Set<String> getMatchGodCards() { return Set.copyOf(matchGodCards);}

    @Override
    public  void run() {
        printer.erase();
        printer.printTitle();

        String input = "";
        printer.setAskMessage("Server to join: ");
        printer.print();
        in = new Scanner(System.in);
        setState(ViewState.CONNECTION);

        quit = false;

        while(!quit) {

            input = in.nextLine();

            if (!input.toLowerCase().equals("quit")) {

                Scanner parser = new Scanner(input);

                try {
                    switch (getState()) {
                        case CONNECTION:
                            String ip = parser.nextLine();
                            printer.setAskMessage("port: ");
                            printer.print();
                            int port = parseInteger();

                            //in.nextLine();

                            notifyConnection(ip, port);
                            break;

                        case NUMPLAYERS:
                            setNumberOfPlayers(parseInteger(input));
                            synchronized (this) {
                                notifyNumberOfPlayers(getNumberOfPlayers());
                                setState(ViewState.WAITING);
                            }
                            break;
                        case NICKDATE:
                            setNickname(parser.nextLine());
                            printer.erase();
                            printer.setAskMessage("Birth Date (yyyy.mm.dd): ");
                            printer.print();
                            setDate(in.nextLine());
                            printer.erase();

                            synchronized (this) {
                                notifyNewPlayer(getNickname(), getDate());
                                setState(ViewState.WAITING);
                            }
                            break;
                        case MATCHGODS:
                            while (getMatchGodCards().size() < getNumberOfPlayers()) {
                                if (parser.hasNext()) {
                                    addMatchGodCard(getOption(parseInteger(input)));
                                    parser.nextLine(); //to read scanner buffer
                                } else {
                                    chooseMatchGodCards(getNumberOfPlayers(), allGodCards);
                                    addMatchGodCard(getOption(parseInteger()));
                                    //in.nextLine(); //to read new line character
                                }


                                //if(getMatchGodCards().size() == getNumberOfPlayers())
                                //chooseMatchGodCards(getNumberOfPlayers(), allGodCards);
                            }
                            printer.erase();
                            printer.print();

                            synchronized (this) {
                                notifyMatchGodCardsChoice(getNickname(), getMatchGodCards());
                                setState(ViewState.WAITING);
                            }
                            break;
                        case STARTPLAYER:
                            synchronized (this) {
                                notifySetStartPlayer(getNickname(), getOption(parseInteger(input)));
                                setState(ViewState.WAITING);
                            }
                            break;
                        case PLAYERGOD:
                            synchronized (this) {
                                notifyGodCardChoice(getNickname(), getOption(parseInteger(input)));
                                setState(ViewState.WAITING);
                            }
                            break;

                        case BUILDERCOLOR:
                            synchronized (this) {
                                notifyColorChoice(getNickname(), getOption(parseInteger(input)));
                                setState(ViewState.WAITING);
                            }
                            break;

                        case BUILDERPLACEMENT:
                            boolean askForRow = false;
                            int row;
                            int column;

                            printer.erase();

                            while (getChosenBuilderPositions().size() < 2) {
                                if (parser.hasNext()) {
                                    row = parseInteger(input);
                                    parser.nextLine(); //to free Scanner buffer
                                }
                                else {
                                    row = parseInteger();
                                }

                                printer.setAskMessage("COLUMN: ");
                                printer.print();
                                column = parseInteger();
                                addBuilderPosition(new Coordinates(row, column));

                                //in.nextLine(); //to read new line character

                                if (getChosenBuilderPositions().size() < 2) {
                                    printer.setAskMessage("Choose builder number " + (getChosenBuilderPositions().size() + 1) + "\nROW: ");
                                    printer.print();
                                }
                            }

                            synchronized (this) {
                                notifySetupBuilders(getNickname(), getChosenBuilderPositions().get(0), getChosenBuilderPositions().get(1));
                                setState(ViewState.WAITING);
                            }
                            break;
                        case STEP:
                            synchronized (this) {
                                notifyStepChoice(getNickname(), getOption(parseInteger(input)));
                                setState(ViewState.WAITING);
                            }
                            break;

                        case MOVE:
                            move();
                            break;
                        case BUILD:
                            build();
                            break;

                        case WAITING:
                            printer.setInfoMessage("Waiting from Server.\nYou entered: " + parser.nextLine());
                            printer.print();
                            break;

                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    in.nextLine();
                } catch (InvalidOptionException e) {
                    printer.setInfoMessage(e.getMessage());
                    printer.print();

                } catch (NoSuchElementException ignored) {

                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else {
                quit = true;
                notifyDisconnection();

            }
        }

        System.out.println("You exited the game.");
        System.exit(0);
    }

    private Coordinates coordinatesInsertion() {
        int row;
        int column;

        //asks for row
        printer.erase();
        printer.setAskMessage("ROW: ");
        printer.print();
        row = parseInteger();

        //ask
        printer.setAskMessage("COLUMN: ");
        printer.print();
        column = parseInteger();

        //in.nextLine(); //to read new line character

        return new Coordinates(row, column);
    }

    private Coordinates chooseTurnBuilder () {
        gameMap.setPossibleDst(possibleDstBuilder1, possibleDstBuilder2);
        gameMap.setChosenBuilderNum(0);
        printer.setGameMapString(gameMap.toString());

        Coordinates src;
        printer.setInfoMessage("Insert the coordinates of the builder you want to use ");
        printer.print();
        src = coordinatesInsertion();

        //verifies that the selected cell contains a valid builder
        while (!((Coordinates.equals(gameMap.getOccupiedCells().get(getNickname()).get(0), src) &&
                (!possibleDstBuilder1.isEmpty() || !possibleDstBuilder1forDome.isEmpty())) ||
                (Coordinates.equals(gameMap.getOccupiedCells().get(getNickname()).get(1), src) &&
                        (!possibleDstBuilder2.isEmpty() || !possibleDstBuilder2forDome.isEmpty())))) {


            printer.setInfoMessage("Invalid coordinates, select a cell with a valid builder");
            src = coordinatesInsertion();
        }

        if (Coordinates.equals(gameMap.getOccupiedCells().get(getNickname()).get(0), src))
            gameMap.setChosenBuilderNum(1);
        else
            gameMap.setChosenBuilderNum(2);
        return src;
    }


    private String displayMap(Map<String, String> map) {
        StringBuilder result = new StringBuilder();
        inputOptions = new ArrayList<>();
        int i = -1;

        for(String godName : map.keySet()) {
            i++;
            result.append(i).append(") ").append(red).append(godName).append(Color.RESET).append(": ").append(map.get(godName)).append("\n");
            inputOptions.add(i, godName);
        }

        return result.toString();

    }


    private String displaySet(Set<String> set) {
        StringBuilder result = new StringBuilder();
        inputOptions = new ArrayList<>();
        int i = -1;

        String realColor;

        for(String element : set) {
            i++;

            switch (element) {
                case "MAGENTA":
                    realColor = Color.ANSI_MAGENTA.escape();
                    break;
                case "LIGHT_BLUE":
                    realColor = Color.ANSI_LIGHTBLUE.escape();
                    break;
                default:
                    realColor = "";
                    break;
            }

            result.append(i).append(") ").append(realColor).append(element).append(Color.RESET).append("\n");
            inputOptions.add(i, element);
        }


        return result.toString();
    }


    private String displayPlayerCards(Map<String, String> chosenGodCardsForPlayer, Map<String, String> chosenColors) {
        StringBuilder result = new StringBuilder();

        for (String player : chosenGodCardsForPlayer.keySet()) {

            String playerColor = (chosenColors.get(player).toUpperCase());

            result.append("    ").append(returnColor(playerColor)).append(player).append(Color.RESET).append(" : ").append(chosenGodCardsForPlayer.get(player)).append("\n");

        }

        return result.toString();
    }

    private String getOption(int choice) throws InvalidOptionException{

        if(choice < 0 || choice >= inputOptions.size())
            throw new InvalidOptionException("Invalid Choice.");

        return inputOptions.get(choice);
    }

    //override View methods

    @Override
    public synchronized void askNumberOfPlayers() {
        super.askNumberOfPlayers();

        printer.setAskMessage("Number Of Players: ");
        printer.print();
    }

    @Override
    public synchronized void askNickAndDate() {
        super.askNickAndDate();

        printer.setAskMessage("Nickname: ");
        printer.print();
    }

    @Override
    public void chooseMatchGodCards(int numOfPlayers, Map<String, String> godDescriptionsParam) {
        super.chooseMatchGodCards(numOfPlayers, godDescriptionsParam);

        this.allGodCards = godDescriptionsParam;
        this.inputOptions = new ArrayList<>();

        Map<String, String> availableGods = new HashMap<>();
        for(String key : godDescriptionsParam.keySet())
            if(!getMatchGodCards().contains(key))
                availableGods.put(key, godDescriptionsParam.get(key));


        String crown = Color.ANSI_YELLOW.escape() + crownSymbol + Color.RESET;
        printer.setInfoMessage("\n" + crown + "  You're the " + Color.ANSI_YELLOW.escape() + "Challenger" + Color.RESET + " of this match!  " + crown + "\n" +
                "Choose " + (numOfPlayers - getMatchGodCards().size()) + " godCards for the match: \n");
        printer.setChoiceList(displayMap(availableGods));
        printer.setAskMessage("Choose " + getNumberOfPlayers() + " cards for the game.\nChoice: ");
        printer.print();

    }

    @Override
    public void chooseStartPlayer(Set<String> players) {
        super.chooseStartPlayer(players);

        inputOptions = new ArrayList<>();
        inputOptions.addAll(players);
        printer.setInfoMessage(null);
        printer.setChoiceList(displaySet(players));
        printer.setAskMessage("Choice: ");
        printer.print();
    }

    @Override
    public void askGodCard(Map<String, String> godDescriptions, Set<String> chosenCards) {
        super.askGodCard(godDescriptions, chosenCards);

        Map<String, String> availableGods = new HashMap<>();
        inputOptions = new ArrayList<>();


        for(String godName : godDescriptions.keySet().stream().filter(godName -> !chosenCards.contains(godName)).collect(Collectors.toSet())) {
            availableGods.put(godName, godDescriptions.get(godName));
            inputOptions.add(godName);
        }

        printer.setChoiceList(displayMap(availableGods));
        printer.setAskMessage("Choice: ");
        printer.print();
    }

    @Override
    public void askBuilderColor(Set<String> chosenColors) {
        super.askBuilderColor(chosenColors);

        Set<String> allColors = new HashSet<>(Set.of("MAGENTA", "WHITE", "LIGHT_BLUE"));
        allColors.removeAll(chosenColors);

        setState(ViewState.BUILDERCOLOR);
        inputOptions = new ArrayList<>();
        inputOptions.addAll(allColors);
        printer.setChoiceList(displaySet(allColors));
        printer.setAskMessage("Choice: ");
        printer.print();
    }

    @Override
    public void placeBuilders() {
        super.placeBuilders();

        printer.setGameMapString(gameMap.toString());
        printer.setPlayersList(displayPlayerCards(getChosenGodCardsForPlayer(), getChosenColorsForPlayer()));
        printer.setChoiceList(null);
        printer.setAskMessage("Choose your first builder.\nROW: ");
        printer.print();
    }

    @Override
    public void chooseNextStep(Set<String> possibleSteps) {
        super.chooseNextStep(possibleSteps);

        inputOptions = new ArrayList<>();

        inputOptions.addAll(possibleSteps);

        printer.setChoiceList(displaySet(possibleSteps));
        printer.setAskMessage("Next Step: ");
        printer.print();

        printer.erase();
    }

    @Override
    public void onBuilderBuild(String nickname, Coordinates src, Coordinates dst, boolean dome, boolean result) {
        super.onBuilderBuild(nickname, src, dst, dome, result);

        if(!result) {
            printer.setInfoMessage("Build failed");
            setState(ViewState.BUILD);
        }
        else {
            gameMap.setPossibleDst(null, null);
            printer.setGameMapString(gameMap.toString());
            printer.setPlayersList(displayPlayerCards(getChosenGodCardsForPlayer(), getChosenColorsForPlayer()));

            printer.setChoiceList(null);
            printer.setAskMessage(null);
        }

        printer.print();
    }

    @Override
    public void onBuilderMovement(String nickname, Coordinates src, Coordinates dst, boolean result) {
        super.onBuilderMovement(nickname, src, dst, result);

        if(!result) {
            printer.setInfoMessage("Move failed");
            setState(ViewState.MOVE);
        }
        else {
            gameMap.setPossibleDst(null, null);
            printer.setGameMapString(gameMap.toString());
            printer.setPlayersList(displayPlayerCards(getChosenGodCardsForPlayer(), getChosenColorsForPlayer()));

            printer.setChoiceList(null);
            printer.setAskMessage(null);
        }

        printer.print();

    }

    @Override
    public void onBuildersPlacedUpdate(String nickname, Coordinates positionBuilder1, Coordinates positionBuilder2, boolean result) {
        super.onBuildersPlacedUpdate(nickname, positionBuilder1, positionBuilder2, result);


        if(!result) {
            printer.setInfoMessage("Builders placement failed");
            printer.setGameMapString(gameMap.toString());
            printer.setPlayersList(displayPlayerCards(getChosenGodCardsForPlayer(), getChosenColorsForPlayer()));

            printer.setAskMessage("ROW: ");
            printer.print();
            setState(ViewState.BUILDERPLACEMENT);
        }
        else {
            printer.erase();
            printer.setInfoMessage(nickname + "'s builders placed!");
            printer.setGameMapString(gameMap.toString());
            printer.setPlayersList(displayPlayerCards(getChosenGodCardsForPlayer(), getChosenColorsForPlayer()));

            printer.print();
        }
    }

    @Override
    public void onChosenStep(String nickname, String step, boolean result) {
        super.onChosenStep(nickname, step, result);

        if(!result)
            printer.setInfoMessage("Step choice failed");

        printer.print();
    }

    @Override
    public void onColorAssigned(String nickname, String color, boolean result) {
        super.onColorAssigned(nickname, color, result);

        printer.erase();

        if(!result)
            printer.setInfoMessage("Color assignment failed");

        printer.print();
    }

    @Override
    public void onEndGameUpdate(String winnerNickname) {
        super.onEndGameUpdate(winnerNickname);

        printer.erase();
        printer.setState("Game Over!");
        printer.setInfoMessage(winnerNickname + " won!");
        printer.print();
    }

    @Override
    public void onWrongInsertionUpdate(String error) {
        super.onWrongInsertionUpdate(error);

        printer.setInfoMessage(error);
        printer.print();
    }

    @Override
    public void onWrongNumberInsertion() {
        super.onWrongNumberInsertion();

        printer.setInfoMessage("SERVER: Wrong Number Insertion");
        printer.print();
        setState(ViewState.NUMPLAYERS);
    }

    @Override
    public void onGodCardAssigned(String nickname, String card, boolean result) {
        super.onGodCardAssigned(nickname, card, result);

        if(result) {
            if(nickname.equals(getNickname()))
                printer.erase();
                printer.setInfoMessage("GodCard assigned correctly");
        } else
            printer.setInfoMessage("GodCard assignment failed");

        printer.print();
    }

    @Override
    public void onPlayerAdded(String nickname, boolean result) {
        super.onPlayerAdded(nickname, result);

        if(result) {
            printer.setInfoMessage("\n" + nickname + " joined the game!");
        }
        else {
            printer.setInfoMessage("Could not register nickname");
            printer.setAskMessage("Nickname: ");
        }

        printer.print();
    }

    @Override
    public void onLossUpdate(String nickname) {
        super.onLossUpdate(nickname);

        gameMap.removePlayer(nickname);
        printer.setInfoMessage(nickname + " has lost!");
        printer.print();


    }

    @Override
    public void onPlayerTurn(String nickname) {
        super.onPlayerTurn(nickname);
        currentTurnBuilderPos = null;

        printer.setInfoMessage("Now playing: " + nickname);
        printer.print();
    }

    @Override
    public void onStartPlayerSet(String nickname, boolean result) {
        super.onStartPlayerSet(nickname, result);

        if(result)
            printer.setInfoMessage("The starting player is: " + nickname);
        else
            printer.setInfoMessage("Could not set " + nickname + " as starting player");

        printer.print();
    }


    @Override
    public void onStateUpdate(Model.State currState) {

        super.onStateUpdate(currState);

        printer.erase();

        if(currState.equals(Model.State.SETUP_PLAYERS))
            printer.setInfoMessage("Waiting for opponents...");

        printer.print();
        printer.erase();

        /*printer.erase();
        printer.setState(currState.toString());
        printer.print();
        printer.erase();*/
    }

    @Override
    public void onMatchGodCardsAssigned(Set<String> godCardsToUse, boolean result) {
        super.onMatchGodCardsAssigned(godCardsToUse, result);

        if(!result) {
            chooseMatchGodCards(getNumberOfPlayers(), allGodCards);
        } else {
            printer.setInfoMessage("GodCards to use: " + godCardsToUse);
            printer.print();
        }
    }

    @Override
    public void updatePossibleMoveDst(Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2) {
        super.updatePossibleMoveDst(possibleDstBuilder1, possibleDstBuilder2);

        gameMap.setPossibleDst(possibleDstBuilder1, possibleDstBuilder2);
        //printer.setGameMapString(gameMap.toString());
        printer.setInfoMessage("Move action required.");
        printer.setAskMessage("Press ENTER to start or enter QUIT to exit the game.");
        printer.print();


        //erase message that asks for ENTER
        printer.setAskMessage(null);

    }

    @Override
    public void updatePossibleBuildDst(Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2, Set<Coordinates> possibleDstBuilder1forDome, Set<Coordinates> possibleDstBuilder2forDome) {
        super.updatePossibleBuildDst(possibleDstBuilder1, possibleDstBuilder2, possibleDstBuilder1forDome, possibleDstBuilder2forDome);

        printer.setInfoMessage("Build action required.");
        printer.setAskMessage("Press ENTER to start or enter QUIT to exit the game.");
        printer.print();

        //erase message
        printer.setAskMessage(null);
    }

    @Override
    public void build() {
        String buildType = "B";
        Coordinates dst;
        Set<Coordinates> possibleDstBuilder;
        boolean buildDome = false;

        //gameMap.show(possibleDstBuilder1, possibleDstBuilder2, getChosenBuilderNum());

        if (gameMap.getChosenBuilderNum() == 0)
            currentTurnBuilderPos = chooseTurnBuilder();

        if ((!possibleDstBuilder1forDome.isEmpty() && gameMap.getChosenBuilderNum() == 1) || (!possibleDstBuilder2forDome.isEmpty() &&
                gameMap.getChosenBuilderNum() == 2)) {
            printer.setAskMessage("Select what you want to build: insert 'D' for dome or 'B' for building: ");
            printer.print();
            buildType = in.nextLine().toUpperCase();
            //checkLeaving(buildType);

            while (!(buildType.equals("D") || buildType.equals("B"))) {
                printer.setAskMessage("Invalid insertion. Select what you want to build: insert 'D' for dome or 'B' for building ");
                printer.print();
                buildType = in.nextLine().toUpperCase();
                //checkLeaving(buildType);
            }
        }

        printer.setAskMessage(null);

        if (buildType.equals("D")) {
            buildDome = true;
            if (gameMap.getChosenBuilderNum() == 1)
                possibleDstBuilder = possibleDstBuilder1forDome;
            else
                possibleDstBuilder = possibleDstBuilder2forDome;
        }
        else {
            if (gameMap.getChosenBuilderNum() == 1)
                possibleDstBuilder = possibleDstBuilder1;
            else
                possibleDstBuilder = possibleDstBuilder2;
        }

        if (gameMap.getChosenBuilderNum() == 1)
            gameMap.setPossibleDst(possibleDstBuilder, null);
        else
            gameMap.setPossibleDst(null, possibleDstBuilder);

        printer.setGameMapString(gameMap.toString());
        printer.setInfoMessage("Insert the coordinates of where you want to build ");
        printer.print();
        dst = coordinatesInsertion();

        while (!possibleDstBuilder.contains(dst)){
            printer.setInfoMessage("Invalid coordinates. Select a cell from the selected ones");
            dst = coordinatesInsertion();
        }

        synchronized (this) {
            notifyBuild(getNickname(), currentTurnBuilderPos, dst, buildDome);
            setState(ViewState.WAITING);
        }
    }

    @Override
    public void move() {

        Set<Coordinates> possibleDstBuilder;

        //gameMap.show(possibleDstBuilder1, possibleDstBuilder2, getChosenBuilderNum());

        if (gameMap.getChosenBuilderNum() == 0)
            currentTurnBuilderPos = chooseTurnBuilder();

        if (gameMap.getChosenBuilderNum() == 1) {
            possibleDstBuilder = possibleDstBuilder1;
            gameMap.setPossibleDst(possibleDstBuilder, null);
        }
        else {
            possibleDstBuilder = possibleDstBuilder2;
            gameMap.setPossibleDst(null, possibleDstBuilder);
        }

        gameMap.setChosenBuilderNum(gameMap.getChosenBuilderNum());

        printer.erase();
        printer.setGameMapString(gameMap.toString());
        printer.setPlayersList(displayPlayerCards(getChosenGodCardsForPlayer(), getChosenColorsForPlayer()));

        printer.setInfoMessage("Insert the coordinates of where you want to move ");
        printer.print();
        Coordinates dstMove = coordinatesInsertion();


        //verifies that the selected cell contains a valid builder
        while (!possibleDstBuilder.contains(dstMove)){
            printer.erase();
            printer.setInfoMessage("Invalid coordinates. Select a cell from the available ones");

            printer.setGameMapString(gameMap.toString());
            printer.setPlayersList(displayPlayerCards(getChosenGodCardsForPlayer(), getChosenColorsForPlayer()));
            dstMove = coordinatesInsertion();
        }

        synchronized (this) {
            notifyMove(getNickname(), currentTurnBuilderPos, dstMove);
            setState(ViewState.WAITING);
        }
    }

    @Override
    public void onConnectionError(String message) {
        printer.erase();
        printer.setInfoMessage(message);
        printer.print();
        printer.erase();
        printer.setAskMessage("Server to join: ");
        printer.print();
    }

    @Override
    public void onDisconnection() {
        super.onDisconnection();

        gameMap.setChosenBuilderNum(0);
        gameMap = new CliGameMap();

        chosenBuilderPositions = new ArrayList<>();
        matchGodCards = new HashSet<>();

        printer.setInfoMessage("Disconnecting");
        printer.print();
        printer.erase();
        printer.printTitle();
        printer.setAskMessage("Server to join: ");
        printer.print();
    }
}
