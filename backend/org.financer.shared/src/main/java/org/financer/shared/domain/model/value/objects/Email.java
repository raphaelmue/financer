package org.financer.shared.domain.model.value.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.validator.routines.EmailValidator;
import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@Immutable
@Schema(description = "Value object for email")
public final class Email implements Serializable {
    private static final long serialVersionUID = -1402839140672131281L;

    private static final EmailValidator validator = EmailValidator.getInstance(false, true);

    @EqualsAndHashCode.Include
    @Column(name = "email", length = 128, nullable = false)
    @Schema(description = "Email address", required = true, example = "test@gmail.com")
    private String emailAddress;

    public Email() {
    }

    public Email(String emailAddress) {
        if (!validator.isValid(emailAddress)) {
            throw new IllegalArgumentException("The given email address ('" + emailAddress + "') is not valid!");
        }
        this.emailAddress = emailAddress;
    }
}

