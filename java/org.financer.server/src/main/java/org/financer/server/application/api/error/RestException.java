package org.financer.server.application.api.error;

import org.financer.shared.exceptions.FinancerException;
import org.springframework.http.HttpStatus;

/**
 * Root exception class for all exceptions that are thrown within the REST application.
 *
 * @author Raphael Müßeler
 */
public class RestException extends FinancerException {

    private static final String MESSAGE_KEY = "exception.unexpected";

    private final HttpStatus httpStatus;

    /**
     * Instantiates a new REST Exception.
     *
     * @param httpStatus http status that is returned
     * @param messageKey message key
     * @param args       arguments for message key
     */
    public RestException(HttpStatus httpStatus, String messageKey, String... args) {
        super(null, messageKey, args);
        this.httpStatus = httpStatus;
    }

    public RestException(String message, HttpStatus httpStatus, String messageKey, String... args) {
        super(message, messageKey, args);
        this.httpStatus = httpStatus;
    }


    public RestException() {
        super(null, MESSAGE_KEY);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
