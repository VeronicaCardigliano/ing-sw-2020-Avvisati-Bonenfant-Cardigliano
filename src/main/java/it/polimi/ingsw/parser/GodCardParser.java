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
import java.util.Set;

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


    public GodCard createCard( Player player, String godName) {
        GodCard cardCreated;
        JSONObject godObject;
        ArrayList<ArrayList<String>> states;

        if(godNames.contains(godName)) {
            godObject = jsonObject.getJSONObject(godName);

            //possible attributes

            String name = godObject.opt("name") != null ? godObject.getString("name") : "default";
            String description = godObject.opt("description") != null ? godObject.getString("description") : "default";
            states = parseStates(godObject);

            switch (godObject.getString("type").toUpperCase()) {
                case "MOVE":
                    int pushForce = godObject.opt("pushForce") != null ? godObject.getInt("pushForce") : 0;
                    boolean secondMoveDiffDest = godObject.opt("secondMoveDiffDest") != null && godObject.getBoolean("secondMoveDiffDest");

                    cardCreated = new YourMoveGodCard(player, name, description, states, pushForce, secondMoveDiffDest);
                    break;

                case "BUILD":
                    boolean canBuildDomeEverywhere = godObject.opt("canBuildDomeEverywhere") != null && godObject.getBoolean("canBuildDomeEverywhere");
                    boolean secondBuildNotDome = godObject.opt("secondBuildNotDome") != null && godObject.getBoolean("secondBuildNotDome");
                    boolean secondBuildDiffDest = godObject.opt("secondBuildDiffDest") != null && godObject.getBoolean("secondBuildDiffDest");
                    int numberOfBuilds = godObject.opt("numberOfBuilds") != null ? godObject.getInt("numberOfBuilds") : 1;

                    cardCreated = new YourBuildGodCard(player, name, description, states, numberOfBuilds, canBuildDomeEverywhere, secondBuildDiffDest, secondBuildNotDome);
                    break;

                case "OPPONENT":
                    boolean activeOnMoveUp = godObject.opt("activeOnMoveUp") != null && godObject.getBoolean("activeOnMoveUp");
                    boolean blockMoveUp = godObject.opt("blockMoveUp") != null && godObject.getBoolean("blockMoveUp");
                    cardCreated = new OpponentTurnGodCard(player, name, description, states, activeOnMoveUp, blockMoveUp);
                    break;

                case "TURN":
                    cardCreated = new YourTurnGodCard(player, name, description, states);
                    break;

                case "WIN":
                    int minimumDownStepsToWin = godObject.opt("minimumDownStepsToWin") != null ? godObject.getInt("minimumDownStepsToWin") : 3;
                    int completeTowersToWin = godObject.opt("completeTowersToWin") != null ? godObject.getInt("completeTowersToWin") : IslandBoard.dimension * IslandBoard.dimension + 1;

                    cardCreated = new WinConditionGodCard(player, name, description, states, minimumDownStepsToWin, completeTowersToWin);
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

    public ArrayList<ArrayList<String>> parseStates(JSONObject jsonObject) {

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
