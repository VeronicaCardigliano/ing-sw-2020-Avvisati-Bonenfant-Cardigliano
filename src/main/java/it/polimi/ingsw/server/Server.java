package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.view.ViewManager;

/**
 * Server entry point.
 * Contains main function that initializes Model, ViewManager,
 * Controller and a listening Multithread server.
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

        server.startServer(controller);

    }
}
