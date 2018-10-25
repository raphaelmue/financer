package de.raphaelmuesseler.financer.server.main;

import de.raphaelmuesseler.financer.server.db.Database;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private static final int PORT = 3500;

    private Logger logger = Logger.getLogger("Server");
    private ServerSocket serverSocket;
    private ExecutorService executor = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        int port;
        try {
            if (args.length > 0) {
                port = Integer.parseInt(args[0]);
                if (port < 1000 || port > 5000) {
                    System.out.println("Please enter a port number between 1000 and 5000");
                    return;
                }
            } else {
                port = PORT;
            }

            if (args.length > 1 && args[1] != null && args[1].equals("local")) {
                Database.setHost(true);
            } else {
                Database.setHost(false);
            }

            Server server = new Server(port);
            server.run();
        } catch (NumberFormatException e) {
            System.out.println("Please enter a real port!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Instantiates a new multi threaded socket server.
     *
     * @param port port, on which the server runs
     * @throws IOException thrown, when something went wrong creating the SocketServer
     */
    private Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.logger.log(Level.INFO, "Java Server started and is running on port " + port);
    }

    /**
     * Runs the server until the server application is stopped.
     */
    private void run() {
        while (true) {
            this.logger.log(Level.INFO, "Waiting for client ...");
            try {
                Socket client = this.serverSocket.accept();
                DataInputStream input = new DataInputStream(client.getInputStream());
                DataOutputStream output = new DataOutputStream(client.getOutputStream());
                this.executor.execute(new ClientHandler(client, input, output));
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
