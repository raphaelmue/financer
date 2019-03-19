package de.raphaelmuesseler.financer.shared.model.db;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DatabaseUser implements DatabaseObject, Serializable {
    private static final long serialVersionUID = 8551108621522985674L;
    private int id;
    private String email, password, salt, name, surname, gender;
    @SerializedName("birthdate")
    private String birthDate;

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

    @Override
    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getSalt() {
        return salt;
    }

    public String getSurname() {
        return surname;
    }

    public String getFullName() {
        return this.name + " " + this.surname;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
