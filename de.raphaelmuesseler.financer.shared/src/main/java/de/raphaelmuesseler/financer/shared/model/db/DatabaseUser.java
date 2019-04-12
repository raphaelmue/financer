package de.raphaelmuesseler.financer.shared.model.db;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

public class DatabaseUser implements Serializable {
    private static final long serialVersionUID = 8551108621522985674L;

    private int id;
    private String email;
    private String password;
    private String salt;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String genderName;
    private Set<DatabaseCategory> categories;
    private Set<DatabaseSettings> databaseSettings;
    private Set<DatabaseToken> tokens;

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

    protected String getGenderName() {
        return genderName;
    }

    protected void setGenderName(String gender) {
        this.genderName = gender;
    }

    public Set<DatabaseCategory> getCategories() {
        return categories;
    }

    public void setCategories(Set<DatabaseCategory> categories) {
        this.categories = categories;
    }

    protected Set<DatabaseSettings> getDatabaseSettings() {
        return databaseSettings;
    }

    protected void setDatabaseSettings(Set<DatabaseSettings> settings) {
        this.databaseSettings = settings;
    }

    public Set<DatabaseToken> getTokens() {
        return tokens;
    }

    public void setTokens(Set<DatabaseToken> tokens) {
        this.tokens = tokens;
    }
}
