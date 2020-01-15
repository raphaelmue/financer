package de.raphaelmuesseler.financer.shared.exceptions;

import de.raphaelmuesseler.financer.shared.model.user.User;

public class EmailAlreadyInUseException extends FinancerException {
    public EmailAlreadyInUseException(User user) {
        super("The Email address '" + user.getEmail() + "' is already in use.");
        this.setKey("errEmailAlreadyInUse");
    }
}
