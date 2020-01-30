package org.financer.shared.model.db;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "users")
public class UserEntity implements DataEntity {
    private static final long serialVersionUID = 8551108621522985674L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "salt")
    private String salt;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "birthdate")
    private LocalDate birthDate;

    @Column(name = "gender")
    private String genderName;

    @OneToMany(mappedBy = "user")
    private Set<CategoryEntity> categories;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<SettingsEntity> databaseSettings;

    @OneToMany(mappedBy = "user")
    private Set<TokenEntity> tokens;

    @Column(name = "verified", columnDefinition = "boolean default false")
    private boolean verified;

    @Override
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

    public Set<CategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(Set<CategoryEntity> categories) {
        this.categories = categories;
    }

    public Set<SettingsEntity> getDatabaseSettings() {
        return databaseSettings;
    }

    public void setDatabaseSettings(Set<SettingsEntity> settings) {
        this.databaseSettings = settings;
    }

    public Set<TokenEntity> getTokens() {
        return tokens;
    }

    public void setTokens(Set<TokenEntity> tokens) {
        this.tokens = tokens;
    }

    public boolean getVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
