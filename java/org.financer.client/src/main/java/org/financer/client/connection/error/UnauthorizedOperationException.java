package org.financer.client.connection.error;

import org.financer.shared.exceptions.FinancerException;

public class UnauthorizedOperationException extends FinancerException {

    private static final String MESSAGE = "User is not allowed to perform this action";
    private static final String MESSAGE_KEY = "exception.unauthorizedOperation";

    /**
     * Instantiates the unauthorized operation exception
     */
    public UnauthorizedOperationException() {
        super(MESSAGE, MESSAGE_KEY);
    }

}
