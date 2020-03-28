package org.financer.shared.domain.model.value.objects;

import org.apache.commons.validator.routines.EmailValidator;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Immutable
public final class Email implements Serializable {
    private static final long serialVersionUID = -1402839140672131281L;


    private static final EmailValidator validator = EmailValidator.getInstance(false, true);

    @Column(name = "email", length = 128, nullable = false)
    private String emailAddress;

    public Email() {
    }

    public Email(String emailAddress) {
        if (!validator.isValid(emailAddress)) {
            throw new IllegalArgumentException("The given email address ('" + emailAddress + "') is not valid!");
        }
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(emailAddress, email.emailAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emailAddress);
    }

    @Override
    public String toString() {
        return this.getEmailAddress();
    }
}

