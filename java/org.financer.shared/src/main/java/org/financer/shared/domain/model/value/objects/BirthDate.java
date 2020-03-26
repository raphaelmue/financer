package org.financer.shared.domain.model.value.objects;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@Immutable
public class BirthDate implements Serializable {
    private static final long serialVersionUID = -4072522792982525094L;


    @Column(name = "birth_date")
    private LocalDate birthDate;

    public BirthDate() {
    }

    public BirthDate(LocalDate birthDate) {
        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birth date cannot be in the future.");
        }
        this.birthDate = birthDate;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }
}
