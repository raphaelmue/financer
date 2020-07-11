package org.financer.server.application.api.error;

import org.financer.shared.domain.model.value.objects.TokenString;
import org.springframework.http.HttpStatus;

/**
 * Exception, that handles cases, if the client tries to access to the server, but has no rights for it.
 *
 * @author Raphael Müßeler
 */
public class UnauthorizedTokenException extends RestException {

    private static final String MESSAGE = "Token ('%s') is invalid.";
    private static final String MESSAGE_KEY = "exception.unauthorizedToken";

    /**
     * Instantiates an unauthorized exception.
     *
     * @param tokenString token string that is invalid
     */
    public UnauthorizedTokenException(TokenString tokenString) {
        super(String.format(MESSAGE, tokenString.getToken()), HttpStatus.UNAUTHORIZED, MESSAGE_KEY, tokenString.getToken());
    }
}
