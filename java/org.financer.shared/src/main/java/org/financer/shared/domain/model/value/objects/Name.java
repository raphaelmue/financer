package org.financer.shared.domain.model.value.objects;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Immutable
public final class Name implements Serializable {
    private static final long serialVersionUID = -249456135411252935L;

    @Column(name = "name", length = 64, nullable = false)
    private String firstName;

    @Column(name = "surname", length = 64, nullable = false)
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
    public String toString() {
        return this.firstName + " " + this.surname;
    }
}