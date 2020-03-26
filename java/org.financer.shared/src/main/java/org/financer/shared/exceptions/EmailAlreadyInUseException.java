package org.financer.shared.exceptions;

import org.financer.shared.domain.model.value.objects.Email;

public class EmailAlreadyInUseException extends FinancerException {
    public EmailAlreadyInUseException(Email email) {
        super("The Email address '" + email.getEmailAddress() + "' is already in use.");
        this.setKey("errEmailAlreadyInUse");
    }
}
