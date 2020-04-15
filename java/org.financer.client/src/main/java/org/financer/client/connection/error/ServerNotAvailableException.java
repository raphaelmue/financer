package org.financer.client.connection.error;

import org.financer.client.connection.RequestConfig;
import org.financer.shared.exceptions.FinancerException;

public class ServerNotAvailableException extends FinancerException {

    private static final String MESSAGE = "Request [%s: %s] failed. Server is not probably available.";
    private static final String MESSAGE_KEY = "exception.serverNotAvailable";

    public ServerNotAvailableException(RequestConfig requestConfig) {
        super(MESSAGE, MESSAGE_KEY, requestConfig.getHttpMethod().toString(), requestConfig.getPath());
    }
}
