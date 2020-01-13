package de.raphaelmuesseler.financer.server.main;

import de.raphaelmuesseler.financer.server.db.HibernateUtil;
import de.raphaelmuesseler.financer.util.concurrency.FinancerExecutor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private static final Logger logger = Logger.getLogger("Server");
    private ServerSocket serverSocket;
    private boolean isRunning = false;

    private static Properties serverProperties;

    public static void setServerProperties(Properties serverProperties) {
        Server.serverProperties = serverProperties;
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.run();
    }

    /**
     * Instantiates a new multi threaded socket server.
     *
     * @throws IOException thrown, when something went wrong creating the SocketServer
     */
    public Server() throws IOException {
        Properties defaultProperties = new Properties();
        defaultProperties.load(getClass().getResourceAsStream("default.properties"));

        if (serverProperties == null) {
            serverProperties = new Properties(defaultProperties);
            try {
                serverProperties.load(new FileInputStream("conf/financer.properties"));
            } catch (IOException e) {
                logger.log(Level.INFO, "No custom properties specified!");
            }
        }

        HibernateUtil.setDatabaseProperties(serverProperties);

        int port = Integer.parseInt(serverProperties.getProperty("financer.server.port"));
        this.serverSocket = new ServerSocket(port);
        logger.log(Level.INFO, "Java Socket Server started and is running on port {0}!", port);
    }

    /**
     * Runs the server until the server application is stopped.
     */
    public void run() {
        this.isRunning = true;
        while (true) {
            logger.log(Level.INFO, "Waiting for client ...");
            try {
                Socket client = this.serverSocket.accept();
                DataInputStream input = new DataInputStream(client.getInputStream());
                DataOutputStream output = new DataOutputStream(client.getOutputStream());
                FinancerExecutor.getExecutor().execute(new ClientHandler(client, input, output));
            } catch (SocketException e) {
                if (e.getMessage().equals("Interrupted function call: accept failed")) {
                    logger.log(Level.SEVERE, "Server has stopped.");
                    break;
                }
                logger.log(Level.SEVERE, e.getMessage(), e);
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Stops the server after the next client that will be handled.
     */
    public void stop() {
        logger.log(Level.INFO, "Server will be stopped.");
        try {
            serverSocket.close();
        } catch (IOException ignored) {
        }
    }
}
