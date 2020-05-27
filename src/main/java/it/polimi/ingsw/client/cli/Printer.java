package it.polimi.ingsw.client.cli;

import java.io.PrintStream;

public class Printer {

    private final PrintStream out;

    //stuff to print
    private String state;
    private String infoMessage;
    private String gameMapString;
    private String playersList;
    private String choiceList;
    private String askMessage;


    public Printer(PrintStream out) {
        this.out = out;

    }


    public void setState(String state) {
        this.state = state;
    }

    public void setInfoMessage(String infoMessage) {
        this.infoMessage = infoMessage;
    }

    public void setGameMapString(String gameMapString) {
        this.gameMapString = gameMapString;
    }

    public void setAskMessage(String message) {
        this.askMessage = message;
    }

    public void setPlayersList(String playersList) {
        this.playersList = playersList;
    }

    public void setChoiceList(String choiceList) {
        this.choiceList = choiceList;
    }

    private void printTitle() {
        System.out.println("\n" +
                "░██████╗░█████╗░███╗░░██╗████████╗░█████╗░██████╗░██╗███╗░░██╗██╗\n" +
                "██╔════╝██╔══██╗████╗░██║╚══██╔══╝██╔══██╗██╔══██╗██║████╗░██║██║\n" +
                "╚█████╗░███████║██╔██╗██║░░░██║░░░██║░░██║██████╔╝██║██╔██╗██║██║\n" +
                "░╚═══██╗██╔══██║██║╚████║░░░██║░░░██║░░██║██╔══██╗██║██║╚████║██║\n" +
                "██████╔╝██║░░██║██║░╚███║░░░██║░░░╚█████╔╝██║░░██║██║██║░╚███║██║\n" +
                "╚═════╝░╚═╝░░╚═╝╚═╝░░╚══╝░░░╚═╝░░░░╚════╝░╚═╝░░╚═╝╚═╝╚═╝░░╚══╝╚═╝");
    }

    public void print() {
        //printTitle();

        if(state != null)
            out.println(state);
        if(infoMessage != null)
            out.println(infoMessage);
        if(gameMapString != null)
            out.println(gameMapString);
        if(playersList != null)
            out.println(playersList);
        if(choiceList != null)
            out.println(choiceList);
        if(askMessage != null)
            out.println(askMessage);
    }
}
