package de.raphaelmuesseler.financer.shared.model;

import de.raphaelmuesseler.financer.shared.model.db.DatabaseObject;
import de.raphaelmuesseler.financer.shared.model.db.DatabaseUser;

import java.sql.Date;
import java.time.LocalDate;

public class User extends DatabaseUser {
    private String token = "";

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
            this.setBirthdate(((DatabaseUser) databaseObject).getBirthdate());
        }
        return this;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDate getBirthDateAsLocalDate() {
        return LocalDate.parse(super.getBirthdate());
    }

    public void setBirthDate(LocalDate birthDate) {
        super.setBirthdate(birthDate.toString());
    }
}
