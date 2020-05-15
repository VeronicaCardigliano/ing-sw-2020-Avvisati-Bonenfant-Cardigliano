package it.polimi.ingsw.server.parser;

import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.gameMap.Coordinates;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public abstract class Messages {

    public static final String TYPE = "type";

    //type values
    public static final String MOVE = "move";
    public static final String BUILD = "build";
    public static final String BUILDERS_PLACEMENT = "buildersPlacement";
    public static final String POSSIBLE_BUILD_DESTINATIONS = "possibleBuildDestinations";
    public static final String POSSIBLE_MOVE_DESTINATIONS = "possibleMoveDestinations";
    public static final String ENDGAME = "endGame";
    //public static final String SET_BUILDERS = "setBuilders";
    public static final String SET_STEP_CHOICE = "setStepChoice";
    public static final String SET_NUMBER_OF_PLAYERS = "setNumberOfPlayers";
    public static final String SET_GOD_CARD = "setGodCard";
    public static final String ADD_PLAYER = "addPlayer";
    public static final String DISCONNECT = "disconnect";
    public static final String ERROR = "error";
    public static final String ERROR_NUMBER = "errorNumber"; //for ERROR

    public static final String PARS_ERROR_COLOR = "parsErrorColor";
    public static final String PARS_ERROR_MOVE = "parsErrorMove";
    public static final String PARS_ERROR_BUILD = "parsErrorBuild";
    public static final String PARS_ERROR_GOD = "parsErrorGod";
    public static final String PARS_ERROR_STEP_CHOICE = "parsErrorStepChoice";
    public static final String PARS_ERROR_BUILDERS = "parsErrorBuilders";
    public static final String PARS_ERROR_NUMBER = "parsErrorNumber";
    public static final String PARS_ERROR_PLAYER = "parsErrorPlayer";

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


    //key values
    public static final String COLOR = "color"; //for SET_COLOR
    public static final String WINNER = "winner"; //for END_GAME
    public static final String SRC = "src";
    public static final String DST = "dst";
    public static final String BUILD_DOME = "buildDome";
    public static final String NUMBER_OF_PLAYERS = "numberOfPlayers"; //FOR SET_NUMBER_OF_PLAYERS
    public static final String DATE = "date"; //for ADD_PLAYER
    public static final String NAME = "name"; //for ADD_PLAYER
    public static final String DESCRIPTION = "description"; //for ERROR
    public static final String POSSIBLE_DST = "possibleDst";
    public static final String STEP_CHOICE = "stepChoice";
    public static final String POSITIONS = "positions"; //for SET_BUILDERS
    public static final String RESULT = "result";
    public static final String STATE = "state"; //for STATE_UPDATE
    public static final String GOD_CARD = "godCard"; //for SET_GOD_CARD
    public static final String GOD_DESCRIPTIONS = "godDescriptions"; //for ASK_GOD
    public static final String CHOSEN_GOD_CARDS = "chosenGodCards"; //for ASK_GOD
    public static final String CHOSEN_COLORS = "chosenColors"; //for ASK_COLOR


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

    public static String lostGame() {
        JSONObject message = new JSONObject();
        message.put(TYPE, LOST_UPDATE);

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

    public static String askNumberOfPlayers() {
        return (new JSONObject()).put(TYPE, ASK_NUMBER_OF_PLAYERS).toString();
    }

    public static String askBuilders(){ return (new JSONObject()).put(TYPE, ASK_BUILDERS).toString(); }

    public static String askStep(){
        return (new JSONObject()).put(TYPE, ASK_STEP).toString();
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

    public static String errorNumber(){
        return (new JSONObject()).put(TYPE, ERROR_NUMBER).toString();
    }

    public static String parsErrorNumber() {return (new JSONObject()).put(TYPE, PARS_ERROR_NUMBER).toString();}

    public static String parsErrorColor() {return (new JSONObject()).put(TYPE, PARS_ERROR_COLOR).toString();}

    public static String parsErrorMove() {return (new JSONObject()).put(TYPE, PARS_ERROR_MOVE).toString();}

    public static String parsErrorBuild() {return (new JSONObject()).put(TYPE, PARS_ERROR_BUILD).toString();}

    public static String parsErrorGod() {return (new JSONObject()).put(TYPE, PARS_ERROR_GOD).toString();}

    public static String parsErrorBuilders() {return (new JSONObject()).put(TYPE, PARS_ERROR_BUILDERS).toString();}

    public static String parsErrorStepChoice() {return (new JSONObject()).put(TYPE, PARS_ERROR_STEP_CHOICE).toString();}

    public static String parsErrorPlayer() {return (new JSONObject()).put(TYPE, PARS_ERROR_PLAYER).toString();}

}
