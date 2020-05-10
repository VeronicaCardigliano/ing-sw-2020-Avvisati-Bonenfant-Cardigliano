package it.polimi.ingsw.server;

import it.polimi.ingsw.server.view.ViewManager;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        ViewManager vm = new ViewManager();
        MultiThreadServer server = new MultiThreadServer(2033);

        server.startServer(vm);

    }
}
