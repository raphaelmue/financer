package org.financer.shared.domain.model.value.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Immutable
@Schema(description = "Value object for users name")
public final class Name implements Serializable {
    private static final long serialVersionUID = -249456135411252935L;

    @Column(name = "name", length = 64, nullable = false)
    @Schema(description = "First name of the user", required = true, example = "John")
    private String firstName;

    @Column(name = "surname", length = 64, nullable = false)
    @Schema(description = "Surname of the user", required = true, example = "Doe")
    private String surname;

    public Name() {
    }

    public Name(String firstName, String surname) {
        this.firstName = firstName;
        this.surname = surname;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Name name = (Name) o;
        return Objects.equals(firstName, name.firstName) &&
                Objects.equals(surname, name.surname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, surname);
    }

    @Override
    public String toString() {
        return this.firstName + " " + this.surname;
    }
}