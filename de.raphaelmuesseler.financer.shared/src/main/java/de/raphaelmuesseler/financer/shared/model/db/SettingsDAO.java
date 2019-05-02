package de.raphaelmuesseler.financer.shared.model.db;

public class SettingsDAO {
    private int id;
    private UserDAO user;
    private String property;
    private String value;

    public int getId() {
        return id;
    }

    public UserDAO getUser() {
        return user;
    }

    public String getProperty() {
        return property;
    }

    public String getValue() {
        return value;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUser(UserDAO user) {
        this.user = user;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
