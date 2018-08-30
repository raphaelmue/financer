package de.raphaelmuesseler.financer.client.connection;

import de.raphaelmuesseler.financer.shared.connection.AsyncConnectionCall;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;

import java.util.Map;

public class ServerRequestHandler implements Runnable {

    private ServerRequest serverRequest;
    private AsyncConnectionCall asyncCall;

    public ServerRequestHandler(String methodName, Map<String, Object> parameters, AsyncConnectionCall asyncCall) {
        this(new ServerRequest(methodName, parameters), asyncCall);
    }

    private ServerRequestHandler(ServerRequest serverRequest, AsyncConnectionCall asyncCall) {
        this.serverRequest = serverRequest;
        this.asyncCall = asyncCall;
    }

    @Override
    public void run() {
        try {
            ConnectionResult result = this.serverRequest.make();
            if (result.getErrorMessage() == null && result.getResult() != null) {
                this.asyncCall.onSuccess(result);
            } else {
                this.asyncCall.onFailure(new Throwable(result.getErrorMessage()));
            }
        } catch (Exception e) {
            this.asyncCall.onFailure(e);
        }
    }
}
