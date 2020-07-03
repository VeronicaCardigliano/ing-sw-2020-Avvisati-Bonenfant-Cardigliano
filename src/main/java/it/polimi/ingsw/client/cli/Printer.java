package it.polimi.ingsw.client.cli;

import java.io.PrintStream;

/**
 * Can sets 6 different messages to print in CLI.
 * It will print in order state, infoMessage, gameMapString, playersList, choiceList, askMessage.
 */
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


    public synchronized void setState(String state) {
        this.state = state;
    }

    public synchronized void setInfoMessage(String infoMessage) {
        this.infoMessage = infoMessage;
    }

    public synchronized void setGameMapString(String gameMapString) {
        this.gameMapString = gameMapString;
    }

    public synchronized void setAskMessage(String message) {
        this.askMessage = message;
    }

    public synchronized void setPlayersList(String playersList) {
        this.playersList = playersList;
    }

    public synchronized void setChoiceList(String choiceList) {
        this.choiceList = choiceList;
    }

    /**
     * Prints Home Title
     */
    public void printTitle() {
        out.println("\n" +
                "░██████╗░█████╗░███╗░░██╗████████╗░█████╗░██████╗░██╗███╗░░██╗██╗\n" +
                "██╔════╝██╔══██╗████╗░██║╚══██╔══╝██╔══██╗██╔══██╗██║████╗░██║██║\n" +
                "╚█████╗░███████║██╔██╗██║░░░██║░░░██║░░██║██████╔╝██║██╔██╗██║██║\n" +
                "░╚═══██╗██╔══██║██║╚████║░░░██║░░░██║░░██║██╔══██╗██║██║╚████║██║\n" +
                "██████╔╝██║░░██║██║░╚███║░░░██║░░░╚█████╔╝██║░░██║██║██║░╚███║██║\n" +
                "╚═════╝░╚═╝░░╚═╝╚═╝░░╚══╝░░░╚═╝░░░░╚════╝░╚═╝░░╚═╝╚═╝╚═╝░░╚══╝╚═╝");
    }

    /**
     * prints all strings.
     */
    public synchronized void print() {

        if(state != null)
            out.println("\n" + state);
        if(gameMapString != null)
            out.println(gameMapString);
        if(playersList != null)
            out.println(playersList);
        if(infoMessage != null)
            out.println(infoMessage);
        if(choiceList != null)
            out.print(choiceList);
        if(askMessage != null)
            out.print(askMessage);
    }

    /**
     * erase all strings
     */
    public synchronized void erase() {
        state = null;
        infoMessage = null;
        gameMapString = null;
        playersList = null;
        choiceList = null;
        askMessage = null;
    }
}
