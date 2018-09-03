package de.raphaelmuesseler.financer.server.main;

import de.raphaelmuesseler.financer.server.service.FinancerService;
import de.raphaelmuesseler.financer.shared.connection.ConnectionCall;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {

    private Logger logger;
    private FinancerService service = FinancerService.getInstance();
    private final Socket client;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    ClientHandler(Socket client, DataInputStream input, DataOutputStream output) throws IOException {
        this.client = client;
        this.inputStream = new ObjectInputStream(input);
        this.outputStream = new ObjectOutputStream(output);
        this.logger = Logger.getLogger("ClientHandler ("  + this.client.getRemoteSocketAddress() + ")");
    }

    @Override
    public void run() {
        this.logger.log(Level.INFO, "Client (" + client.getRemoteSocketAddress() + ") has established connection.");

        try {
            ConnectionCall connectionCall = (ConnectionCall) this.inputStream.readObject();
            ConnectionResult<Object> result;

            Method method;
            try {
                method = FinancerService.class.getMethod(connectionCall.getMethodName(), Logger.class, Map.class);
                result = (ConnectionResult<Object>) method.invoke(this.service, this.logger, connectionCall.getParameters());
                this.logger.log(Level.INFO, "Request has been successfully handled.");
            } catch (Exception exception) {
                this.logger.log(Level.SEVERE, exception.getMessage(), exception);
                result = new ConnectionResult<>(null, exception);
            }

            // sending result to client
            this.outputStream.writeObject(result);

            // close connection to client
            this.inputStream.close();
            this.outputStream.close();
            this.client.close();
            this.logger.log(Level.INFO, "Connection to Client (" + client.getRemoteSocketAddress() + ") has been closed.");
        } catch (IOException e) {
            this.logger.log(Level.SEVERE, e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
