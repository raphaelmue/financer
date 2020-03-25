package org.financer.shared.domain.model.value.objects;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Immutable
public final class Name {

    @Column(name = "name", length = 64)
    private final String firstName;

    @Column(name = "surname", length = 64)
    private final String surname;

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