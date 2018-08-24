package de.raphaelmuesseler.financer.server.main;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private static final int PORT = 3500;

    private Logger logger = Logger.getLogger("Server");
    private ServerSocket serverSocket;

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
            Server server = new Server(port);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a real port!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.logger.log(Level.INFO, "Java Server started and is running on port " + port);

        while (true) {
            this.handleConnection(this.serverSocket.accept());
        }
    }

    private void handleConnection(Socket client) throws IOException {
        Scanner in  = new Scanner(client.getInputStream());
        PrintWriter out = new PrintWriter(client.getOutputStream(), true);


    }
}
