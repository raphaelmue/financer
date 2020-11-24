package org.financer.shared.domain.model.value.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.financer.shared.domain.model.Formattable;
import org.financer.shared.domain.model.Settings;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@Immutable
@Schema(description = "Value object for users name")
public final class Name implements Serializable, Formattable {
    private static final long serialVersionUID = -249456135411252935L;

    @EqualsAndHashCode.Include
    @Column(name = "name", length = 64, nullable = false)
    @Schema(description = "First name of the user", required = true, example = "John")
    private String firstName;

    @EqualsAndHashCode.Include
    @Column(name = "surname", length = 64, nullable = false)
    @Schema(description = "Surname of the user", required = true, example = "Doe")
    private String surname;

    public Name() {
    }

    public Name(String firstName, String surname) {
        this.firstName = firstName;
        this.surname = surname;
    }

    @Override
    public String format(Settings settings) {
        return this.firstName + " " + this.surname;
    }
}