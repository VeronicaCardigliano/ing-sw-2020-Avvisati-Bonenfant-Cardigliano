package it.polimi.ingsw.server.parser;

//Parses a string containing a json to evaluate e request coming from the view

import it.polimi.ingsw.server.model.gameMap.Coordinates;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class NetworkParser {

    private final JSONObject jsonObject;

    public NetworkParser(String jsonString){

        jsonObject = new JSONObject(jsonString);

    }

    public String getAttribute(String attribute) {
        return jsonObject.getString(attribute);
    }

    public String getRequest(){
        return jsonObject.getString(Messages.TYPE);
    }

    public String getColor(){
        return jsonObject.getString(Messages.COLOR);
    }

    //"coordinates": [ [i_src, j_src], [i_dst, j_dst] ],
    public Coordinates getSrcCoordinates(){
        int i = jsonObject.getJSONObject(Messages.SRC).getInt("i");
        int j = jsonObject.getJSONObject(Messages.SRC).getInt("j");
        return new Coordinates(i,j);
    }

    public Coordinates getDstCoordinates(){
        int i = jsonObject.getJSONObject(Messages.DST).getInt("i");
        int j = jsonObject.getJSONObject(Messages.DST).getInt("j");
        return new Coordinates(i,j);
    }

    public boolean getBuildDome(){
        return jsonObject.getBoolean(Messages.BUILD_DOME);
    }

    public int getNumberOfPlayers(){
        return jsonObject.getInt(Messages.NUMBER_OF_PLAYERS);
    }


    public String getDate(){
        return jsonObject.getString(Messages.DATE);
    }

    public boolean getResult() {
        return jsonObject.getBoolean(Messages.RESULT);
    }

    public String getName() { return jsonObject.getString(Messages.NAME);}

    public String getStepChoice() { return jsonObject.getString(Messages.STEP_CHOICE);}

    private Coordinates fromJSONObject(JSONObject coordJSON) {
        return new Coordinates(coordJSON.getInt("i"), coordJSON.getInt("j"));
    }

    /**
     * create a Set of Coordinates from a JSONArray
     * @return
     */
    private Set<Coordinates> fromJSONArray(JSONArray array) {
        Set<Coordinates> set = new HashSet<>();

        for(int i = 0; i < array.length(); i++)
            set.add(fromJSONObject(array.getJSONObject(i)));

        return set;

    }

    public ArrayList<Coordinates> getCoordArray() {
        JSONArray arr = jsonObject.getJSONArray(Messages.POSITIONS);
        ArrayList<Coordinates> list = new ArrayList<>();

        for(int i = 0; i < arr.length(); i++)
            list.add(fromJSONObject(arr.getJSONObject(i)));

        return list;

    }


    public ArrayList<Set<Coordinates>> getCoordSetList() {
        JSONArray arr = jsonObject.getJSONArray(Messages.POSSIBLE_DST);
        ArrayList<Set<Coordinates>> list = new ArrayList<>();

        for(int i = 0; i < arr.length(); i++)
            list.add(fromJSONArray(arr.getJSONArray(i)));

        return list;
    }

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

    public Set<String> getSetFromArray(String key) {
        Set<String> set = new HashSet<>();

        JSONArray arr = jsonObject.getJSONArray(key);

        for(int i = 0; i < arr.length(); i++)
            set.add(arr.getString(i));

        return set;
    }

}

