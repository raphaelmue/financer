package de.raphaelmuesseler.financer.shared.exceptions;

/**
 * Exception, that handles cases, if the client tries to access to the server, but has no rights for it.
 *
 * @author Raphael Müßeler
 */
public class NotAuthorizedException extends FinancerException {
    public NotAuthorizedException(String message) {
        super(message);
    }
}
