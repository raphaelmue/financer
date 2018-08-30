package de.raphaelmuesseler.financer.client.connection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.raphaelmuesseler.financer.shared.connection.ConnectionCall;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class ServerRequest {

    private final static String HOST = "localhost";
    private final static int PORT = 3500;

    private final ConnectionCall connectionCall;

    ServerRequest(String methodName, Map<String, Object> parameters) {
        this.connectionCall = new ConnectionCall(methodName, parameters);
    }

    ServerRequest(ConnectionCall connectionCall) {
        this.connectionCall = connectionCall;
    }

    ConnectionResult make() throws IOException {
        Gson gson = new GsonBuilder().create();
        return this.make(gson.toJson(this.connectionCall));
    }

    private ConnectionResult make(String gsonString) throws IOException {
        Gson gson = new GsonBuilder().create();

        Socket socket = new Socket(HOST, PORT);
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        output.writeUTF(gsonString);

        DataInputStream input = new DataInputStream(socket.getInputStream());

        return gson.fromJson(input.readUTF(), ConnectionResult.class);
    }
}
