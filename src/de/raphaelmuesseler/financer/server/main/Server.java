package de.raphaelmuesseler.financer.server.main;

import de.raphaelmuesseler.financer.server.service.FinancerService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private static final int PORT = 3500;

    private Logger logger = Logger.getLogger("Server");
    private FinancerService service = FinancerService.getInstance();
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
            server.run();
        } catch (NumberFormatException e) {
            System.out.println("Please enter a real port!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.logger.log(Level.INFO, "Java Server started and is running on port " + port);
    }

    private void run() {
        while (true) {
            this.logger.log(Level.INFO, "Waiting for client ...");
            try (Socket client = this.serverSocket.accept()) {
                this.handleConnection(client);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void handleConnection(Socket client) throws IOException {
        this.logger.log(Level.INFO, "Client (" + client.getRemoteSocketAddress() + ") has established connection.");

        DataInputStream input = new DataInputStream(client.getInputStream());
        System.out.println(input.readUTF());
        DataOutputStream output = new DataOutputStream(client.getOutputStream());
        output.writeUTF("Result");

        client.close();
    }
}
