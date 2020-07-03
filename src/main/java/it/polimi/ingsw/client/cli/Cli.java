package it.polimi.ingsw.client.cli;


import it.polimi.ingsw.client.View;
import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.gameMap.Builder;
import it.polimi.ingsw.server.model.gameMap.Coordinates;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static it.polimi.ingsw.client.cli.Color.returnColor;


/**
 * Console based User Interface.
 */
public class Cli extends View{

    private final Printer printer = new Printer(System.out);

    private ArrayList<Coordinates> chosenBuilderPositions = new ArrayList<>();
    private ArrayList<String> inputOptions; //array containing strings to choose from.

    //some UNICODE characters
    public static final String red = Color.ANSI_RED.escape();
    private static final String crownSymbol = "\u2654";


    private Set<String> matchGodCards = new HashSet<>();
    private Map<String, String> allGodCards; //all godCards descriptions sent from server.

    private Scanner in;
    private String input;

    private Integer row;
    private Coordinates coord;


    //flags for build phase
    private boolean askForBuildType = false; //tells if input will be used as build choice
    private String buildType;
    private Set<Coordinates> possibleDstBuilder = new HashSet<>();
    private boolean buildDome = false;


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

    private void addMatchGodCard(String godCard) throws Exception{
        if(!matchGodCards.contains(godCard))
            matchGodCards.add(godCard);
        else
            throw new Exception("GodCard already chosen!");
    }

    private Set<String> getMatchGodCards() { return Set.copyOf(matchGodCards);}

