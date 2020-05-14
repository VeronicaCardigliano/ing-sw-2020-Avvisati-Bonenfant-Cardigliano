package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.view.ViewManager;
import it.polimi.ingsw.server.view.VirtualView;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadServer {
    private final int port;

    public MultiThreadServer(int port) {
        this.port = port;
    }

    public void startServer(ViewManager viewManager, Controller controller) {
        ExecutorService executor = Executors.newFixedThreadPool(5);
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
            }
        }
    }
}
