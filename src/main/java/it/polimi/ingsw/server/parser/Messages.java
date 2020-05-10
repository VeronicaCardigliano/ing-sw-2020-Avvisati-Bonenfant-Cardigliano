package it.polimi.ingsw.server.parser;

import it.polimi.ingsw.server.model.gameMap.Coordinates;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Set;

public abstract class Messages {

    public static final String TYPE = "type";

    //type values
    public static final String MOVE = "move";
    public static final String BUILD = "build";
    public static final String BUILDERS_PLACEMENT = "buildersPlacement";
    public static final String POSSIBLE_BUILD_DESTINATIONS = "possibleBuildDestinations";
    public static final String POSSIBLE_MOVE_DESTINATIONS = "possibleMoveDestinations";
    public static final String ERROR = "error";
    public static final String ENDGAME = "endGame";
    public static final String SET_BUILDERS = "setBuilders";
    public static final String SET_STEP_CHOICE = "setStepChoice";
    public static final String SET_COLOR = "setColor";
    public static final String LOST_UPDATE = "lostUpdate";
    public static final String SET_NUMBER_OF_PLAYERS = "setNumberOfPlayers";
    public static final String ADD_PLAYER = "addPlayer";
    public static final String DELETE_PLAYER = "deletePlayer";
    public static final String DISCONNECT = "disconnect";

    //key values
    public static final String COLOR = "color"; //for SET_COLOR
    public static final String WINNER = "winner"; //for END_GAME
    public static final String SRC = "src";
    public static final String DST = "dst";
    public static final String BUILD_DOME = "buildDome";
    public static final String NUMBER_OF_PLAYERS = "numberOfPlayers"; //FOR SET_NUMBER_OF_PLAYERS
    public static final String DATE = "date"; //for ADD_PLAYER
    public static final String NAME = "name"; //for ADD_PLAYER
    public static final String PLAYER = "player";
    public static final String DESCRIPTION = "description"; //for ERROR
    public static final String POSSIBLE_DST = "possibleDst";
    public static final String STEP_CHOICE = "stepChoice";

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
        /*message.put("possibleDstBuilder1", fromCollection(possibleDstBuilder1));
        message.put("possibleDstBuilder2", fromCollection(possibleDstBuilder2));
        message.put("possibleDstBuilder1forDome", fromCollection(possibleDstBuilder1forDome));
        message.put("possibleDstBuilder2forDome", fromCollection(possibleDstBuilder2forDome));*/

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

    public static String move(String player, Coordinates src, Coordinates dst) {
        JSONObject message = new JSONObject();
        message.put(TYPE, MOVE);
        message.put(PLAYER, player);
        message.put(SRC, fromCoordinates(src));
        message.put(DST, fromCoordinates(dst));


        return message.toString();
    }

    public static String build(String player, Coordinates src, Coordinates dst, boolean buildDome) {
        JSONObject message = new JSONObject();
        message.put(TYPE, BUILD);
        message.put(PLAYER, player);
        message.put(SRC, fromCoordinates(src));
        message.put(DST, fromCoordinates(dst));
        message.put(BUILD_DOME, buildDome);

        return message.toString();
    }

    public static String buildersPlacement(String nickname, Coordinates positionBuilder1, Coordinates positionBuilder2) {
        JSONObject message = new JSONObject();
        message.put(TYPE, BUILDERS_PLACEMENT);
        message.put(PLAYER, nickname);
        message.put("positionBuilder1", fromCoordinates(positionBuilder1));
        message.put("positionBuilder2", fromCoordinates(positionBuilder2));

        return message.toString();
    }

    public static String disconnect() {
        return (new JSONObject()).put(TYPE, DISCONNECT).toString();
    }

    public static String deletePlayer() {
        return (new JSONObject()).put(TYPE, DELETE_PLAYER).toString();
    }

    public static String stepChoice(String choice) {
        JSONObject message = new JSONObject();
        message.put(TYPE, SET_STEP_CHOICE);
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



}
