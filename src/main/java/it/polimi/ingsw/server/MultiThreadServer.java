package it.polimi.ingsw.server;

import it.polimi.ingsw.server.view.ViewManager;
import it.polimi.ingsw.server.view.VirtualView;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadServer {
    private int port;
    private final int maxConnections = 3;

    public MultiThreadServer(int port) {
        this.port = port;
    }

    public void startServer(ViewManager viewManager) {
        ExecutorService executor = Executors.newFixedThreadPool(maxConnections);
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
                VirtualView view = new VirtualView(socket, viewManager);
                viewManager.add(view);

                executor.submit(view);

            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
