package it.polimi.ingsw.network;

import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.gameMap.Coordinates;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public abstract class Messages {

    public static final String TYPE = "type";

    //type values
    public static final String MOVE = "move";
    public static final String BUILD = "build";
    public static final String BUILDERS_PLACEMENT = "buildersPlacement";
    public static final String BUILDER_PUSHED = "builderPushed";
    public static final String POSSIBLE_BUILD_DESTINATIONS = "possibleBuildDestinations";
    public static final String POSSIBLE_MOVE_DESTINATIONS = "possibleMoveDestinations";
    public static final String ENDGAME = "endGame";
    public static final String SET_STEP_CHOICE = "setStepChoice";
    public static final String SET_NUMBER_OF_PLAYERS = "setNumberOfPlayers";
    public static final String SET_GOD_CARD = "setGodCard";
    public static final String ADD_PLAYER = "addPlayer";
    public static final String DISCONNECT = "disconnect";
    public static final String ERROR = "error";
    public static final String ERROR_NUMBER = "errorNumber"; //for ERROR
    public static final String INFO = "info";
    public static final String SET_MATCH_GOD_CARDS = "setMatchGodCards";
    public static final String SET_START_PLAYER = "setStartPlayer";
    public static final String PING = "ping";
    public static final String PONG = "pong";

    public static final String PARSE_ERROR_COLOR = "parseErrorColor";
    public static final String PARSE_ERROR_MOVE = "parseErrorMove";
    public static final String PARSE_ERROR_BUILD = "parseErrorBuild";
    public static final String PARSE_ERROR_GOD = "parseErrorGod";
    public static final String PARSE_ERROR_STEP_CHOICE = "parseErrorStepChoice";
    public static final String PARSE_ERROR_BUILDERS = "parseErrorBuilders";
    public static final String PARSE_ERROR_NUMBER = "parseErrorNumber";
    public static final String PARSE_ERROR_PLAYER = "parseErrorPlayer";

    public static final String COLOR_UPDATE = "colorUpdate";
    public static final String TURN_UPDATE = "turnUpdate";
    public static final String LOST_UPDATE = "lostUpdate";
    public static final String STATE_UPDATE = "stateUpdate";
    public static final String PLAYER_ADDED = "playerAdded";
    public static final String GOD_CARD_ASSIGNED = "godCardAssigned";

    //request type from controller
    public static final String ASK_NICK_AND_DATE = "askNickAndDate";
    public static final String ASK_COLOR = "askColor";
    public static final String ASK_GOD = "askGod";
    public static final String ASK_NUMBER_OF_PLAYERS = "askNumberOfPlayers";
    public static final String ASK_BUILDERS = "askBuilders";
    public static final String ASK_STEP = "askStep";
    public static final String POSSIBLE_STEPS = "possibleSteps";
    public static final String CHOOSE_MATCH_GOD_CARDS = "chooseMatchGodCards"; //
    public static final String CHOOSE_START_PLAYER = "chooseStartPlayer";


    //key values
    public static final String COLOR = "color";                             //for SET_COLOR
    public static final String WINNER = "winner";                       //for END_GAME
    public static final String SRC = "src";
    public static final String DST = "dst";
    public static final String BUILD_DOME = "buildDome";
    public static final String NUMBER_OF_PLAYERS = "numberOfPlayers";       //FOR SET_NUMBER_OF_PLAYERS
    public static final String DATE = "date";                               //for ADD_PLAYER
    public static final String NAME = "name";                               //for ADD_PLAYER
    public static final String DESCRIPTION = "description";                 //for ERROR
    public static final String POSSIBLE_DST = "possibleDst";
    public static final String STEP_CHOICE = "stepChoice";
    public static final String POSITIONS = "positions";                     //for SET_BUILDERS
    public static final String RESULT = "result";
    public static final String STATE = "state";                             //for STATE_UPDATE
    public static final String GOD_CARD = "godCard";                        //for SET_GOD_CARD
    public static final String GOD_DESCRIPTIONS = "godDescriptions";        //for ASK_GOD
    public static final String CHOSEN_GOD_CARDS = "chosenGodCards";         //for ASK_GOD
    public static final String CHOSEN_COLORS = "chosenColors";              //for ASK_COLOR
    public static final String MESSAGE = "message";                         //for INFO
    public static final String PLAYERS = "players";                         //for CHOOSE_START_PLAYER




    private static JSONObject fromCoordinates(Coordinates coord) {
        JSONObject obj = new JSONObject();
        obj.put("i", coord.getI());
        obj.put("j", coord.getJ());

        return obj;
    }

    private static JSONArray fromCollection(Collection<Coordinates> c) {
        JSONArray array = new JSONArray();
        for(Coordinates coord : c)
            array.put(fromCoordinates(coord));

        return array;
    }


    public static String possibleBuildDestinations(Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2, Set<Coordinates> possibleDstBuilder1forDome, Set<Coordinates> possibleDstBuilder2forDome) {
        JSONObject message = new JSONObject();
        message.put(TYPE, POSSIBLE_BUILD_DESTINATIONS);

        JSONArray possibleDst = new JSONArray();
        possibleDst.put(fromCollection(possibleDstBuilder1));
        possibleDst.put(fromCollection(possibleDstBuilder2));
        possibleDst.put(fromCollection(possibleDstBuilder1forDome));
        possibleDst.put(fromCollection(possibleDstBuilder2forDome));

        message.put(POSSIBLE_DST, possibleDst);

        return message.toString();

    }

    public static String possibleMoveDestinations(Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2) {
        JSONObject message = new JSONObject();
        message.put(TYPE, POSSIBLE_MOVE_DESTINATIONS);

        JSONArray possibleDst = new JSONArray();
        possibleDst.put(fromCollection(possibleDstBuilder1));
        possibleDst.put(fromCollection(possibleDstBuilder2));

        message.put(POSSIBLE_DST, possibleDst);


        return message.toString();
    }

    public static String errorMessage(String errorMessage) {
        JSONObject message = new JSONObject();
        message.put(TYPE,  ERROR);
        message.put(DESCRIPTION, errorMessage);

        return message.toString();
    }

    public static String lostGame(String nickname) {
        JSONObject message = new JSONObject();
        message.put(TYPE, LOST_UPDATE).put(NAME, nickname);

        return message.toString();
    }

    public static String endGame(String winner) {
        JSONObject message = new JSONObject();
        message.put(TYPE, ENDGAME);
        message.put(WINNER, winner);

        return message.toString();
    }

    /**
     * Used
     * @param src
     * @param dst
     * @return
     */
    public static String move(String nickname, Coordinates src, Coordinates dst) {
        JSONObject message = new JSONObject();
        message.put(TYPE, MOVE);
        message.put(NAME, nickname);
        message.put(SRC, fromCoordinates(src));
        message.put(DST, fromCoordinates(dst));

        return message.toString();
    }


    public static String move(String nickname, Coordinates src, Coordinates dst, boolean result) {

        return (new JSONObject(move(nickname,src, dst))).put(RESULT, result).toString();
    }

    public static String builderPushed(String nickname, Coordinates src, Coordinates dst) {
        return (new JSONObject()).put(TYPE, BUILDER_PUSHED).put(NAME, nickname).put(SRC, fromCoordinates(src)).put(DST, fromCoordinates(dst)).toString();
    }


    public static String build(String nickname, Coordinates src, Coordinates dst, boolean buildDome) {
        JSONObject message = new JSONObject();
        message.put(TYPE, BUILD);
        message.put(NAME, nickname);
        message.put(SRC, fromCoordinates(src));
        message.put(DST, fromCoordinates(dst));
        message.put(BUILD_DOME, buildDome);

        return message.toString();
    }

    public static String build(String nickname, Coordinates src, Coordinates dst, boolean buildDome, boolean result) {

        return (new JSONObject(build(nickname, src, dst, buildDome))).put(RESULT, result).toString();
    }


    public static String buildersPlacement(String nickname, Coordinates positionBuilder1, Coordinates positionBuilder2, boolean result) {
        return (new JSONObject(buildersPlacement(nickname, positionBuilder1, positionBuilder2))).put(RESULT, result).toString();
    }

    public static String buildersPlacement(String nickname, Coordinates positionBuilder1, Coordinates positionBuilder2) {
        JSONObject message = new JSONObject();
        message.put(TYPE, BUILDERS_PLACEMENT);
        message.put(NAME, nickname);

        JSONArray positions = new JSONArray();
        positions.put(fromCoordinates(positionBuilder1));
        positions.put(fromCoordinates(positionBuilder2));

        message.put(POSITIONS, positions);

        return message.toString();
    }

    public static String disconnect() {
        return (new JSONObject()).put(TYPE, DISCONNECT).toString();
    }



    public static String stepChoice(String nickname, String choice, boolean result) {
        return (new JSONObject(stepChoice(nickname, choice))).put(RESULT, result).toString();
    }

    public static String stepChoice(String nickname, String choice) {
        JSONObject message = new JSONObject();
        message.put(TYPE, SET_STEP_CHOICE);
        message.put(NAME, nickname);
        message.put(STEP_CHOICE, choice);

        return message.toString();
    }

    public static String setNumberOfPlayers(int number) {
        JSONObject message = new JSONObject();
        message.put(TYPE, SET_NUMBER_OF_PLAYERS);
        message.put(NUMBER_OF_PLAYERS, number);

        return message.toString();
    }

    public static String addPlayer(String name, String birthday) {
        JSONObject message = new JSONObject();

        message.put(TYPE, ADD_PLAYER);
        message.put(NAME, name);
        message.put(DATE, birthday);

        return message.toString();
    }

    public static String askNickAndDate() {
        return (new JSONObject()).put(TYPE, ASK_NICK_AND_DATE).toString();
    }

    public static String askColor(Set<String> chosenColors) {
        JSONObject message = new JSONObject();

        message.put(TYPE, ASK_COLOR);
        message.put(CHOSEN_COLORS, chosenColors);

        return message.toString();
    }

    public static String askGod(Map<String, String> godDescriptions, Set<String> chosenGodCards) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(TYPE, ASK_GOD);

        jsonObject.put(GOD_DESCRIPTIONS, godDescriptions);
        jsonObject.put(CHOSEN_GOD_CARDS, chosenGodCards);

        return jsonObject.toString();

    }

    public static String askStep(ArrayList<String> stateList){
        JSONObject message = new JSONObject();
        JSONArray arr = new JSONArray();
        for (String s : stateList)
            arr.put(s);
        return message.put(TYPE, ASK_STEP).put(POSSIBLE_STEPS, arr).toString();
    }

    public static String askNumberOfPlayers() {
        return (new JSONObject()).put(TYPE, ASK_NUMBER_OF_PLAYERS).toString();
    }

    public static String askBuilders(){ return (new JSONObject()).put(TYPE, ASK_BUILDERS).toString(); }

    public static String askStep(){
        return (new JSONObject()).put(TYPE, ASK_STEP).toString();
    }

    public static String chooseStartPlayer(Set<String> players) {
        return (new JSONObject()).put(TYPE, CHOOSE_START_PLAYER).put(PLAYERS, players).toString();
    }

    public static String setStartPlayer(String nickname, boolean result) {
        return (new JSONObject(setStartPlayer(nickname))).put(RESULT, result).toString();
    }

    public static String setStartPlayer(String nickname) {
        return (new JSONObject()).put(TYPE, SET_START_PLAYER).put(NAME, nickname).toString();
    }


    public static String turnUpdate(String nickname){
        return (new JSONObject()).put(TYPE, TURN_UPDATE).put(NAME, nickname).toString();
    }

    public static String colorUpdate(String color) {
        return (new JSONObject()).put(TYPE, COLOR_UPDATE).put(COLOR, color).toString();
    }

    public static String colorUpdate(String nickname, String color, boolean result){
        return (new JSONObject(colorUpdate(color))).put(NAME, nickname).put(RESULT, result).toString();
    }

    public static String stepUpdate(Model.State state) {
        return (new JSONObject()).put(TYPE, STATE_UPDATE).put(STATE, state.toString()).toString();
    }

    public static String playerAdded(String nickname, boolean result) {
        return (new JSONObject()).put(TYPE, PLAYER_ADDED).put(NAME, nickname).put(RESULT, result).toString();
    }




    public static String godCardAssigned(String nickname, String godCard, boolean result){
        return (new JSONObject()).put(TYPE, GOD_CARD_ASSIGNED).put(NAME, nickname).put(GOD_CARD, godCard).put(RESULT, result).toString();
    }

    public static String setGodCard(String godCardName) {
        return (new JSONObject()).put(TYPE, SET_GOD_CARD).put(GOD_CARD, godCardName).toString();
    }

    public static String info(String message) {
        return ((new JSONObject()).put(TYPE, INFO).put(MESSAGE, message).toString());
    }

    public static String chooseMatchGodCards(int numOfPlayers, Map<String, String> godDescriptions) {
        return (new JSONObject()).put(NUMBER_OF_PLAYERS, numOfPlayers).put(GOD_DESCRIPTIONS, godDescriptions).put(TYPE, CHOOSE_MATCH_GOD_CARDS).toString();
    }

    public static String setGodCardsToUse(String nickname, Set<String> godNames, boolean result) {
        return (new JSONObject(setGodCardsToUse(nickname, godNames))).put(RESULT, result).toString();
    }

    public static String setGodCardsToUse(String nickname, Set<String> godNames) {
        JSONObject message = new JSONObject();
        JSONArray arr = new JSONArray(godNames);

        message.put(TYPE, SET_MATCH_GOD_CARDS);
        message.put(GOD_DESCRIPTIONS, arr);
        message.put(NAME, nickname);

        return message.toString();

    }
    //--------------------------------
    //KEEP-ALIVE Connection Messages

    public static String ping() {
        return (new JSONObject()).put(TYPE, PING).toString();
    }
    public static String pong() {
        return (new JSONObject()).put(TYPE, PONG).toString();
    }

    //--------------------------------

    public static String errorNumber(){
        return (new JSONObject()).put(TYPE, ERROR_NUMBER).toString();
    }

    public static String parseErrorNumber() {return (new JSONObject()).put(TYPE, PARSE_ERROR_NUMBER).toString();}

    public static String parseErrorColor() {return (new JSONObject()).put(TYPE, PARSE_ERROR_COLOR).toString();}

    public static String parseErrorMove() {return (new JSONObject()).put(TYPE, PARSE_ERROR_MOVE).toString();}

    public static String parseErrorBuild() {return (new JSONObject()).put(TYPE, PARSE_ERROR_BUILD).toString();}

    public static String parseErrorGod() {return (new JSONObject()).put(TYPE, PARSE_ERROR_GOD).toString();}

    public static String parseErrorBuilders() {return (new JSONObject()).put(TYPE, PARSE_ERROR_BUILDERS).toString();}

    public static String parseErrorStepChoice() {return (new JSONObject()).put(TYPE, PARSE_ERROR_STEP_CHOICE).toString();}

    public static String parseErrorPlayer() {return (new JSONObject()).put(TYPE, PARSE_ERROR_PLAYER).toString();}

}
