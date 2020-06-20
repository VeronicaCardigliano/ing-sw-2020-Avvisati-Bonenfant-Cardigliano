package it.polimi.ingsw;
import it.polimi.ingsw.client.cli.CliClient;
import it.polimi.ingsw.client.gui.GuiClient;
import it.polimi.ingsw.server.Server;


public class App {


    public static void main(String[] args) {
        if (args.length == 0)
            GuiClient.main(args);
        else {
            if (args.length == 1 && args[0].equals("Server"))
                Server.main(args);
            else if (args.length == 2 && args[0].equals("Client"))
                switch (args[1]) {
                    case "GUI":
                        GuiClient.main(args);
                        break;
                    case "CLI":
                        CliClient.launch();
                        break;
                }
            else
                System.out.println("Usage:\tjava -jar Server\n\t\tjava -jar Client [CLI | GUI]");
        }
    }
}