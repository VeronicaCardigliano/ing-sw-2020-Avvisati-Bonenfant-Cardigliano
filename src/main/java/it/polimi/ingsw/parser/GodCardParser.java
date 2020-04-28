package it.polimi.ingsw.parser;

import it.polimi.ingsw.model.IslandBoard;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.godCards.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Parses a json file with GodCard properties.
 * GodCardParser offers one factory method to create GodCards given the player's name and the god's name.
 */
public class GodCardParser {
    private final JSONObject jsonObject;
    private final Set<String> godNames;


    public GodCardParser(String path) {
        String jsonString = null;
        try {
            //I need to save the file content in a string
            jsonString = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (jsonString != null)
            this.jsonObject = new JSONObject(jsonString);
        else
            this.jsonObject = new JSONObject("{}");
        godNames = jsonObject.keySet();
    }

    public Set<String> getGodNames() {
        return godNames;
    }

    public String getDescription (String godCardName) {
            return jsonObject.getJSONObject(godCardName).getString("description");
    }

    public GodCard createCard(Player player, String godName) {
        GodCard cardCreated;
        JSONObject godObject;
        ArrayList<ArrayList<String>> states;

        Map<String, Boolean> flagParameters = new HashMap<>();
        Map<String, Integer> intParameters = new HashMap<>();

        if(godNames.contains(godName)) {
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
                    intParameters.put("numberOfBuilds", godObject.opt("numberOfBuilds") != null ? godObject.getInt("numberOfBuilds") : 1);

                    cardCreated = new YourBuildGodCard(player, name, description, states, flagParameters, intParameters);
                    break;

                case "OPPONENT":
                    flagParameters.put("activeOnMoveUp", godObject.opt("activeOnMoveUp") != null && godObject.getBoolean("activeOnMoveUp"));
                    flagParameters.put("blockMoveUp", godObject.opt("blockMoveUp") != null && godObject.getBoolean("blockMoveUp"));
                    cardCreated = new OpponentTurnGodCard(player, name, description, states, flagParameters, intParameters);
                    break;

                case "TURN":
                    flagParameters.put("blockMovingUpIfBuilt", godObject.opt("blockMovingUpIfBuilt") != null &&
                            godObject.getBoolean("blockMovingUpIfBuilt"));
                    cardCreated = new YourTurnGodCard(player, name, description, states, flagParameters, intParameters);
                    break;

                case "WIN":
                    intParameters.put("minimumDownStepsToWin", godObject.opt("minimumDownStepsToWin") != null ?
                            godObject.getInt("minimumDownStepsToWin") : 3);
                    intParameters.put("completeTowersToWin", godObject.opt("completeTowersToWin") != null ?
                            godObject.getInt("completeTowersToWin") : IslandBoard.dimension * IslandBoard.dimension + 1);

                    cardCreated = new WinConditionGodCard(player, name, description, states, flagParameters, intParameters);
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
