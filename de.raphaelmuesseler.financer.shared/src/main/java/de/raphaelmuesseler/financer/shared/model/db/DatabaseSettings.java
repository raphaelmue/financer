package de.raphaelmuesseler.financer.shared.model.db;

public class DatabaseSettings {
    private int id;
    private DatabaseUser user;
    private String property;
    private String value;

    public int getId() {
        return id;
    }

    public DatabaseUser getUser() {
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

    public void setUser(DatabaseUser user) {
        this.user = user;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
