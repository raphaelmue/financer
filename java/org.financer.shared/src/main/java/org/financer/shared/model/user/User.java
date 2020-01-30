package org.financer.shared.model.user;

import org.financer.shared.model.db.SettingsEntity;
import org.financer.shared.model.db.TokenEntity;
import org.financer.shared.model.db.UserEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User extends UserEntity {
    private static final long serialVersionUID = 164741396778652317L;

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
    private Token activeToken;

    public User(UserEntity databaseUser) {
        this(databaseUser.getId(),
                databaseUser.getEmail(),
                databaseUser.getPassword(),
                databaseUser.getSalt(),
                databaseUser.getName(),
                databaseUser.getSurname(),
                databaseUser.getBirthDate(),
                Gender.getGenderByName(databaseUser.getGenderName()),
                databaseUser.getVerified());
        if (databaseUser.getTokens() != null) {
            this.setTokens(databaseUser.getTokens());
        }
        if (databaseUser.getDatabaseSettings() != null) {
            this.setDatabaseSettings(databaseUser.getDatabaseSettings());
        }
        if (databaseUser.getCategories() != null) {
            this.setCategories(databaseUser.getCategories());
        }
    }

    public User() {
        this(-1, null, null, null, null, null, null, null, false);
    }

    public User(int id, String email, String password, String salt, String name, String surname, LocalDate birthDate, Gender gender, boolean verified) {
        this.setId(id);
        this.setEmail(email);
        this.setPassword(password);
        this.setSalt(salt);
        this.setName(name);
        this.setSurname(surname);
        this.setBirthDate(birthDate);
        if (gender != null) {
            this.setGenderName(gender.getName());
        } else {
            this.setGenderName(null);
        }
        this.setVerified(verified);

        this.settings = new UserSettings();
    }

    @Override
    public UserEntity toEntity() {
        UserEntity databaseUser = new UserEntity();
        databaseUser.setId(this.getId());
        databaseUser.setEmail(this.getEmail());
        databaseUser.setPassword(this.getPassword());
        databaseUser.setSalt(this.getSalt());
        databaseUser.setName(this.getName());
        databaseUser.setSurname(this.getSurname());
        databaseUser.setBirthDate(this.getBirthDate());
        databaseUser.setGenderName(this.getGender().getName());
        databaseUser.setVerified(this.getVerified());
        databaseUser.setTokens(this.getTokens());
        databaseUser.setDatabaseSettings(this.getDatabaseSettings());
        return databaseUser;
    }

    public String getFullName() {
        return this.getName() + " " + this.getSurname();
    }

    public UserSettings getSettings() {
        if (this.getDatabaseSettings() != null) {
            for (SettingsEntity databaseSettings : super.getDatabaseSettings()) {
                this.settings.setValueByProperty(Settings.Property.getPropertyByName(databaseSettings.getProperty()), databaseSettings.getValue());
            }
        }
        return (UserSettings) this.settings;
    }

    public Gender getGender() {
        return Gender.getGenderByName(this.getGenderName());
    }

    public Token getActiveToken() {
        return activeToken;
    }

    @Override
    public Set<TokenEntity> getTokens() {
        if (super.getTokens() == null) {
            this.setTokens(new HashSet<>());
        }
        return super.getTokens();
    }

    public List<Token> getTokenList() {
        List<Token> result = new ArrayList<>();
        for (TokenEntity tokenEntity : this.getTokens()) {
            result.add(new Token(tokenEntity));
        }
        return result;
    }

    public void setGender(Gender gender) {
        this.setGenderName(gender.getName());
    }

    @Override
    public void setDatabaseSettings(Set<SettingsEntity> settings) {
        if (settings != null) {
            super.setDatabaseSettings(settings);
        }
    }

    public void setActiveToken(Token activeToken) {
        this.activeToken = activeToken;
    }
}
