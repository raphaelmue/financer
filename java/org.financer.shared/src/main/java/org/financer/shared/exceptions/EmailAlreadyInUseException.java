package org.financer.shared.exceptions;

import org.financer.shared.model.user.User;

public class EmailAlreadyInUseException extends FinancerException {
    public EmailAlreadyInUseException(User user) {
        super("The Email address '" + user.getEmail() + "' is already in use.");
        this.setKey("errEmailAlreadyInUse");
    }
}
