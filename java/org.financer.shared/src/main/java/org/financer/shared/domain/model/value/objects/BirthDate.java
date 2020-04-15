package org.financer.shared.domain.model.value.objects;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
@Immutable
public class BirthDate implements Serializable {
    private static final long serialVersionUID = -4072522792982525094L;


    @Column(name = "birth_date")
    private LocalDate birthDate;

    public BirthDate() {
    }

    public BirthDate(String birthDateString) {
        this.birthDate = LocalDate.parse(birthDateString);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BirthDate birthDate1 = (BirthDate) o;
        return Objects.equals(birthDate, birthDate1.birthDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(birthDate);
    }

    @Override
    public String toString() {
        return "BirthDate [" +
                "birthDate=" + birthDate +
                ']';
    }


}
