package it.polimi.ingsw.server.parser;

//Parses a string containing a json to evaluate e request coming from the view

import it.polimi.ingsw.server.model.gameMap.Coordinates;
import org.json.JSONObject;
import org.json.JSONArray;

//TODO return an error if some information are missing
public class NetworkParser {

    private JSONObject jsonObject;

    public NetworkParser(String jsonString){

        jsonObject = new JSONObject(jsonString);

    }


    public String getRequest(){
        return jsonObject.getString("action").toUpperCase();
    }

    public String getColor(){
        return jsonObject.getString("color").toUpperCase();
    }

    //"coordinates": [ [i_src, j_src], [i_dst, j_dst] ],
    public Coordinates getSrcCoordinates(){
        int i = jsonObject.getJSONArray("coordinates").getJSONArray(0).getInt(0);
        int j = jsonObject.getJSONArray("coordinates").getJSONArray(0).getInt(1);
        return new Coordinates(i,j);
    }

    public Coordinates getDstCoordinates(){
        int i = jsonObject.getJSONArray("coordinates").getJSONArray(1).getInt(0);
        int j = jsonObject.getJSONArray("coordinates").getJSONArray(1).getInt(1);
        return new Coordinates(i,j);
    }

    public boolean getBuildDome(){
        return jsonObject.getBoolean("buildDome");
    }

    public int getNumberOfPlayers(){
        return jsonObject.getInt("numberOfPlayers");
    }


    public String getDate(){
        return jsonObject.getString("date");
    }

}
