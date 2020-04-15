package org.financer.client.connection.error;

import org.financer.client.connection.RestErrorMessage;
import org.financer.shared.exceptions.FinancerException;

public class RestException extends FinancerException {

    private final RestErrorMessage restErrorMessage;

    public RestException(RestErrorMessage message) {
        super(String.join(" ", message.getMessages()), null);
        this.restErrorMessage = message;
    }

    public RestErrorMessage getRestErrorMessage() {
        return restErrorMessage;
    }
}
