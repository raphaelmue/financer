package de.raphaelmuesseler.financer.shared.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.raphaelmuesseler.financer.shared.model.db.DatabaseObject;
import de.raphaelmuesseler.financer.shared.model.db.DatabaseUser;
import de.raphaelmuesseler.financer.shared.model.db.Token;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true, value = { "birthDateAsLocalDate" })
public class User extends DatabaseUser {
    private Token token;

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
        }
        return this;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public LocalDate getBirthDateAsLocalDate() {
        return LocalDate.parse(super.getBirthDate());
    }

    public void setBirthDate(LocalDate birthDate) {
        super.setBirthDate(birthDate.toString());
    }
}
