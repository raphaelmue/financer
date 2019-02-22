package de.raphaelmuesseler.financer.shared.model.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.raphaelmuesseler.financer.shared.model.db.DatabaseObject;
import de.raphaelmuesseler.financer.shared.model.db.DatabaseUser;
import de.raphaelmuesseler.financer.shared.model.db.Token;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true, value = { "birthDateAsLocalDate" })
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

    private Token token;
    private UserSettings settings;

    public User() {
        super();
    }

    public User(String email, String password, String salt, String name, String surname, LocalDate birthDate) {
        super(email, password, salt, name, surname, birthDate.toString());
    }

    @Override
    public DatabaseObject fromDatabaseObject(DatabaseObject databaseObject) {
        if (databaseObject instanceof DatabaseUser) {
            this.setId(databaseObject.getId());
            this.setName(((DatabaseUser) databaseObject).getName());
            this.setSurname(((DatabaseUser) databaseObject).getSurname());
            this.setEmail(((DatabaseUser) databaseObject).getEmail());
            this.setPassword(((DatabaseUser) databaseObject).getPassword());
            this.setSalt(((DatabaseUser) databaseObject).getSalt());
            this.setBirthDate(((DatabaseUser) databaseObject).getBirthDate());
            this.setGender(((DatabaseUser) databaseObject).getGender());
        }
        return this;
    }

    public Token getToken() {
        return token;
    }

    public UserSettings getSettings() {
        if (settings == null) {
            settings = new UserSettings();
        }
        return settings;
    }

    public Gender getGenderObject() {
        return Gender.getGenderByName(super.getGender());
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public void setSettings(UserSettings settings) {
        this.settings = settings;
    }

    public LocalDate getBirthDateAsLocalDate() {
        return LocalDate.parse(super.getBirthDate());
    }

    public void setBirthDate(LocalDate birthDate) {
        super.setBirthDate(birthDate.toString());
    }
}
