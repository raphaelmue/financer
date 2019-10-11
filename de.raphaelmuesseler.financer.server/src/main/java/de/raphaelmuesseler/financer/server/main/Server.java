package de.raphaelmuesseler.financer.server.main;

import de.raphaelmuesseler.financer.server.db.DatabaseName;
import de.raphaelmuesseler.financer.server.db.HibernateUtil;
import de.raphaelmuesseler.financer.server.service.FinancerRestService;
import de.raphaelmuesseler.financer.server.service.FinancerService;
import de.raphaelmuesseler.financer.server.service.VerificationService;
import de.raphaelmuesseler.financer.util.concurrency.FinancerExecutor;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private static final int PORT = 3500;
    private static final int REST_PORT = 3501;

    private static final Logger logger = Logger.getLogger("Server");
    private ServerSocket serverSocket;
    private ExecutorService executor = FinancerExecutor.getExecutor();

    public static void main(String[] args) {
        int port = -1;
        int smtpPort = -1;
        String smtpEmail = null;
        String smtpPassword = null;
        String smtpHost = null;
        try {
            DatabaseName databaseName = DatabaseName.DEV;
            for (String arg : args) {
                if (arg.contains("--port=")) {
                    port = Integer.parseInt(arg.substring(7));
                    if (port < 1000 || port > 5000) {
                        logger.log(Level.SEVERE, "Please enter a port number between 1000 and 5000");
                        return;
                    }
                } else if (arg.contains("--db-host=")) {
                    HibernateUtil.setIsHostLocal(arg.substring(10).equals("local"));
                } else if (arg.contains("--database=") && (DatabaseName.getByShortCut(arg.substring(11)) != null)) {
                    databaseName = DatabaseName.getByShortCut(arg.substring(11));
                } else if (arg.contains("--smtp-email=")) {
                    smtpEmail = arg.substring(13);
                } else if (arg.contains("--smtp-password=")) {
                    smtpPassword = arg.substring(16);
                } else if (arg.contains("--smtp-host=")) {
                    smtpHost = arg.substring(12);
                } else if (arg.contains("smtp-port=")) {
                    smtpPort = Integer.parseInt(arg.substring(12));
                }
            }

            if (port == -1) {
                port = PORT;
            }

            FinancerService.setVerificationService(new VerificationService(smtpHost, smtpPort, smtpEmail, smtpPassword));
            HibernateUtil.setDatabaseName(databaseName);

            Server server = new Server(port);
            server.startHttpServer();
            server.run();
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "Please enter a real port!");
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
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
        logger.log(Level.INFO, "Java Socket Server started and is running on port {0}!", port);
    }

    /**
     * Runs the server until the server application is stopped.
     */
    public void run() {
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

    private void startHttpServer() {
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
        logger.log(Level.INFO, "Server will be stopped.");
        try {
            serverSocket.close();
        } catch (IOException ignored) {
        }
    }
}
