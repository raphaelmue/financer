package org.financer.shared.domain.model.value.objects;

public enum Gender {
    MALE("male"),
    FEMALE("female"),
    NOT_SPECIFIED("notSpecified");

    private final String name;

    Gender(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Gender getGenderByName(String name) {
        for (Gender gender : values()) {
            if (gender.getName().equals(name)) {
                return gender;
            }
        }
        return null;
    }

}
