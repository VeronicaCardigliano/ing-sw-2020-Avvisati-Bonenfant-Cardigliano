package it.polimi.ingsw.server.parser;

import it.polimi.ingsw.server.model.gameMap.IslandBoard;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.godCards.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Parses a json file with GodCard properties.
 * GodCardParser offers one factory method to create GodCards given the player's name and the god's name.
 */
public class GodCardParser {
    private final JSONObject jsonObject;
    private final Map<String, String> godDescriptions = new HashMap<>();


    public GodCardParser(String path) {
        String jsonString = null;
        Set<String> godNames;


        try {
            InputStream fileStream = getClass().getResourceAsStream(path);
            jsonString = new String(fileStream.readAllBytes());
        } catch (NullPointerException | IOException e) {
            System.out.println("Configuration file not found");
        }


        if (jsonString != null)
            this.jsonObject = new JSONObject(jsonString);
        else
            this.jsonObject = new JSONObject("{}");

        godNames = jsonObject.keySet();

        for(String godName : godNames)
            godDescriptions.put(godName, getDescription(jsonObject, godName));

    }

    public Map<String, String> getGodDescriptions() {
        return godDescriptions;
    }


    private static String getDescription (JSONObject jsonObject, String godCardName) {
            return jsonObject.getJSONObject(godCardName).getString("description");
    }

    public GodCard createCard(Player player, String godName) {
        GodCard cardCreated;
        JSONObject godObject;
        ArrayList<ArrayList<String>> states;

        Map<String, Boolean> flagParameters = new HashMap<>();
        Map<String, Integer> intParameters = new HashMap<>();

        if(godDescriptions.containsKey(godName)) {
            godObject = jsonObject.getJSONObject(godName);

            //possible attributes

            String name = godObject.opt("name") != null ? godObject.getString("name") : "default";
            String description = godObject.opt("description") != null ?
                    godObject.getString("description") : "default";
            states = parseStates(godObject);

            switch (godObject.getString("type").toUpperCase()) {
                case "MOVE":
                    intParameters.put("pushForce", godObject.opt("pushForce") != null ? godObject.getInt("pushForce") : 0);
                    flagParameters.put("secondMoveDiffDst", godObject.opt("secondMoveDiffDst") != null &&
                            godObject.getBoolean("secondMoveDiffDst"));
                    flagParameters.put("extraMovePerimeter", godObject.opt("extraMovePerimeter") != null &&
                            godObject.getBoolean("extraMovePerimeter"));

                    cardCreated = new YourMoveGodCard(player, name, description, states, flagParameters, intParameters);
                    break;

                case "BUILD":
                    flagParameters.put("canBuildDomeEverywhere", godObject.opt("canBuildDomeEverywhere") != null &&
                            godObject.getBoolean("canBuildDomeEverywhere"));
                    flagParameters.put("secondBuildNotDome", godObject.opt("secondBuildNotDome") != null &&
                            godObject.getBoolean("secondBuildNotDome"));
                    flagParameters.put("secondBuildDiffDest", godObject.opt("secondBuildDiffDest") != null &&
                            godObject.getBoolean("secondBuildDiffDest"));
                    flagParameters.put("blockUnderItself", godObject.opt("blockUnderItself") != null &&
                            godObject.getBoolean("blockUnderItself"));
                    flagParameters.put("extraBuildNotPerimeter", godObject.opt("extraBuildNotPerimeter") != null &&
                            godObject.getBoolean("extraBuildNotPerimeter"));
                    intParameters.put("numberOfBuilds", godObject.opt("numberOfBuilds") != null ? godObject.getInt("numberOfBuilds") : 1);

                    cardCreated = new YourBuildGodCard(player, name, description, states, flagParameters, intParameters);
                    break;

                case "OPPONENT":
                    flagParameters.put("activeOnMoveUp", godObject.opt("activeOnMoveUp") != null && godObject.getBoolean("activeOnMoveUp"));
                    flagParameters.put("blockMoveUp", godObject.opt("blockMoveUp") != null && godObject.getBoolean("blockMoveUp"));
                    flagParameters.put("limusPower", godObject.opt("limusPower") != null && godObject.getBoolean(
                            "limusPower"));
                    flagParameters.put("alwaysActive", godObject.opt("alwaysActive") != null && godObject.getBoolean(
                            "alwaysActive"));
                    cardCreated = new OpponentTurnGodCard(player, name, description, states, flagParameters, intParameters);

                    break;

                case "TURN":
                    flagParameters.put("blockMovingUpIfBuilt", godObject.opt("blockMovingUpIfBuilt") != null &&
                            godObject.getBoolean("blockMovingUpIfBuilt"));
                    cardCreated = new YourTurnGodCard(player, name, description, states, flagParameters);
                    break;

                case "WIN":
                    intParameters.put("minimumDownStepsToWin", godObject.opt("minimumDownStepsToWin") != null ?
                            godObject.getInt("minimumDownStepsToWin") : 3);
                    intParameters.put("completeTowersToWin", godObject.opt("completeTowersToWin") != null ?
                            godObject.getInt("completeTowersToWin") : IslandBoard.dimension * IslandBoard.dimension + 1);

                    cardCreated = new WinConditionGodCard(player, name, description, states, intParameters);
                    break;

                default:
                    cardCreated = new GodCard(player, name, description, states);
            }
        } else {
            states = parseStates(null);
            cardCreated = new GodCard(player, "default", "default", states);
        }

        return cardCreated;
    }

    private ArrayList<ArrayList<String>> parseStates(JSONObject jsonObject) {

        //initialize states attribute which contains all possible states configurations

        ArrayList<ArrayList<String>> states = new ArrayList<>();

        if(jsonObject != null && jsonObject.opt("states") != null) {

            JSONArray outerList = jsonObject.getJSONArray("states");
            ArrayList<String> innerList;

            //converts outerList into an ArrayList (states)
            for(int i = 0; i < outerList.length(); i++) {
                innerList = new ArrayList<>();

                for (int j = 0; j < outerList.getJSONArray(i).length(); j++)
                    innerList.add(outerList.getJSONArray(i).getString(j));

                states.add(innerList);

            }


        } else {
            ArrayList<String> list = new ArrayList<>();
            list.add("MOVE");
            list.add("BUILD");
            states.add(list);
        }

        return states;
    }

}
