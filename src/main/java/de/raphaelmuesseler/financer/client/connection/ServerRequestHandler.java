package de.raphaelmuesseler.financer.client.connection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.shared.connection.AsyncConnectionCall;
import de.raphaelmuesseler.financer.shared.connection.ConnectionCall;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.util.SerialTreeItem;

import java.io.IOException;
import java.net.ConnectException;
import java.util.*;
import java.util.concurrent.Executor;

public class ServerRequestHandler implements Runnable {

    private ServerRequest serverRequest;
    private AsyncConnectionCall asyncCall;
    private boolean runLater;

    public ServerRequestHandler(String methodName, Map<String, Object> parameters, AsyncConnectionCall asyncCall) {
        this(new ServerRequest(methodName, parameters), asyncCall);
    }

    public ServerRequestHandler(String methodName, Map<String, Object> parameters, AsyncConnectionCall asyncCall, boolean runLater) {
        this(new ServerRequest(methodName, parameters), asyncCall, runLater);
    }

    private ServerRequestHandler(ServerRequest serverRequest, AsyncConnectionCall asyncCall) {
        this(serverRequest, asyncCall, false);
    }

    private ServerRequestHandler(ServerRequest serverRequest, AsyncConnectionCall asyncCall, boolean runLater) {
        this.serverRequest = serverRequest;
        this.asyncCall = asyncCall;
        this.runLater = runLater;
    }

    @Override
    public void run() {
        this.asyncCall.onBefore();
        try {
            ConnectionResult result = this.serverRequest.make();
            if (result.getException() == null) {
                this.asyncCall.onSuccess(result);
            }
        } catch (Exception e) {
            this.asyncCall.onFailure(e);
            if (e instanceof ConnectException && runLater) {
                Gson gson = new GsonBuilder().create();
                List<Object> calls;
                if (LocalStorage.readObject(LocalStorage.REQUESTS_FILE) != null) {
                    calls = (List<Object>) LocalStorage.readObject(LocalStorage.REQUESTS_FILE);
                } else {
                    calls = new ArrayList<>();
                }
                calls.add(this.serverRequest.getConnectionCall());
                LocalStorage.writeObjects(LocalStorage.REQUESTS_FILE, Collections.singleton(calls));
            }
        } finally {
            this.asyncCall.onAfter();
        }
    }

    public static void makeRequests(Executor executor) throws IOException {
        if (LocalStorage.readObject(LocalStorage.REQUESTS_FILE) != null) {
            List<Object> calls = (List<Object>) LocalStorage.readObject(LocalStorage.REQUESTS_FILE);
            if (calls != null && calls.size() > 0 && ServerRequest.testConnection()) {
                for (Object object : calls) {
                    ConnectionCall call = (ConnectionCall) object;
                    executor.execute(new ServerRequestHandler(new ServerRequest(call), result -> {
                    }));
                }
                LocalStorage.writeObject(LocalStorage.REQUESTS_FILE, null);
            }
        }
    }
}
