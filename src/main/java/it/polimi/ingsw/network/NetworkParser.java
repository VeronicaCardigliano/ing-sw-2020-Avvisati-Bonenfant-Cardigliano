package it.polimi.ingsw.network;


import it.polimi.ingsw.server.model.gameMap.Coordinates;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * NetworkParser objects are created from JSONObject objects and offer different functions to parse the json String associated to the JSONObject
 */
public class NetworkParser {

    private final JSONObject jsonObject; //JSONObject to parse

    public NetworkParser(String jsonString){

        jsonObject = new JSONObject(jsonString);

    }

    /**
     * Extract from json value at key attribute
     * @param attribute key to look for
     */
    public String getAttribute(String attribute) {
        return jsonObject.getString(attribute);
    }

    /**
     * Extract request type from jsonObject
     */
    public String getRequest(){
        return jsonObject.getString(Messages.TYPE);
    }

    /**
     * Extract color value from jsonObject
     */
    public String getColor() {
        return jsonObject.getString(Messages.COLOR);
    }

    /**
     * Extract source value from jsonObject
     */
    public Coordinates getSrcCoordinates(){
        int i = jsonObject.getJSONObject(Messages.SRC).getInt("i");
        int j = jsonObject.getJSONObject(Messages.SRC).getInt("j");
        return new Coordinates(i,j);
    }

    /**
     * Extract destination value from jsonObject
     */
    public Coordinates getDstCoordinates(){
        int i = jsonObject.getJSONObject(Messages.DST).getInt("i");
        int j = jsonObject.getJSONObject(Messages.DST).getInt("j");
        return new Coordinates(i,j);
    }

    /**
     * Extract buildDome boolean value from jsonObject
     */
    public boolean getBuildDome(){
        return jsonObject.getBoolean(Messages.BUILD_DOME);
    }

    /**
     * Extract number of players value from jsonObject
     */
    public int getNumberOfPlayers(){
        return jsonObject.getInt(Messages.NUMBER_OF_PLAYERS);
    }


    /**
     * Extract date value from jsonObject
     */
    public String getDate() {
        return jsonObject.getString(Messages.DATE);
    }

    /**
     * Extract result boolean value from jsonObject
     */
    public boolean getResult() {
        return jsonObject.getBoolean(Messages.RESULT);
    }

    /**
     * Extract name value from jsonObject
     */
    public String getName() { return jsonObject.getString(Messages.NAME);}

    /**
     * Extract step choice value from jsonObject
     */
    public String getStepChoice() { return jsonObject.getString(Messages.STEP_CHOICE);}

    /**
     * Extract coordinate value from jsonObject
     */
    private Coordinates fromJSONObject(JSONObject coordJSON) {
        return new Coordinates(coordJSON.getInt("i"), coordJSON.getInt("j"));
    }

    /**
     * create a Set of Coordinates from a JSONArray
     */
    private Set<Coordinates> fromJSONArray(JSONArray array) {
        Set<Coordinates> set = new HashSet<>();

        for(int i = 0; i < array.length(); i++)
            set.add(fromJSONObject(array.getJSONObject(i)));

        return set;

    }

    /**
     * extract list of coordinates from jsonObject
     */
    public ArrayList<Coordinates> getCoordArray() {
        JSONArray arr = jsonObject.getJSONArray(Messages.POSITIONS);
        ArrayList<Coordinates> list = new ArrayList<>();

        for(int i = 0; i < arr.length(); i++)
            list.add(fromJSONObject(arr.getJSONObject(i)));

        return list;

    }

    /**
     * extract list of sets of coordinates from jsonObject
     */
    public ArrayList<Set<Coordinates>> getCoordSetList() {
        JSONArray arr = jsonObject.getJSONArray(Messages.POSSIBLE_DST);
        ArrayList<Set<Coordinates>> list = new ArrayList<>();

        for(int i = 0; i < arr.length(); i++)
            list.add(fromJSONArray(arr.getJSONArray(i)));

        return list;
    }

    /**
     * extract god card name value from jsonObject
     */
    public String getGodCardName() {
        return jsonObject.getString(Messages.GOD_CARD);
    }

    public Map<String, String> getGodDescriptions() {
        JSONObject obj = jsonObject.getJSONObject(Messages.GOD_DESCRIPTIONS);

        Map<String, String> godDescriptions = new HashMap<>();
        for(String key : obj.keySet())
            godDescriptions.put(key, obj.getString(key));

        return godDescriptions;
    }

    /**
     * extract Set of strings from array in jsonObject
     * @param key key position of the jsonArray contained in jsonObject
     */
    public Set<String> getSetFromArray(String key) {
        Set<String> set = new HashSet<>();

        JSONArray arr = jsonObject.getJSONArray(key);

        for(int i = 0; i < arr.length(); i++)
            set.add(arr.getString(i));

        return set;
    }

}

