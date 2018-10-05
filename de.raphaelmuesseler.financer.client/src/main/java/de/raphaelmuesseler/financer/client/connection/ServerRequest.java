package de.raphaelmuesseler.financer.client.connection;

import de.raphaelmuesseler.financer.shared.connection.ConnectionCall;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class ServerRequest {

    private final static String HOST_LOCAL = "localhost";
    private final static String HOST_DEPLOY = "raphael-muesseler.de";
    private static String HOST;
    private final static int PORT = 3500;

    private final ConnectionCall connectionCall;

    ServerRequest(String methodName, Map<String, Object> parameters) {
        this.connectionCall = new ConnectionCall(methodName, parameters);
    }

    ServerRequest(ConnectionCall connectionCall) {
        this.connectionCall = connectionCall;
    }

    public static void setHost(boolean local) {
        ServerRequest.HOST = local ? ServerRequest.HOST_LOCAL : ServerRequest.HOST_DEPLOY;
    }

    ConnectionResult make() throws IOException, ClassNotFoundException {
        Socket socket = new Socket(HOST, PORT);

        ObjectOutputStream output = new ObjectOutputStream(new DataOutputStream(socket.getOutputStream()));
        output.writeObject(this.connectionCall);

        ObjectInputStream input = new ObjectInputStream(new DataInputStream(socket.getInputStream()));

        return (ConnectionResult) input.readObject();
    }
}
