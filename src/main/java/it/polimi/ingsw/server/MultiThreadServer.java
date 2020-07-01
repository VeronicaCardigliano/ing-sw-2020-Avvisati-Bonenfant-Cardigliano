package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.view.VirtualView;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Multithread Server listening for client connections.
 * With every new connection it create a Virtual View object that handles the new socket io.
 */
public class MultiThreadServer {
    private final int port;

    public MultiThreadServer(int port) {
        this.port = port;
    }

    /**
     * creates a ServerSocket listening for connections. For every new connection it creates a new Virtual View.
     * The controller is set to be the virtual view observer and then it runs virtual view on a thread.
     */
    public void startServer(Controller controller) {
        int maxNumberOfThreads = 5;
        ExecutorService executor = Executors.newFixedThreadPool(maxNumberOfThreads);
        ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket(port);

        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }

        System.out.println("Server Ready");
        while (true) {
            try {

                Socket socket = serverSocket.accept();

                System.out.println("Connected to " + socket.getRemoteSocketAddress());

                VirtualView view = new VirtualView(socket, controller);
                view.setConnectionObserver(controller);

                executor.submit(view);



            } catch (IOException e) {
                System.err.println(e.getMessage());
                break;
            }
        }
    }
}
