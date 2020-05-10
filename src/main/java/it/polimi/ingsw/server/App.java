package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.view.ViewManager;
import it.polimi.ingsw.server.view.VirtualView;

import javax.swing.text.View;

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
