package de.raphaelmuesseler.financer.shared.model;

public class User {
    private int id;
    private String email, password, salt, name, surname;

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
