package org.financer.server.main;

import org.financer.server.db.HibernateUtil;
import org.financer.server.service.FinancerService;
import org.financer.shared.connection.ConnectionCall;
import org.financer.shared.connection.ConnectionResult;
import org.financer.shared.exceptions.NotAuthorizedException;
import org.financer.shared.model.user.User;
import org.hibernate.Session;

import java.io.*;
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
        this.logger = Logger.getLogger("ClientHandler (" + this.client.getRemoteSocketAddress() + ")");
    }

    @Override
    public void run() {
        this.logger.log(Level.INFO, "Client (" + client.getRemoteSocketAddress() + ") has established connection.");

        try {
            ConnectionCall connectionCall = (ConnectionCall) this.inputStream.readObject();
            ConnectionResult<Serializable> result = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                if (!connectionCall.getMethodName().equals("checkCredentials") && !connectionCall.getMethodName().equals("registerUser")) {
                    User user = this.service.checkUsersToken(this.logger, session, connectionCall.getParameters());
                    if (user == null) {
                        throw new NotAuthorizedException("Token '" + connectionCall.getParameters().get("token") + "' is invalid.");
                    }
                }

                Method method;
                try {
                    method = FinancerService.class.getMethod(connectionCall.getMethodName(), Logger.class, Session.class, Map.class);
                    connectionCall.getParameters().put("ipAddress", client.getInetAddress().toString());
                    //noinspection unchecked
                    result = (ConnectionResult<Serializable>) method.invoke(this.service, this.logger, session, connectionCall.getParameters());
                    this.logger.log(Level.INFO, "Request has been successfully handled.");
                } catch (Exception exception) {
                    this.logger.log(Level.SEVERE, exception.getMessage(), exception);
                    result = new ConnectionResult<>(null, exception);
                }
            } catch (NotAuthorizedException exception) {
                result = new ConnectionResult<>(null, exception);
            } finally {
                // sending result to client
                this.outputStream.writeObject(result);

                // close connection to client
                this.inputStream.close();
                this.outputStream.close();
                this.client.close();
                this.logger.log(Level.INFO, "Connection to Client (" + client.getRemoteSocketAddress() + ") has been closed.");
            }
        } catch (IOException | ClassNotFoundException e) {
            this.logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
