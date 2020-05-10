package it.polimi.ingsw.server.parser;

import it.polimi.ingsw.server.model.gameMap.Coordinates;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Set;

public abstract class Messages {

    private static final String TYPE = "type";
    private static final String MOVE = "MOVE";
    private static final String BUILD = "BUILD";
    private static final String BUILDERS_PLACEMENT = "BUILDERS_PLACEMENT";
    private static final String ERROR = "ERROR";
    private static final String POSSIBLE_BUILD_DESTINATIONS = "POSSIBLE_BUILD_DESTINATIONS";
    private static final String POSSIBLE_MOVE_DESTINATIONS = "POSSIBLE_MOVE_DESTINATIONS";
    private static final String ENDGAME = "ENDGAME";
    private static final String LOST = "LOST";
    private static final String WINNER = "winner";

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
        message.put("possibleDstBuilder1", fromCollection(possibleDstBuilder1));
        message.put("possibleDstBuilder2", fromCollection(possibleDstBuilder2));
        message.put("possibleDstBuilder1forDome", fromCollection(possibleDstBuilder1forDome));
        message.put("possibleDstBuilder2forDome", fromCollection(possibleDstBuilder2forDome));

        return message.toString();

    }

    public static String possibleMoveDestinations(Set<Coordinates> possibleDstBuilder1, Set<Coordinates> possibleDstBuilder2) {
        JSONObject message = new JSONObject();
        message.put(TYPE, POSSIBLE_MOVE_DESTINATIONS);
        message.put("possibleDstBuilder1", fromCollection(possibleDstBuilder1));
        message.put("possibleDstBuilder2", fromCollection(possibleDstBuilder2));

        return message.toString();
    }

    public static String errorMessage(String errorMessage) {
        JSONObject message = new JSONObject();
        message.put(TYPE,  ERROR);
        message.put("description", errorMessage);

        return message.toString();
    }

    public static String lostGame() {
        JSONObject message = new JSONObject();
        message.put(TYPE, LOST);

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
        message.put("player", player);
        message.put("src", fromCoordinates(src));
        message.put("dst", fromCoordinates(dst));


        return message.toString();
    }

    public static String build(String player, Coordinates src, Coordinates dst, boolean buildDome) {
        JSONObject message = new JSONObject();
        message.put(TYPE, BUILD);
        message.put("player", player);
        message.put("src", fromCoordinates(src));
        message.put("dst", fromCoordinates(dst));
        message.put("buildDome", buildDome);

        return message.toString();
    }

    public static String buildersPlacement(String nickname, Coordinates positionBuilder1, Coordinates positionBuilder2) {
        JSONObject message = new JSONObject();
        message.put(TYPE, BUILDERS_PLACEMENT);
        message.put("player", nickname);
        message.put("positionBuilder1", fromCoordinates(positionBuilder1));
        message.put("positionBuilder2", fromCoordinates(positionBuilder2));

        return message.toString();
    }


}
