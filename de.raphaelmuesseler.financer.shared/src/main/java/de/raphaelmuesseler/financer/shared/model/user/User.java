package de.raphaelmuesseler.financer.shared.model.user;

import de.raphaelmuesseler.financer.shared.model.db.DatabaseSettings;
import de.raphaelmuesseler.financer.shared.model.db.DatabaseUser;

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

    private final Settings settings;

    public User(String email, String password, String salt, String name, String surname, LocalDate birthDate, Gender gender) {
        this.setEmail(email);
        this.setPassword(password);
        this.setSalt(salt);
        this.setName(name);
        this.setSurname(surname);
        this.setBirthDate(birthDate);
        this.setGenderName(gender.getName());

        this.settings = new UserSettings();
        for (DatabaseSettings databaseSettings : this.getDatabaseSettings()) {
            this.settings.setValueByProperty(Settings.Property.getPropertyByName(databaseSettings.getProperty()), databaseSettings.getValue());
        }
    }

    public Settings getSettings() {
        return this.settings;
    }

    public Gender getGender() {
        return Gender.getGenderByName(this.getGenderName());
    }

    public void setGender(Gender gender) {
        this.setGenderName(gender.getName());
    }
}
