package de.raphaelmuesseler.financer.shared.model.db;

import de.raphaelmuesseler.financer.shared.model.user.User;

public class DatabaseSettings {
    private int id;
    private User user;
    private String property;
    private String value;

    public int getId() {
        return id;
    }

    public User getUser() {
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

    public void setUser(User user) {
        this.user = user;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
