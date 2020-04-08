package org.financer.shared.domain.model.value.objects;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Immutable
public class Gender implements Serializable {
    private static final long serialVersionUID = -860866161803817820L;

    public enum Values {
        MALE("male"),
        FEMALE("female"),
        NOT_SPECIFIED("notSpecified");

        private final String name;

        Values(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Values getGenderByName(String name) {
            for (Values gender : values()) {
                if (gender.getName().equals(name)) {
                    return gender;
                }
            }
            return null;
        }

    }

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Values gender;

    public Gender() {
    }

    public Gender(Values gender) {
        this.gender = gender;
    }

    public Gender(String genderName) {
        this.gender = Values.getGenderByName(genderName);
    }

    public Values getGender() {
        return gender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gender gender1 = (Gender) o;
        return gender == gender1.gender;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gender);
    }

    @Override
    public String toString() {
        return "Gender [" +
                "gender=" + gender +
                ']';
    }


}
