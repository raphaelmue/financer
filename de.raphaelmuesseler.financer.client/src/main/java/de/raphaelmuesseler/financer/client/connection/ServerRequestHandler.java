package de.raphaelmuesseler.financer.client.connection;

import de.raphaelmuesseler.financer.client.local.Application;
import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.shared.connection.ConnectionCall;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.user.User;

import java.io.IOException;
import java.io.Serializable;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ServerRequestHandler implements Runnable {

    private ServerRequest serverRequest;
    private AsyncConnectionCall asyncCall;
    private boolean runLater;
    private static LocalStorage localStorage;
    private static Application application;

    public ServerRequestHandler(User user, String methodName, Map<String, Serializable> parameters, AsyncConnectionCall asyncCall) {
        this(new ServerRequest(user, methodName, parameters), asyncCall, false);
    }

    public ServerRequestHandler(String methodName, Map<String, Serializable> parameters, AsyncConnectionCall asyncCall) {
        this(new ServerRequest(methodName, parameters), asyncCall, false);
    }

    public ServerRequestHandler(User user, String methodName, Map<String, Serializable> parameters, AsyncConnectionCall asyncCall, boolean runLater) {
        this(new ServerRequest(user, methodName, parameters), asyncCall, runLater);
    }

    private ServerRequestHandler(ServerRequest serverRequest, AsyncConnectionCall asyncCall, boolean runLater) {
        this.serverRequest = serverRequest;
        this.asyncCall = asyncCall;
        this.runLater = runLater;
    }

    public static void setLocalStorage(LocalStorage localStorage) {
        ServerRequestHandler.localStorage = localStorage;
    }

    public static void setApplication(Application application) {
        ServerRequestHandler.application = application;
    }

    @Override
    public void run() {
        this.asyncCall.onBefore();
        try {
            application.showLoadingBox();
            ConnectionResult result = this.serverRequest.make();
            if (result.getException() == null) {
                this.asyncCall.onSuccess(result);
                application.setOnline();
                makeRequests(Executors.newCachedThreadPool());
            } else {
                this.asyncCall.onFailure(result.getException());
            }
        } catch (Exception e) {
            this.asyncCall.onFailure(e);
            application.setOffline();
            if (e instanceof ConnectException && runLater) {
                List<Object> calls;
                if (localStorage.readObject("requests") != null) {
                    calls = localStorage.readList("requests");
                } else {
                    calls = new ArrayList<>();
                }
                calls.add(this.serverRequest.getConnectionCall());
                localStorage.writeObject("requests", (Serializable) calls);
            }
        } finally {
            this.asyncCall.onAfter();
            application.hideLoadingBox();
        }
    }

    public static void makeRequests(Executor executor) throws IOException {
        if (localStorage.readObject("requests") != null) {
            List<Object> calls = localStorage.readList("requests");
            if (calls != null && !calls.isEmpty() && ServerRequest.testConnection()) {
                for (Object object : calls) {
                    ConnectionCall call = (ConnectionCall) object;
                    executor.execute(new ServerRequestHandler(new ServerRequest(call), result -> {}, true));
                }
                localStorage.writeObject("requests", null);
            }
        }
    }
}
