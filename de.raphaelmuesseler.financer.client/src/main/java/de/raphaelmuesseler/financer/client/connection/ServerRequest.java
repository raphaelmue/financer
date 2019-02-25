package de.raphaelmuesseler.financer.client.connection;

import de.raphaelmuesseler.financer.shared.connection.ConnectionCall;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.user.User;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class ServerRequest {

    private final static String HOST_LOCAL = "localhost";
    private final static String HOST_DEPLOY = "raphael-muesseler.de";
    private static String HOST;
    private static int PORT = 3500;
    private final static int TIMEOUT = 5000;

    private final ConnectionCall connectionCall;

    ServerRequest(ConnectionCall connectionCall) {
        this.connectionCall = connectionCall;
    }

    ServerRequest(String methodName, Map<String, Object> parameters) {
        parameters.put("system", System.getProperty("os.name"));
        this.connectionCall = new ConnectionCall(methodName, parameters);
    }

    ServerRequest(User user, String methodName, Map<String, Object> parameters) {
        parameters.put("token", user.getToken());
        parameters.put("system", System.getProperty("os.name"));

        this.connectionCall = new ConnectionCall(methodName, parameters);
    }

    public static void setHost(boolean local) {
        ServerRequest.HOST = local ? ServerRequest.HOST_LOCAL : ServerRequest.HOST_DEPLOY;
    }

    public static void setPort(int port) {
        ServerRequest.PORT = port;
    }

    ConnectionResult make() throws IOException, ClassNotFoundException {
        Socket socket = new Socket(HOST, PORT);

        ObjectOutputStream output = new ObjectOutputStream(new DataOutputStream(socket.getOutputStream()));
        output.writeObject(this.connectionCall);

        ObjectInputStream input = new ObjectInputStream(new DataInputStream(socket.getInputStream()));

        return (ConnectionResult) input.readObject();
    }

    static boolean testConnection() throws IOException {
        Socket socket = new Socket(HOST, PORT);
        return socket.getInetAddress().isReachable(TIMEOUT);
    }

    ConnectionCall getConnectionCall() {
        return connectionCall;
    }
}