    /**
     * Main CLI function.
     * Prints content and handles User input according to ViewState state value.
     */
    @Override
    public  void run() {

        String server = null;

        printer.erase();
        printer.printTitle();


        printer.setAskMessage("Server to join: ");
        printer.print();
        in = new Scanner(System.in);
        setState(ViewState.CONNECTION);

        boolean quit = false;

        while (!quit) {

            input = in.nextLine();

            if (!input.toLowerCase().equals("quit")) {

                Scanner parser = new Scanner(input);

                int column;
                try {

                    //Cli decisions according to state
                    switch (getState()) {
                        case CONNECTION:
                            if(server == null) {
                                server = input;
                                printer.setAskMessage("Port: ");
                                printer.print();
                            } else {
                                notifyConnection(server, Integer.parseInt(input));
                                server = null;
                            }

                            break;

                        case NUMPLAYERS:
                            setNumberOfPlayers(Integer.parseInt(input));

                            notifyNumberOfPlayers(getNumberOfPlayers());
                            setState(ViewState.WAITING);

                            break;
                        case NICKDATE:
                            if(getNickname() == null) {
                                setNickname(input);

                                printer.erase();
                                printer.setAskMessage("Birth Date (yyyy.mm.dd): ");
                                printer.print();
                                printer.erase();
                            } else {
                                setDate(input);
                                notifyNewPlayer(getNickname(), getDate());
                                setState(ViewState.WAITING);
                            }

                            break;

                        case MATCHGODS:
                            if(getMatchGodCards().size() < getNumberOfPlayers()) {
                                addMatchGodCard(getOption(Integer.parseInt(input)));

                                if(getMatchGodCards().size() < getNumberOfPlayers())
                                    chooseMatchGodCards(getNumberOfPlayers(), allGodCards);

                            }

                            if(getMatchGodCards().size() == getNumberOfPlayers()) {
                                printer.erase();

                                notifyMatchGodCardsChoice(getNickname(), getMatchGodCards());
                                setState(ViewState.WAITING);
                            }

                            break;

                        case STARTPLAYER:
                            notifySetStartPlayer(getNickname(), getOption(Integer.parseInt(input)));
                            setState(ViewState.WAITING);

                            break;

                        case PLAYERGOD:
                            notifyGodCardChoice(getNickname(), getOption(Integer.parseInt(input)));
                            setState(ViewState.WAITING);

                            break;

                        case BUILDERCOLOR:
                            notifyColorChoice(getNickname(), getOption(Integer.parseInt(input)));
                            setState(ViewState.WAITING);

                            break;

                        case BUILDERPLACEMENT:
                            printer.erase();

                            switch (getChosenBuilderPositions().size()) {
                                case 0:
                                    if(row == null) {
                                        row = Integer.valueOf(input);
                                        printer.setAskMessage("COLUMN: ");
                                    }
                                    else {
                                        column = Integer.parseInt(input);
                                        addBuilderPosition(new Coordinates(row, column));

                                        row = null;

                                        printer.setAskMessage("Place your second builder \nROW: ");
                                    }
                                    printer.print();

                                    //we have now one builder
                                    break;

                                case 1:
                                    if(row == null) {
                                        row = Integer.valueOf(input);
                                        printer.setAskMessage("COLUMN: ");
                                        printer.print();
                                    } else {
                                        column = Integer.parseInt(input);
                                        addBuilderPosition(new Coordinates(row, column));

                                        row = null;

                                        notifySetupBuilders(getNickname(), getChosenBuilderPositions().get(0), getChosenBuilderPositions().get(1));
                                        setState(ViewState.WAITING);
                                    }
                                    break;
                            }

                            break;

                        case STEP:
                            notifyStepChoice(getNickname(), getOption(Integer.parseInt(input)));
                            setState(ViewState.WAITING);

                            break;

                        case MOVE:
                            if(row == null) {
                                printer.erase();
                                row = Integer.parseInt(input);
                                printer.setAskMessage("COLUMN: ");
                                printer.print();
                            } else {
                                column = Integer.parseInt(input);

                                coord = new Coordinates(row, column);

                                //got the coordinates

                                move();

                                row = null;
                            }

                            break;
                        case BUILD:

                            if(askForBuildType)
                                build();
                            else if(row == null) {
                                printer.erase();
                                row = Integer.parseInt(input);
                                printer.setAskMessage("COLUMN: ");
                                printer.print();

                            } else {
                                column = Integer.parseInt(input);

                                coord = new Coordinates(row, column);

                                //got the coordinates

                                build();

                                row = null;
                            }

                            break;

                        case WAITING:
                            printer.setInfoMessage("Waiting from Server.\nYou entered: " + parser.nextLine());
                            printer.print();
                            break;

                    }
                } catch (NumberFormatException e) {
                    printer.setInfoMessage("You have to insert a number");

                    if ((getState().equals(ViewState.MOVE) || getState().equals(ViewState.BUILD) || getState().equals(ViewState.BUILDERPLACEMENT) && row == null))
                        printer.setAskMessage("ROW: ");
                    printer.print();
                    printer.setInfoMessage(null);

                } catch (NoSuchElementException ignored) {

                } catch (InvalidOptionException e) {
                    printer.setInfoMessage(e.getMessage());
                    printer.print();

                } catch (IllegalArgumentException e) {      //new Coordinates(a,b) throws IllegalArgumentException if (a,b) is out of Map.
                    row = null;
                    printer.setInfoMessage("Coordinates out of Map");
                    printer.setAskMessage("ROW: ");
                    printer.print();
                }  catch (Exception e) {
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



    /**
     * Check if coord attribute is a cell containing an owned builder that is not blocked
     * @return true if coord attribute contains a valid cell
     */
    private boolean chooseTurnBuilder () {

        boolean result = true;

        //verifies that the selected cell contains a valid builder
        if(!((Coordinates.equals(gameMap.getOccupiedCells().get(getNickname()).get(0), coord) &&
                (!possibleDstBuilder1.isEmpty() || !possibleDstBuilder1forDome.isEmpty())) ||
                (Coordinates.equals(gameMap.getOccupiedCells().get(getNickname()).get(1), coord) &&
                        (!possibleDstBuilder2.isEmpty() || !possibleDstBuilder2forDome.isEmpty())))) {


            printer.setAskMessage(null);
            printer.setInfoMessage("Invalid coordinates, select a cell with a valid builder");
            printer.setAskMessage("ROW: ");
            printer.print();

            result = false;
        } else {
            if (Coordinates.equals(gameMap.getOccupiedCells().get(getNickname()).get(0), coord))
                gameMap.setChosenBuilderNum(1);
            else
                gameMap.setChosenBuilderNum(2);

        }

        return result;
    }

    /**
     * returns string to print representing a Map Collection
     * @param map to represent
     * @return String to print
     */
    private String getMapRepresentation(Map<String, String> map) {
        StringBuilder result = new StringBuilder();
        inputOptions = new ArrayList<>();
        int index = -1;

        for(String godName : map.keySet()) {
            index++;
            result.append(index).append(") ").append(red).append(godName).append(Color.RESET).append(": ").append(map.get(godName)).append("\n");
            inputOptions.add(index, godName);
        }

        result.append("\n");

        return result.toString();

    }

    /**
     * returns string to print representing a Set Collection
     * @param set to represent
     * @return String to print
     */
    private String getSetRepresentation(Set<String> set) {
        StringBuilder result = new StringBuilder();
        inputOptions = new ArrayList<>();
        int index = -1;

        String realColor;

        for(String element : set) {
            index++;
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
            inputOptions.add(index, element);

            if(getState().equals(ViewState.BUILDERCOLOR))
                element = element.replace("_", " ");

            result.append(index).append(") ").append(realColor).append(element.toLowerCase()).append(Color.RESET).append("\n");

        }


        return result.toString();
    }


    /**
     * returns a String that lists each player name colored according to his chosen color followed by his chosen card
     * @param chosenGodCardsForPlayer map from player nickname to the card he chose.
     * @param chosenColors map from player nickname to the color he chose
     * @return String to print
     */
    private String getPlayersAndCardsRepresentation(Map<String, String> chosenGodCardsForPlayer, Map<String, String> chosenColors) {
        StringBuilder result = new StringBuilder();

        for (String player : chosenGodCardsForPlayer.keySet()) {

            String playerColor = (chosenColors.get(player).toUpperCase());

            result.append("    ").append(returnColor(playerColor)).append(player).append(Color.RESET).append(" : ").append(chosenGodCardsForPlayer.get(player)).append("\n");

        }

        return result.toString();
    }

    /**
     * get String from inputOptions attribute at choice position
     * @param choice it's the index of inputOptions list to read at
     * @return String at choice index
     * @throws InvalidOptionException if choice is not a valid index of inputOptions list
     */
    private String getOption(int choice) throws InvalidOptionException{

        if(choice < 0 || choice >= inputOptions.size())
            throw new InvalidOptionException("Invalid Choice.");

        return inputOptions.get(choice);
    }


    //override View methods

    /**
     * Prints request
     */
    @Override
    public void askNumberOfPlayers() {
        super.askNumberOfPlayers();

        printer.setAskMessage("Number of players: ");
        printer.print();
    }

    /**
     * Prints request
     */
    @Override
    public void askNickAndDate() {
        super.askNickAndDate();

        printer.setAskMessage("Nickname: ");
        printer.print();
    }

    /**
     * Prints request
     * @param godDescriptionsParam Map having god names as keys and their power description as values
     */
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

        if (getMatchGodCards().size() == 0) {
            printer.setInfoMessage("\n" + crown + "  You're the " + Color.ANSI_YELLOW.escape() + "Challenger" + Color.RESET + " of this match!  " + crown + "\n" +
                    "Choose " + numOfPlayers + " godCards for the match: \n");
        }
        else {
            printer.setInfoMessage(null);
        }
        printer.setChoiceList(getMapRepresentation(availableGods));
        int choiceNumber = getMatchGodCards().size() + 1;
        printer.setAskMessage("Choice" + choiceNumber + ": ");
        printer.print();
    }

    /**
     * Prints request
     * @param players players to choose from
     */
    @Override
    public void chooseStartPlayer(Set<String> players) {
        super.chooseStartPlayer(players);

        inputOptions = new ArrayList<>();
        inputOptions.addAll(players);
        printer.setInfoMessage("As Challenger of the match, choose the starting player: ");
        printer.setChoiceList(getSetRepresentation(players));
        printer.setAskMessage("Choice: ");
        printer.print();
    }

    /**
     * Prints request
     * @param godDescriptions Map having god names as keys and their power description as values
     * @param chosenCards Set containing names of gods already chosen
     */
    @Override
    public void askGodCard(Map<String, String> godDescriptions, Set<String> chosenCards) {
        super.askGodCard(godDescriptions, chosenCards);

        Map<String, String> availableGods = new HashMap<>();
        inputOptions = new ArrayList<>();


        for(String godName : godDescriptions.keySet().stream().filter(godName -> !chosenCards.contains(godName)).collect(Collectors.toSet())) {
            availableGods.put(godName, godDescriptions.get(godName));
            inputOptions.add(godName);
        }

        printer.setInfoMessage("Choose you god Card");
        printer.setChoiceList(getMapRepresentation(availableGods));
        printer.setAskMessage("Choice: ");
        printer.print();
    }

    /**
     * Prints request
     * @param chosenColors colors already chosen
     */
    @Override
    public void askBuilderColor(Set<String> chosenColors) {
        super.askBuilderColor(chosenColors);

        Set<String> allColors = Stream.of(Builder.BuilderColor.values()).map(Enum::name).collect(Collectors.toSet());
        allColors.removeAll(chosenColors);

        inputOptions = new ArrayList<>();
        inputOptions.addAll(allColors);
        printer.setChoiceList(getSetRepresentation(allColors));
        printer.setAskMessage("Choice: ");
        printer.print();
    }

    /**
     * Prints request
     */
    @Override
    public void placeBuilders() {
        super.placeBuilders();

        printer.setGameMapString(gameMap.toString());
        printer.setPlayersList(getPlayersAndCardsRepresentation(getChosenGodCardsForPlayer(), getChosenColorsForPlayer()));
        printer.setChoiceList(null);
        printer.setAskMessage("Place your first builder.\nROW: ");
        printer.print();
    }

    /**
     * Prints request
     *
     * possibleSteps can contain only Strings that equal to "MOVE", "BUILD", "END".
     *
     * @param possibleSteps Set of possible Steps to choose from.
     */
    @Override
    public void chooseNextStep(Set<String> possibleSteps) {
        super.chooseNextStep(possibleSteps);

        inputOptions = new ArrayList<>();

        inputOptions.addAll(possibleSteps);

        printer.setChoiceList(getSetRepresentation(possibleSteps));
        printer.setAskMessage("Next step: ");
        printer.print();

        printer.erase();
    }

    /**
     * If results is true it updates gameMap with the new build event and prints it. If result is false it prints an error message
     * @param nickname player who played
     * @param src position of the builder who built
     * @param dst where the builder built
     * @param dome true if the builder built a Dome
     * @param result true if the server accepted it
     */
    @Override
    public void onBuilderBuild(String nickname, Coordinates src, Coordinates dst, boolean dome, boolean result) {
        super.onBuilderBuild(nickname, src, dst, dome, result);

        if(!result) {
            printer.setInfoMessage("Build failed");
        }
        else {
            printer.setGameMapString(gameMap.toString());
            printer.setPlayersList(getPlayersAndCardsRepresentation(getChosenGodCardsForPlayer(), getChosenColorsForPlayer()));

            printer.setChoiceList(null);
            printer.setAskMessage(null);
        }

        printer.print();
    }

    /**
     * If results is true it updates gameMap with the new move event and prints it. If result is false it prints an error message
     * @param nickname player who played
     * @param src position of the builder that moved
     * @param dst where the builder moved
     * @param result true if the server accepted it
     */
    @Override
    public void onBuilderMovement(String nickname, Coordinates src, Coordinates dst, boolean result) {
        super.onBuilderMovement(nickname, src, dst, result);

        if(!result) {
            printer.setInfoMessage("Move failed");

        }
        else {
            gameMap.setPossibleDst(null, null);

            printer.setGameMapString(gameMap.toString());
            printer.setPlayersList(getPlayersAndCardsRepresentation(getChosenGodCardsForPlayer(), getChosenColorsForPlayer()));
            printer.setChoiceList(null);
            printer.setAskMessage(null);
        }

        printer.print();

    }

    /**
     * If results is true it updates gameMap with the new builders and prints it. If result is false it prints an error message
     * @param nickname nickname of the player who placed his builders
     * @param positionBuilder1 position of the first builder placed
     * @param positionBuilder2 position of the second builder placed
     * @param result true if the server accepted it
     */
    @Override
    public void onBuildersPlacedUpdate(String nickname, Coordinates positionBuilder1, Coordinates positionBuilder2, boolean result) {
        super.onBuildersPlacedUpdate(nickname, positionBuilder1, positionBuilder2, result);

        printer.erase();

        if(!result) {
            printer.setInfoMessage("Builders placement failed");

            chosenBuilderPositions = new ArrayList<>();
        }
        else {
            printer.erase();
            printer.setInfoMessage(nickname + "'s builders placed!");
            printer.setGameMapString(gameMap.toString());
            printer.setPlayersList(getPlayersAndCardsRepresentation(getChosenGodCardsForPlayer(), getChosenColorsForPlayer()));


        }

        printer.print();
        printer.erase();
    }

    /**
     * Prints an error message if the step choice request failed
     * @param nickname nickname of the player who chose the step
     * @param step step chosen
     * @param result true if the server accepted the request
     */
    @Override
    public void onChosenStep(String nickname, String step, boolean result) {
        super.onChosenStep(nickname, step, result);

        if(!result)
            printer.setInfoMessage("Step choice failed");

        printer.print();
    }

    /**
     * Updates color informations for players if result is true or prints an error message if result is false
     * @param nickname nickname of the player who chose his color
     * @param color color chosen
     * @param result true if the server accepted the request
     */
    @Override
    public void onColorAssigned(String nickname, String color, boolean result) {
        super.onColorAssigned(nickname, color, result);

        printer.erase();

        if(!result)
            printer.setInfoMessage("Color assignment failed");

        printer.print();
    }

    /**
     * Prints a game over messsage and notifies the user about the player who won
     * @param winnerNickname nickname of the player who won
     */
    @Override
    public void onEndGameUpdate(String winnerNickname) {

        printer.erase();
        printer.setState("Game Over!");
        printer.setInfoMessage(winnerNickname + " won!");
        printer.print();
    }

    /**
     * Prints error received from server
     * @param error String representing the error
     */
    @Override
    public void onWrongInsertionUpdate(String error) {

        printer.setInfoMessage(error);
        printer.print();
    }

    /**
     * Prints error about invalid value for the number of players
     */
    @Override
    public void onWrongNumberInsertion() {
        super.onWrongNumberInsertion();

        printer.setInfoMessage("You can only insert 2 or 3");
        printer.print();
        setState(ViewState.NUMPLAYERS);
    }

    /**
     * Prints a message according to the result value and if result is true it updates player and relative cards information
     * @param nickname nickname of the player who chose the card
     * @param card card chosen
     * @param result true if the server accepted the request
     */
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

    /**
     * Prints a message according to the result value and if result is true it updates the set of nicknames
     * @param nickname nickname of the player
     * @param result true if the server accepted the registration
     */
    @Override
    public void onPlayerAdded(String nickname, boolean result) {
        super.onPlayerAdded(nickname, result);


        if(result) {
            if (getNickname() == null || getDate() == null) {
                printer.setInfoMessage("\n" + nickname + " joined the game!");
                if(getNickname() == null)
                    printer.setAskMessage("Nickname: ");
                else
                    printer.setAskMessage("Birth Date (yyyy.mm.dd): ");
            }
            else
                printer.setInfoMessage(nickname + " joined the game!");
        }
        else {
            printer.setInfoMessage("Could not register nickname");
            printer.setAskMessage("Nickname: ");
        }

        printer.print();
        printer.erase();
    }

    /**
     * Removes player and his card from game data and prints a message
     * @param nickname nickname of the player who just lost
     */
    @Override
    public void onLossUpdate(String nickname) {
        super.onLossUpdate(nickname);

        printer.setInfoMessage(nickname + " has lost!");
        printer.print();
    }

    /**
     * Prints a message about whose turn is
     * @param nickname nickname of the player whose turn is
     */
    @Override
    public void onPlayerTurn(String nickname) {
        super.onPlayerTurn(nickname);

        printer.setInfoMessage("Now playing: " + nickname);
        printer.print();
    }

    /**
     * Prints error message if result is false
     * @param nickname of the starting player
     * @param result true if correctly set
     */
    @Override
    public void onStartPlayerSet(String nickname, boolean result) {
        super.onStartPlayerSet(nickname, result);

        printer.erase();

        if(result)
            printer.setInfoMessage("The starting player is: " + nickname);
        else
            printer.setInfoMessage("Could not set " + nickname + " as starting player");

        printer.print();
    }

    /**
     * Prints Waiting message if currState equals SETUP_PLAYERS
     * @param currState new ModelState of the game
     */
    @Override
    public void onStateUpdate(Model.State currState) {


        printer.erase();

        if(currState.equals(Model.State.SETUP_PLAYERS))
            printer.setInfoMessage("Waiting for opponents...");

        printer.print();
        printer.erase();

    }

    /**
     * if result is true if prints which card names will be used during the game.
     * if result is false it will ask to choose cards.
     * @param godCardsToUse cards chosen
     * @param result true if the server accepted the choice
     */
    @Override
    public void onMatchGodCardsAssigned(Set<String> godCardsToUse, boolean result) {
        super.onMatchGodCardsAssigned(godCardsToUse, result);

        StringBuilder godNamesToPrint = new StringBuilder();
        for(String name : godCardsToUse)
            godNamesToPrint.append(" ").append(name);

        if(!result) {
            chooseMatchGodCards(getNumberOfPlayers(), allGodCards);
        } else {
            printer.setInfoMessage("GodCards to use: " + godNamesToPrint);
            printer.print();
        }
    }

    /**
     * Update information about which cells can be used by owned builders and set the ViewState to MOVE
     * @param possibleDstBuilder1 positions at which builder 1 can move
     * @param possibleDstBuilder2 positions at which builder 2 can move
     */
    @Override
    public void updatePossibleMoveDst(Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2) {
        super.updatePossibleMoveDst(possibleDstBuilder1, possibleDstBuilder2);

        gameMap.setPossibleDst(possibleDstBuilder1, possibleDstBuilder2);

        printer.setGameMapString(gameMap.toString());
        if(gameMap.getChosenBuilderNum() == 0)
            printer.setInfoMessage("Insert the coordinates of the builder you want to use");
        else
            printer.setInfoMessage("Insert the coordinates of where you want to move");
        printer.setAskMessage("ROW: ");
        printer.print();


    }

    /**
     * Update information about which cells can be used by owned builders and set the ViewState to BUILD
     * @param possibleDstBuilder1 positions where builder 1 can build a normal building
     * @param possibleDstBuilder2 positions where builder 2 can build a normal building
     * @param possibleDstBuilder1forDome positions where builder 1 can build a dome
     * @param possibleDstBuilder2forDome positions where builder 2 can build a dome
     */
    @Override
    public void updatePossibleBuildDst(Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2, Set<Coordinates> possibleDstBuilder1forDome, Set<Coordinates> possibleDstBuilder2forDome) {
        super.updatePossibleBuildDst(possibleDstBuilder1, possibleDstBuilder2, possibleDstBuilder1forDome, possibleDstBuilder2forDome);

        gameMap.setPossibleDst(possibleDstBuilder1, possibleDstBuilder2);

        if(gameMap.getChosenBuilderNum() == 0) {
            printer.setInfoMessage("Insert the coordinates of the builder you want to use");
            printer.setAskMessage("ROW: ");
        }
        else if((!possibleDstBuilder1forDome.isEmpty() && gameMap.getChosenBuilderNum() == 1) || (!possibleDstBuilder2forDome.isEmpty() &&
                gameMap.getChosenBuilderNum() == 2)) {
            gameMap.setPossibleDst(null, null);
            printer.setAskMessage("Select what you want to build: insert 'D' for dome or 'B' for building: ");
            askForBuildType = true;
        }
        else {
            printer.setInfoMessage("Insert the coordinates of where you want to build");
            printer.setAskMessage("ROW: ");
        }

        printer.setGameMapString(gameMap.toString());
        printer.print();

    }

    /**
     * Function that handles input during building phase.
     * If there is no buildType selected and player can build in at least one position a dome it will compare attribute string
     * to 'D' and 'B'. if 'D' string is given the move will be a Dome build; if 'B' string is given the move will be a regular move;
     * if none of the previous string is given it will prompt an error message. The rest of the function is similar to move() as it uses
     * coord attribute to select a valid builder or to select the destination position.
     *
     * This function computes only one of the phases listed above per call.
     */
    @Override
    public void build() {

        boolean buildTypeChosen = false;
        boolean inputForBuilderChoice = false;

        //if no builder has been selected
        if (gameMap.getChosenBuilderNum() == 0) {
            if(chooseTurnBuilder()) {
                inputForBuilderChoice = true;
                gameMap.setCurrentTurnBuilderPos(coord);
            } else
                return;
        }


        //have to set the buildType
        if (buildType == null) {

            //if i can build dome
            if ((!possibleDstBuilder1forDome.isEmpty() && gameMap.getChosenBuilderNum() == 1) || (!possibleDstBuilder2forDome.isEmpty() &&
                    gameMap.getChosenBuilderNum() == 2)) {


                if (inputForBuilderChoice) {
                    printer.setAskMessage("Select what you want to build: insert 'D' for dome or 'B' for building: ");
                    printer.print();
                    askForBuildType = true;
                    return;
                }

                switch (input.toUpperCase()) {
                    case "D":
                        buildDome = true;
                        buildType = "D";
                        possibleDstBuilder = gameMap.getChosenBuilderNum() == 1 ? possibleDstBuilder1forDome : possibleDstBuilder2forDome;
                        break;

                    case "B":
                        buildType = "B";
                        possibleDstBuilder = gameMap.getChosenBuilderNum() == 1 ? possibleDstBuilder1 : possibleDstBuilder2;
                        break;

                    default:
                        printer.setInfoMessage("Invalid insertion");
                        printer.print();
                        return;
                }

                buildTypeChosen = true;
                askForBuildType = false;

            } else {
                buildType = "B";
                possibleDstBuilder = gameMap.getChosenBuilderNum() == 1 ? possibleDstBuilder1 : possibleDstBuilder2;

            }
        }


        printer.setAskMessage(null);


        if (gameMap.getChosenBuilderNum() == 1)
            gameMap.setPossibleDst(possibleDstBuilder, null);
        else
            gameMap.setPossibleDst(null, possibleDstBuilder);

        if(inputForBuilderChoice || buildTypeChosen) {
            //buildTypeChosen = false;
            printer.setGameMapString(gameMap.toString());
            printer.setInfoMessage("Insert the coordinates of where you want to build ");
            printer.setAskMessage("ROW: ");
            printer.print();
            return;
        }

        if(!possibleDstBuilder.contains(coord)){
            printer.setInfoMessage("Invalid coordinates. Select a cell from the selected ones");
            printer.setAskMessage("ROW: ");
            printer.print();
            return;
        }

        possibleDstBuilder.clear();
        buildType = null;

        notifyBuild(getNickname(), gameMap.getCurrentTurnBuilderPos(), coord, buildDome);
        setState(ViewState.WAITING);


        buildDome = false;
    }


    /**
     * Function that handles input during move phase.
     * It uses coord attribute to select a valid builder if not done before or to select the destination position.
     *
     * The function computes only one of the phases listed above per call.
     */
    @Override
    public void move() {

        Set<Coordinates> possibleDstBuilder;
        boolean inputForBuilderChoice = false;

        //if no builder has been selected
        if (gameMap.getChosenBuilderNum() == 0) {
            if(chooseTurnBuilder()) {
                gameMap.setCurrentTurnBuilderPos(coord);
                inputForBuilderChoice = true;
            } else
                return;
        }

        if (gameMap.getChosenBuilderNum() == 1) {
            possibleDstBuilder = possibleDstBuilder1;
            gameMap.setPossibleDst(possibleDstBuilder, null);
        }
        else {
            possibleDstBuilder = possibleDstBuilder2;
            gameMap.setPossibleDst(null, possibleDstBuilder);
        }

        if (inputForBuilderChoice) {

            gameMap.setChosenBuilderNum(gameMap.getChosenBuilderNum());
            printer.erase();
            printer.setGameMapString(gameMap.toString());
            printer.setPlayersList(getPlayersAndCardsRepresentation(getChosenGodCardsForPlayer(), getChosenColorsForPlayer()));

            printer.setInfoMessage("Insert the coordinates of where you want to move");
            printer.setAskMessage("ROW: ");
            printer.print();

        } else {

            //verifies that the selected cell contains a valid builder
            if(!possibleDstBuilder.contains(coord)) {
                printer.erase();
                printer.setInfoMessage("Invalid coordinates. Select a cell from the available ones");
                printer.setAskMessage("ROW: ");

                printer.setGameMapString(gameMap.toString());
                printer.setPlayersList(getPlayersAndCardsRepresentation(getChosenGodCardsForPlayer(), getChosenColorsForPlayer()));

                printer.print();
            } else {

                notifyMove(getNickname(), gameMap.getCurrentTurnBuilderPos(), coord);
                setState(ViewState.WAITING);

            }
        }
    }

    /**
     * Prints connection error message
     */
    @Override
    public void onConnectionError(String message) {
        printer.erase();
        printer.setInfoMessage(message);
        printer.print();
        printer.erase();
        printer.setAskMessage("Server to join: ");
        printer.print();
    }

    /**
     * Prints disconnecting message and go back to Game Initial menu asking for server to join.
     */
    @Override
    public synchronized void onDisconnection() {
        super.onDisconnection();

        setState(ViewState.CONNECTION);
        gameMap = new CliGameMap();

        chosenBuilderPositions = new ArrayList<>();
        matchGodCards = new HashSet<>();

        printer.erase();
        printer.setInfoMessage("Disconnecting");
        printer.print();
        printer.erase();
        printer.printTitle();
        printer.setAskMessage("Server to join: ");
        printer.print();
    }

    @Override
    public void onOpponentDisconnection(String nickname) {
        printer.erase();
        printer.setInfoMessage(nickname + " disconnected");
        printer.print();
    }
}
