package org.financer.shared.domain.model.value.objects;

import org.apache.commons.validator.routines.EmailValidator;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Immutable
public final class Email {

    private static final EmailValidator validator = EmailValidator.getInstance(false, true);

    @Column(name = "email", length = 128)
    private final String emailAddress;

    public Email(String emailAddress) {
        if (!validator.isValid(emailAddress)) {
            throw new IllegalArgumentException("The given email address ('" + emailAddress + "') is not valid!");
        }
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
}
