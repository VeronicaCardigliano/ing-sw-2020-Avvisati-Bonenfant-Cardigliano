package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.view.ViewManager;

/**
 * Hello world!
 *
 */
public class Server
{
    public static void main(int port)
    {
        Model model = new Model();
        ViewManager vm = new ViewManager();
        Controller controller = new Controller(model, vm);

        MultiThreadServer server = new MultiThreadServer(port);

        server.startServer(vm, controller);

    }
}
