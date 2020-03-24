package org.financer.shared.exceptions;

public class EmailAlreadyInUseException extends FinancerException {
    public EmailAlreadyInUseException(User user) {
        super("The Email address '" + user.getEmail() + "' is already in use.");
        this.setKey("errEmailAlreadyInUse");
    }
}
