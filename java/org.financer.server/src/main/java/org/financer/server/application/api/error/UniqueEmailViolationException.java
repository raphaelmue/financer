package org.financer.server.application.api.error;

import org.financer.shared.domain.model.value.objects.Email;
import org.springframework.http.HttpStatus;

public class UniqueEmailViolationException extends RestException {

    private static final String MESSAGE = "Unique constraint violation: Email %s is already assigned to another user.";
    private static final String MESSAGE_KEY = "exception.uniqueEmail";

    public UniqueEmailViolationException(Email email) {
        super(String.format(MESSAGE, email.getEmailAddress()), HttpStatus.BAD_REQUEST, MESSAGE_KEY, email.getEmailAddress());
    }

}
