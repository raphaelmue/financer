package de.raphaelmuesseler.financer.server.main;

import de.raphaelmuesseler.financer.server.db.Database;
import de.raphaelmuesseler.financer.server.service.FinancerRestService;
import de.raphaelmuesseler.financer.util.concurrency.FinancerExecutor;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private static final int PORT = 3500;
    private static final int REST_PORT = 3501;

    private Logger logger = Logger.getLogger("Server");
    private ServerSocket serverSocket;
    private ExecutorService executor = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        int port = -1;
        Database.DatabaseName databaseName = null;
        Database.setHost(false);
        try {
            for (String arg : args) {
                if (arg.contains("--port=")) {
                    port = Integer.parseInt(arg.substring(7));
                    if (port < 1000 || port > 5000) {
                        System.out.println("Please enter a port number between 1000 and 5000");
                        return;
                    }
                } else if (arg.contains("--db-host=")) {
                    Database.setHost(arg.substring(10).equals("local"));
                } else if (arg.contains("--database=")) {
                    if (Database.DatabaseName.getByShortCut(arg.substring(11)) != null) {
                        databaseName = Database.DatabaseName.getByShortCut(arg.substring(11));
                    }
                }
            }

            if (port == -1) {
                port = PORT;
            }

            Database.setDbName(Objects.requireNonNullElse(databaseName, Database.DatabaseName.DEV));

            Server server = new Server(port);
            server.startHttpServer();
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
    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.logger.log(Level.INFO, "Java Socket Server started and is running on port " + port);
    }

    /**
     * Runs the server until the server application is stopped.
     */
    public void run() {
        while (true) {
            this.logger.log(Level.INFO, "Waiting for client ...");
            try {
                Socket client = this.serverSocket.accept();
                DataInputStream input = new DataInputStream(client.getInputStream());
                DataOutputStream output = new DataOutputStream(client.getOutputStream());
                FinancerExecutor.getExecutor().execute(new ClientHandler(client, input, output));
            } catch (Exception e) {
                this.logger.log(Level.SEVERE, e.getMessage());
                break;
            }
        }
    }

    public void startHttpServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in packages
        final ResourceConfig rc = new ResourceConfig().register(new FinancerRestService(executor));

        // create and start a new instance of grizzly http server
        GrizzlyHttpServerFactory.createHttpServer(URI.create("http://localhost:" + REST_PORT + "/"), rc);
    }

    /**
     * Stops the server after the next client that will be handled.
     */
    public void stop() {
        this.logger.log(Level.INFO, "Server will be stopped.");
        try {
            serverSocket.close();
        } catch (IOException ignored) {
        }
    }
}
