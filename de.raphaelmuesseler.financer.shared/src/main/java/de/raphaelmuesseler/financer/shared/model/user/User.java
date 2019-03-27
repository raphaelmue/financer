package de.raphaelmuesseler.financer.shared.model.user;

import de.raphaelmuesseler.financer.shared.model.db.DatabaseUser;
import de.raphaelmuesseler.financer.shared.model.db.Token;

import java.time.LocalDate;

public class User extends DatabaseUser {

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

    public User() {
        super();
    }

    public User(String email, String password, String salt, String name, String surname, LocalDate birthDate, Gender gender) {
        super(email, password, salt, name, surname, birthDate.toString(), gender.getName());
    }
}
