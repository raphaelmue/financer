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

    public ConnectionResult make(String methodName, Map<String, Object> parameters) throws IOException {
        Gson gson = new GsonBuilder().create();
        ConnectionCall call = new ConnectionCall(methodName, parameters);
        return this.make(gson.toJson(call));
    }

    public ConnectionResult make(String gsonString) throws IOException {
        Gson gson = new GsonBuilder().create();

        Socket socket = new Socket(HOST, PORT);
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        output.writeUTF(gsonString);

        DataInputStream input = new DataInputStream(socket.getInputStream());

        return gson.fromJson(input.readUTF(), ConnectionResult.class);
    }
}
