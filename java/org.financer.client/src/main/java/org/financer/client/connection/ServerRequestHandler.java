package org.financer.client.connection;

import org.financer.client.local.Application;
import org.financer.client.local.LocalStorage;
import org.financer.shared.connection.ConnectionCall;
import org.financer.shared.connection.ConnectionResult;
import org.financer.shared.model.user.User;

import java.io.IOException;
import java.io.Serializable;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerRequestHandler implements Runnable {

    private final Logger logger = Logger.getLogger("FinancerApplication");
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
            if (application != null) {
                application.showLoadingBox();
            }
            ConnectionResult result = this.serverRequest.make();
            if (result.getException() == null) {
                this.asyncCall.onSuccess(result);
                if (application != null) {
                    application.setOnline();
                }
                makeRequests(Executors.newCachedThreadPool());
            } else {
                this.asyncCall.onFailure(result.getException());
                logger.log(Level.SEVERE, result.getException().getMessage(), result.getException());
                if (application != null) {
                    application.showErrorDialog(result.getException());
                }
            }
        } catch (Exception e) {
            this.asyncCall.onFailure(e);
            if (application != null) {
                application.setOffline();
                application.showErrorDialog(e);
            }
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
            if (application != null) {
                application.hideLoadingBox();
            }
        }
    }

    public static void makeRequests(Executor executor) throws IOException {
        if (localStorage.readObject("requests") != null) {
            List<Object> calls = localStorage.readList("requests");
            if (calls != null && !calls.isEmpty() && ServerRequest.testConnection()) {
                for (Object object : calls) {
                    ConnectionCall call = (ConnectionCall) object;
                    executor.execute(new ServerRequestHandler(new ServerRequest(call), result -> {
                    }, true));
                }
                localStorage.writeObject("requests", null);
            }
        }
    }
}
