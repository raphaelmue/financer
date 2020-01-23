package org.financer.client.connection;

import org.financer.shared.connection.ConnectionCall;
import org.financer.shared.connection.ConnectionResult;
import org.financer.shared.model.user.User;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Map;

public class ServerRequest {

    private static final String HOST_LOCAL = "localhost";
    private static final String HOST_REMOTE = "raphael-muesseler.de";
    private static String host;
    private static int post = 3500;
    private static final int TIMEOUT = 5000;

    private final ConnectionCall connectionCall;

    ServerRequest(ConnectionCall connectionCall) {
        this.connectionCall = connectionCall;
    }

    ServerRequest(String methodName, Map<String, Serializable> parameters) {
        parameters.put("system", System.getProperty("os.name"));
        this.connectionCall = new ConnectionCall(methodName, parameters);
    }

    ServerRequest(User user, String methodName, Map<String, Serializable> parameters) {
        parameters.put("token", user.getActiveToken().getToken());
        parameters.put("system", System.getProperty("os.name"));

        this.connectionCall = new ConnectionCall(methodName, parameters);
    }

    public static void setHost(boolean local) {
        ServerRequest.host = local ? ServerRequest.HOST_LOCAL : ServerRequest.HOST_REMOTE;
    }

    public static void setHost(String host) {
        ServerRequest.host = host;
    }

    public static void setPort(int port) {
        ServerRequest.post = port;
    }

    ConnectionResult make() throws IOException, ClassNotFoundException {
        Socket socket = new Socket(host, post);

        ObjectOutputStream output = new ObjectOutputStream(new DataOutputStream(socket.getOutputStream()));
        output.writeObject(this.connectionCall);

        ObjectInputStream input = new ObjectInputStream(new DataInputStream(socket.getInputStream()));

        return (ConnectionResult) input.readObject();
    }

    static boolean testConnection() throws IOException {
        try (Socket ignored = new Socket(host, post)) {
            return true;
        } catch (ConnectException e) {
            return false;
        }
    }

    ConnectionCall getConnectionCall() {
        return connectionCall;
    }
}
