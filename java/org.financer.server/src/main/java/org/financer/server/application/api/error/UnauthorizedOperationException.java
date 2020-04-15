package org.financer.server.application.api.error;

import org.springframework.http.HttpStatus;

/**
 * Thrown when an user tries to perform an operation for which he/she has no rights, e.g. if the user tries to access an
 * entity, that he does not own.
 */
public class UnauthorizedOperationException extends RestException {

    private static final String MESSAGE = "User with id %d is not allowed to perform this action";
    private static final String MESSAGE_KEY = "exception.unauthorizedOperation";

    /**
     * Instantiates the unauthorized operation exception
     *
     * @param userId id of the user that tried to perform this action
     */
    public UnauthorizedOperationException(Long userId) {
        super(MESSAGE, HttpStatus.FORBIDDEN, MESSAGE_KEY, userId);
    }

}
