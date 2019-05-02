package de.raphaelmuesseler.financer.shared.model.db;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

public class UserDAO implements Serializable, DataAccessObject {
    private static final long serialVersionUID = 8551108621522985674L;

    private int id;
    private String email;
    private String password;
    private String salt;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String genderName;
    private Set<CategoryDAO> categories;
    private Set<SettingsDAO> databaseSettings;
    private Set<TokenDAO> tokens;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getGenderName() {
        return genderName;
    }

    public void setGenderName(String gender) {
        this.genderName = gender;
    }

    public Set<CategoryDAO> getCategories() {
        return categories;
    }

    public void setCategories(Set<CategoryDAO> categories) {
        this.categories = categories;
    }

    public Set<SettingsDAO> getDatabaseSettings() {
        return databaseSettings;
    }

    public void setDatabaseSettings(Set<SettingsDAO> settings) {
        this.databaseSettings = settings;
    }

    public Set<TokenDAO> getTokens() {
        return tokens;
    }

    public void setTokens(Set<TokenDAO> tokens) {
        this.tokens = tokens;
    }
}
