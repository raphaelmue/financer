package org.financer.shared.domain.model.value.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.financer.shared.exceptions.EnumNotFoundException;
import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.io.Serializable;
import java.util.List;

@Data
@Embeddable
@Immutable
@Schema(description = "Value object for gender")
public class Gender implements Serializable {
    private static final long serialVersionUID = -860866161803817820L;

    @Schema(name = "GenderEnum", description = "Values that can be applied to the gender")
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
            throw new EnumNotFoundException(Values.class, name);
        }

    }

    @EqualsAndHashCode.Include
    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    @Schema(description = "Gender", required = true, enumAsRef = true, example = "FEMALE")
    private Values gender;

    public Gender() {
    }

    public Gender(Values gender) {
        this.gender = gender;
    }

    public Gender(String genderName) {
        this.gender = Values.getGenderByName(genderName);
    }

    public static List<Gender> getAll() {
        return List.of(new Gender(Values.MALE), new Gender(Values.FEMALE), new Gender(Values.NOT_SPECIFIED));
    }

}
