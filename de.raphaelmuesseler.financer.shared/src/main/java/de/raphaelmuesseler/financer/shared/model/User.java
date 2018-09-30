package de.raphaelmuesseler.financer.shared.model;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 4469731095492349222L;
    private int id;
    private String email, password, salt, name, surname;

    public User(String email, String password, String salt, String name, String surname) {
        this.email = email;
        this.password = password;
        this.salt = salt;
        this.name = name;
        this.surname = surname;
    }

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
}
