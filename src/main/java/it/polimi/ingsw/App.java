package it.polimi.ingsw;
import it.polimi.ingsw.client.cli.CliClient;
import it.polimi.ingsw.client.gui.GuiClient;
import it.polimi.ingsw.server.Server;

/**
 * Application entry point class
 */
public abstract class App {


    /**
     * Main function. Application starts here.
     *
     * If no arguments are given Application defaults to GUI mode.
     * If 2 arguments are given they have bo either "Server portNumber" (e.g. "Server 2033")
     * or "Client [CLI | GUI]" (e.g. "Client CLI" to start application in CLI mode)
     *
     * @param args command line arguments.
     *
     *
     */
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
                break;

            default:
                printUsage();
        }
    }

    public static void printUsage() {
        System.out.println("Usage:\tjava -jar Server PORT\n\tjava -jar Client [CLI | GUI]");
    }
}