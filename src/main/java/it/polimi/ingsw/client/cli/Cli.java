package it.polimi.ingsw.client.cli;


import it.polimi.ingsw.client.Color;
import it.polimi.ingsw.client.View;
import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.gameMap.Coordinates;

import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.ingsw.client.Color.returnColor;


/**
 * Console based User Interface.
 */
public class Cli extends View {

    private final Printer printer = new Printer(System.out);
    private boolean quit = false;

    private ArrayList<String> inputOptions;

    private Map<String, String> allGodCards;

    private Scanner in;


    public Cli() {
        in = new Scanner(System.in);
        gameMap = new CliGameMap();
        printer.print();
    }


    @Override
    public  void run() {
        String input = "";
        in = new Scanner(System.in);
        //setState(ViewState.CONNECTION);


        quit = false;

        while(!quit) {

            input = in.nextLine();

            Scanner parser = new Scanner(input);

            try {
                switch (getState()) {
                    case NUMPLAYERS:
                        setNumberOfPlayers(parser.nextInt()); //i think this inserts \n automatically, that's why cli sends an empty message
                        notifyNumberOfPlayers(getNumberOfPlayers());
                        break;
                    case NICKDATE:
                        setNickname(parser.nextLine());
                        printer.setAskMessage("Date (yyyy.mm.dd): ");
                        printer.print();
                        setDate(in.nextLine());
                        notifyNewPlayer(getNickname(), getDate());
                        break;
                    case MATCHGODS:
                        while(getMatchGodCards().size() < getNumberOfPlayers()) {
                            if(parser.hasNext())
                                addMatchGodCard(getOption(parser.nextInt()));
                            else
                                addMatchGodCard(getOption(in.nextInt()));


                            if(getMatchGodCards().size() == getNumberOfPlayers())
                                chooseMatchGodCards(getNumberOfPlayers(), allGodCards);
                        }

                        notifyMatchGodCardsChoice(getNickname(), getMatchGodCards());
                        break;
                    case STARTPLAYER:
                        notifySetStartPlayer(getNickname(), getOption(parser.nextInt()));
                        break;
                    case PLAYERGOD:
                        notifyGodCardChoice(getNickname(), getOption(parser.nextInt()));

                    case BUILDERCOLOR:
                        notifyColorChoice(getNickname(), getOption(parser.nextInt()));
                        break;

                    case BUILDERPLACEMENT:
                        boolean askForRow = false;
                        int row;
                        int column;

                        while(getChosenBuilderPositions().size() < 2) {
                            if(parser.hasNext())
                                row = parser.nextInt();
                            else
                                row = in.nextInt();

                            printer.setAskMessage("COLUMN: ");
                            printer.print();
                            column = in.nextInt();
                            addBuilderPosition(new Coordinates(row, column));

                            if(getChosenBuilderPositions().size() < 2)
                            {
                                printer.setAskMessage("ROW: ");
                                printer.print();
                            }
                        }

                        notifySetupBuilders(getNickname(), getChosenBuilderPositions().get(0), getChosenBuilderPositions().get(1));
                        break;
                    case STEP:
                        notifyStepChoice(getNickname(), getOption(parser.nextInt()));
                        break;

                    case MOVE:
                        move();
                        break;
                    case BUILD:
                        build();
                        break;


                }
            } catch (NullPointerException e) {
                System.err.println("NullPointerException: " + e.getMessage());
                e.printStackTrace();
                in.nextLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Coordinates coordinatesInsertion() {
        int row;
        int column;

        //asks for row
        printer.setGameMapString(null);
        printer.setAskMessage("ROW: ");
        printer.print();
        row = in.nextInt();

        //ask
        printer.setAskMessage("COLUMN: ");
        printer.print();
        column = in.nextInt();

        in.nextLine();

        return new Coordinates(row, column);
    }

    private Coordinates chooseTurnBuilder () {
        gameMap.setPossibleDst(possibleDstBuilder1, possibleDstBuilder2);
        gameMap.setChosenBuilderNumber(0);
        printer.setGameMapString(gameMap.toString());

        Coordinates src;
        printer.setInfoMessage("Insert the coordinates of the builder you want to use ");
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
            setChosenBuilderNum(1);
        else
            setChosenBuilderNum(2);
        return src;
    }


    public void build() {
        String buildType = "B";
        Coordinates dst;
        Set<Coordinates> possibleDstBuilder;
        boolean buildDome = false;

        //gameMap.show(possibleDstBuilder1, possibleDstBuilder2, getChosenBuilderNum());

        if (getChosenBuilderNum() == 0)
            currentTurnBuilderPos = chooseTurnBuilder();

        if ((!possibleDstBuilder1forDome.isEmpty() && getChosenBuilderNum() == 1) || (!possibleDstBuilder2forDome.isEmpty() &&
                getChosenBuilderNum() == 2)) {
            printer.setAskMessage("Select what you want to build: insert 'D' for dome or 'B' for building: ");
            printer.print();
            buildType = in.nextLine().toUpperCase();
            //checkLeaving(buildType);

            while (!(buildType.equals("D") || buildType.equals("B"))) {
                printer.setAskMessage("Invalid insertion. Select what yuo want to build: insert 'D' for dome or 'B' for building ");
                printer.print();
                buildType = in.nextLine().toUpperCase();
                //checkLeaving(buildType);
            }
        }

        if (buildType.equals("D")) {
            buildDome = true;
            if (getChosenBuilderNum() == 1)
                possibleDstBuilder = possibleDstBuilder1forDome;
            else
                possibleDstBuilder = possibleDstBuilder2forDome;
        }
        else {
            if (getChosenBuilderNum() == 1)
                possibleDstBuilder = possibleDstBuilder1;
            else
                possibleDstBuilder = possibleDstBuilder2;
        }

        if (getChosenBuilderNum() == 1)
            gameMap.setPossibleDst(possibleDstBuilder1, null);
        else
            gameMap.setPossibleDst(null, possibleDstBuilder2);

        printer.setInfoMessage("Insert the coordinates of where you want to build ");
        dst = coordinatesInsertion();

        while (!possibleDstBuilder.contains(dst)){
            printer.setInfoMessage("Invalid coordinates. Select a cell from the selected ones");
            dst = coordinatesInsertion();
        }

        notifyBuild(getNickname(), currentTurnBuilderPos, dst, buildDome);
    }


    public void move() {
        Set<Coordinates> possibleDstBuilder;

        //gameMap.show(possibleDstBuilder1, possibleDstBuilder2, getChosenBuilderNum());

        if (getChosenBuilderNum() == 0)
            currentTurnBuilderPos = chooseTurnBuilder();

        if (getChosenBuilderNum() == 1) {
            possibleDstBuilder = possibleDstBuilder1;
            gameMap.setPossibleDst(possibleDstBuilder, null);
        }
        else {
            possibleDstBuilder = possibleDstBuilder2;
            gameMap.setPossibleDst(null, possibleDstBuilder);
        }

        gameMap.setChosenBuilderNumber(getChosenBuilderNum());

        printer.setInfoMessage("Insert the coordinates of where you want to move ");
        Coordinates dstMove = coordinatesInsertion();

        //verifies that the selected cell contains a valid builder
        while (!possibleDstBuilder.contains(dstMove)){
            printer.setInfoMessage("Invalid coordinates. Select a cell from the available ones");
            dstMove = coordinatesInsertion();
        }

        notifyMove(getNickname(), currentTurnBuilderPos, dstMove);
    }


    private String displayMap(Map<String, String> map) {
        StringBuilder result = new StringBuilder();
        inputOptions = new ArrayList<>();
        int i = -1;

        for(String godName : map.keySet()) {
            i++;
            result.append(i).append(") ").append(View.red).append(godName).append(Color.RESET).append(": ").append(map.get(godName)).append("\n");
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

    private String getOption(int choice) throws Exception{

        if(choice < 0 || choice >= inputOptions.size())
            throw new Exception("Invalid Choice.");

        return inputOptions.get(choice);
    }

    //override View methods


    @Override
    public synchronized void askNumberOfPlayers() {
        super.askNumberOfPlayers();

        printer.setAskMessage("Number Of Players: ");
        printer.print();
        notifyAll();
    }

    @Override
    public synchronized void askNickAndDate() {
        super.askNickAndDate();

        printer.setAskMessage("Nickname: ");
        printer.print();
        notifyAll();
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


        String crown = Color.ANSI_YELLOW.escape() + "\u2654" + Color.RESET;
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
        printer.print();
    }

    @Override
    public void askBuilderColor(Set<String> chosenColors) {
        super.askBuilderColor(chosenColors);

        Set<String> allColors = new HashSet<>(Set.of("MAGENTA", "WHITE", "LIGHT_BLUE"));
        allColors.removeAll(chosenColors);

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
    }

    @Override
    public void onBuilderBuild(String nickname, Coordinates src, Coordinates dst, boolean dome, boolean result) {
        super.onBuilderBuild(nickname, src, dst, dome, result);

        if(!result)
            printer.setInfoMessage("Build failed");
        else {
            gameMap.setPossibleDst(null, null);
            printer.setGameMapString(gameMap.toString());
            printer.setChoiceList(null);
            printer.setAskMessage(null);
        }

        printer.print();
    }

    @Override
    public void onBuilderMovement(String nickname, Coordinates src, Coordinates dst, boolean result) {
        super.onBuilderMovement(nickname, src, dst, result);

        if(!result)
            printer.setInfoMessage("Move failed");
        else {
            gameMap.setPossibleDst(null, null);
            printer.setGameMapString(gameMap.toString());
            printer.setChoiceList(null);
            printer.setAskMessage(null);
        }

        printer.print();

    }

    @Override
    public void onBuildersPlacedUpdate(String nickname, Coordinates positionBuilder1, Coordinates positionBuilder2, boolean result) {
        super.onBuildersPlacedUpdate(nickname, positionBuilder1, positionBuilder2, result);

        if(!result)
            printer.setInfoMessage("Builders placement failed");
        else {
            gameMap.setPossibleDst(null, null);
            printer.setGameMapString(gameMap.toString());
            printer.setChoiceList(null);
            printer.setAskMessage(null);
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

        if(!result)
            printer.setInfoMessage("Color assignment failed");

        printer.print();
    }

    @Override
    public void onEndGameUpdate(String winnerNickname) {
        super.onEndGameUpdate(winnerNickname);

        printer.setAskMessage(null);
        printer.setGameMapString(null);
        printer.setState("Game Over");
        printer.setChoiceList(null);
        printer.setInfoMessage("Player: " + winnerNickname + " winned!");
        printer.print();
    }

    @Override
    public void onWrongInsertionUpdate(String error) {
        super.onWrongInsertionUpdate(error);

        printer.setInfoMessage("SERVER: Invalid Insertion");
        printer.print();
    }

    @Override
    public void onWrongNumberInsertion() {
        super.onWrongNumberInsertion();

        printer.setInfoMessage("SERVER: Wrong Number Insertion");
        printer.print();
    }

    @Override
    public void onGodCardAssigned(String nickname, String card, boolean result) {
        super.onGodCardAssigned(nickname, card, result);

        if(result) {
            if(nickname.equals(getNickname()))
                printer.setInfoMessage("GodCard assigned correctly");
        } else
            printer.setInfoMessage("GodCard assignment failed");

        printer.print();
    }

    @Override
    public void onPlayerAdded(String nickname, boolean result) {
        super.onPlayerAdded(nickname, result);

        if(result) {
            printer.setInfoMessage(nickname + " joined the game!");
            printer.setAskMessage(null);
        }
        else
            printer.setInfoMessage("Could not register nickname");

        printer.print();
    }

    @Override
    public void onLossUpdate(String nickname) {
        super.onLossUpdate(nickname);

        printer.setInfoMessage(nickname + " has lost!");
        printer.print();
    }

    @Override
    public void onPlayerTurn(String nickname) {
        super.onPlayerTurn(nickname);

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

        printer.setState(currState.toString());
        printer.setAskMessage(null);
        printer.print();
    }
}
