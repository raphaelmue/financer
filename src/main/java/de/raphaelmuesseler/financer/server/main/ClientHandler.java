package de.raphaelmuesseler.financer.server.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.raphaelmuesseler.financer.server.service.FinancerService;
import de.raphaelmuesseler.financer.shared.connection.ConnectionCall;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
    private DataInputStream input;
    private DataOutputStream output;

    ClientHandler(Socket client, DataInputStream input, DataOutputStream output) {
        this.client = client;
        this.input = input;
        this.output = output;
        this.logger = Logger.getLogger("ClientHandler ("  + this.client.getRemoteSocketAddress() + ")");
    }

    @Override
    public void run() {
        this.logger.log(Level.INFO, "Client (" + client.getRemoteSocketAddress() + ") has established connection.");

        Gson gson = new GsonBuilder().create();

        try {
            ConnectionCall connectionCall = gson.fromJson(this.input.readUTF(), ConnectionCall.class);
            ConnectionResult<Object> result;

            Method method;
            try {
                method = FinancerService.class.getMethod(connectionCall.getMethodName(), Logger.class, Map.class);
                result = (ConnectionResult<Object>) method.invoke(this.service, this.logger, connectionCall.getParameters());
                this.logger.log(Level.INFO, "Request has been successfully handled.");
            } catch (NoSuchMethodException e) {
                this.logger.log(Level.SEVERE, e.getMessage(), e);
                result = new ConnectionResult<>("Method does not exists!");
            } catch (IllegalAccessException | InvocationTargetException e) {
                this.logger.log(Level.SEVERE, e.getMessage(), e);
                result = new ConnectionResult<>("Cannot access on method!");
            }

            // sending result to client
            this.output.writeUTF(gson.toJson(result));

            // close connection to client
            this.input.close();
            this.output.close();
            this.client.close();
            this.logger.log(Level.INFO, "Connection to Client (" + client.getRemoteSocketAddress() + ") has been closed.");
        } catch (IOException e) {
            this.logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
