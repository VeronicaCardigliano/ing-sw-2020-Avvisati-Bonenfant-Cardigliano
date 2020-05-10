package it.polimi.ingsw.server.parser;

//Parses a string containing a json to evaluate e request coming from the view

import it.polimi.ingsw.server.model.gameMap.Coordinates;
import org.json.JSONObject;

public class NetworkParser {

    private final JSONObject jsonObject;

    public NetworkParser(String jsonString){

        jsonObject = new JSONObject(jsonString);

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

    public String getName() { return jsonObject.getString(Messages.NAME);}

    public String getStepChoice() { return jsonObject.getString(Messages.STEP_CHOICE);}

}

