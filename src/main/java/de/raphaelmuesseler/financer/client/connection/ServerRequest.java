package de.raphaelmuesseler.financer.client.connection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.raphaelmuesseler.financer.shared.connection.ConnectionCall;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;

import java.io.*;
import java.net.Socket;
import java.util.Map;

class ServerRequest {

    private final static String HOST = "localhost";
    private final static int PORT = 3500;

    private final ConnectionCall connectionCall;

    ServerRequest(String methodName, Map<String, Object> parameters) {
        this.connectionCall = new ConnectionCall(methodName, parameters);
    }

    ServerRequest(ConnectionCall connectionCall) {
        this.connectionCall = connectionCall;
    }

    ConnectionResult make() throws IOException, ClassNotFoundException {
        Socket socket = new Socket(HOST, PORT);

        ObjectOutputStream output = new ObjectOutputStream(new DataOutputStream(socket.getOutputStream()));
        output.writeObject(this.connectionCall);

        ObjectInputStream input = new ObjectInputStream(new DataInputStream(socket.getInputStream()));

        return (ConnectionResult) input.readObject();
    }
}
