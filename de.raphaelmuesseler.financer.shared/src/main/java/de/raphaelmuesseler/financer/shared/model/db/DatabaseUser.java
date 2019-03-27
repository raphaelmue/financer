package de.raphaelmuesseler.financer.shared.model.db;

import java.io.Serializable;
import java.util.Set;

public class DatabaseUser implements Serializable {
    private static final long serialVersionUID = 8551108621522985674L;

    private int id;
    private String email;
    private String password;
    private String salt;
    private String name;
    private String surname;
    private String birthDate;
    private String gender;
    private Set<DatabaseCategory> categories;
    private Set<DatabaseSettings> settings;
    private Set<DatabaseToken> tokens;

    public DatabaseUser() {

    }

    public DatabaseUser(String email, String password, String salt, String name, String surname, String birthDate, String gender) {
        this.email = email;
        this.password = password;
        this.salt = salt;
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.gender = gender;
    }

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

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Set<DatabaseCategory> getCategories() {
        return categories;
    }

    public void setCategories(Set<DatabaseCategory> categories) {
        this.categories = categories;
    }

    public Set<DatabaseSettings> getSettings() {
        return settings;
    }

    public void setSettings(Set<DatabaseSettings> settings) {
        this.settings = settings;
    }

    public Set<DatabaseToken> getTokens() {
        return tokens;
    }

    public void setTokens(Set<DatabaseToken> tokens) {
        this.tokens = tokens;
    }
}
