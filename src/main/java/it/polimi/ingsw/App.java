package it.polimi.ingsw;
import it.polimi.ingsw.client.cli.CliClient;
import it.polimi.ingsw.client.gui.GuiClient;
import it.polimi.ingsw.server.Server;


public class App {


    public static void main(String[] args) {
        switch (args.length) {

            case 0:
                GuiClient.main(args);
                break;
            case 2:
                switch (args[0]) {
                    case "Server":
                        try {
                            Server.main(Integer.parseInt(args[1]));
                        } catch (NumberFormatException e) {
                            printUsage();
                        }
                        break;
                    case "Client":
                        if(args[1].equals("GUI"))
                            GuiClient.main(args);
                        else if(args[1].equals("CLI"))
                            CliClient.launch();
                        else
                            printUsage();
                        break;
                    default:
                        printUsage();
                }
        }
    }

    public static void printUsage() {
        System.out.println("Usage:\tjava -jar Server PORT\n\t\tjava -jar Client [CLI | GUI]");
    }
}