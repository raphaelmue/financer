package org.financer.shared.exceptions;

/**
 * Exception, that handles cases, if the client tries to access to the server, but has no rights for it.
 *
 * @author Raphael Müßeler
 */
public class UnauthorizedException extends FinancerException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
