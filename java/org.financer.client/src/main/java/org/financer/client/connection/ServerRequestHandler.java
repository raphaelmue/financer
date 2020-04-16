package org.financer.client.connection;

import java.util.concurrent.ExecutorService;

public class ServerRequestHandler {

    private final ExecutorService executor;
    private final ServerRequest<?> serverRequest;

    public ServerRequestHandler(ExecutorService executor, ServerRequest<?> serverRequest) {
        this.executor = executor;
        this.serverRequest = serverRequest;
    }

    public void execute() {
        this.executor.execute(serverRequest);
    }
}
